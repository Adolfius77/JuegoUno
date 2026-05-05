package Server;

import Interfacez.IBroker;
import Interfacez.IProxy;
import Interfacez.ISerializador;
import dtos.MensajeDTO;
import dtos.MensajeDesconexionDTO;
import dtos.MensajeRegistroDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerProxy implements IProxy, Runnable {

    private Socket socket;
    private IBroker broker;
    private String nombreJugador;
    private ISerializador serializador;

    private BufferedReader lector;
    private PrintWriter escritor;

    public ServerProxy(IBroker broker, Socket socket, ISerializador serializador) {
        this.broker = broker;
        this.socket = socket;
        this.nombreJugador = null;
        this.serializador = serializador;

        try {

            this.escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            this.lector = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error al inicializar los flujos del proxy: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Escuchando a jugador desde: " + socket.getInetAddress() + ":" + socket.getPort());

            String canalDeRespuesta = "RESPUESTA_REGISTRO_" + socket.getPort();
            broker.subscribirse(canalDeRespuesta, mensajeRespuesta -> {
                this.enviarMensaje(mensajeRespuesta);
            });

            while (true) {
                String jsonRecibido = lector.readLine();
                if (jsonRecibido == null) {
                    break;
                }

                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);

                if (mensaje instanceof MensajeRegistroDTO) {
                    MensajeRegistroDTO peticionRegistro = (MensajeRegistroDTO) mensaje;
                    this.nombreJugador = peticionRegistro.getNombre();
                    peticionRegistro.setTipo(canalDeRespuesta);

                    broker.publicar("SOLICITUD_REGISTRO", peticionRegistro);
                    System.out.println("Solicitud publicada para: " + this.nombreJugador);
                } else if (mensaje instanceof MensajeDesconexionDTO) {
                    MensajeDesconexionDTO desconexion = (MensajeDesconexionDTO) mensaje;
                    System.out.println("Jugador solicitó desconexión: " + desconexion.getNombreUsuario());
                    break;
                } else {
                    broker.publicar(mensaje.getTipo(), mensaje);
                    System.out.println("Mensaje recibido y publicado: " + mensaje.getTipo());
                }
            }
        } catch (Exception e) {
            System.out.println("El cliente se ha desconectado o hay un error: " + e.getMessage());
        } finally {
            limpiarYNotificarDesconexion();
        }
    }

    @Override
    public void enviarMensaje(MensajeDTO mensaje) {
        try {
            String json = serializador.serealizar(mensaje);
            escritor.println(json);
        } catch (Exception e) {
            System.out.println("Error enviando los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarYNotificarDesconexion() {

        if (nombreJugador != null && !nombreJugador.isEmpty()) {
            MensajeDesconexionDTO aviso = new MensajeDesconexionDTO();
            aviso.setNombreUsuario(nombreJugador);
            aviso.setTipo("JUGADOR_DESCONECTADO");
            broker.publicar("JUGADOR_DESCONECTADO", aviso);
        }

        try {
            if (lector != null) {
                lector.close();
            }
            if (escritor != null) {
                escritor.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando recursos del proxy: " + e.getMessage());
        }
    }
}
