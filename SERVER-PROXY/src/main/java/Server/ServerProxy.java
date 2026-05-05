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

public class ServerProxy implements IProxy {
    private Socket socket;
    private IBroker broker;
    private String nombreJugador;
    private ISerializador serializador;

    private InputStream entrada;
    private OutputStream salida;

    public ServerProxy(IBroker broker, Socket socket, ISerializador serializador) {
        this.broker = broker;
        this.socket = socket;
        this.nombreJugador = null;
        this.serializador = serializador;
    }

    @Override
    public void run() {
        try {
            this.entrada = socket.getInputStream();
            this.salida = socket.getOutputStream();
            BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8));

            System.out.println("Escuchando a jugador desde: " + " " + socket.getInetAddress() + ":" + socket.getPort());


            String canalDeRespuesta = "RESPUESTA_REGISTRO_" + socket.getPort();
            broker.subscribirse(canalDeRespuesta, mensajeRespuesta -> {
                this.enviarMensaje(mensajeRespuesta);
            });

            while(true){
                String jsonRecibido = lector.readLine();
                if(jsonRecibido == null){
                    break;
                }

                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);

                if (mensaje instanceof MensajeRegistroDTO) {
                    MensajeRegistroDTO peticionRegistro = (MensajeRegistroDTO) mensaje;
                    this.nombreJugador = peticionRegistro.getNombre();

                    peticionRegistro.setTipo(canalDeRespuesta);

                    broker.publicar("SOLICITUD_REGISTRO", peticionRegistro);
                    System.out.println("Solicitud publicada para: " + this.nombreJugador);
                }
                else if (mensaje instanceof MensajeDesconexionDTO) {
                    MensajeDesconexionDTO desconexion = (MensajeDesconexionDTO) mensaje;
                    broker.eliminarNodo(desconexion.getNombreUsuario());
                    System.out.println("Jugador desconectado: " + desconexion.getNombreUsuario());
                    break;
                }
                else {
                    broker.publicar(mensaje.getTipo(), mensaje);
                    System.out.println("Mensaje recibido y publicado: " + mensaje.getTipo());
                }
            }
        } catch (Exception e) {
            System.out.println("El cliente se ha desconectado o hay un error: " + e.getMessage());
            if (nombreJugador != null && !nombreJugador.isEmpty()) {
                broker.eliminarNodo(nombreJugador);
            }
        } finally {
            if (nombreJugador != null && !nombreJugador.isEmpty()) {
                broker.eliminarNodo(nombreJugador);
            }
        }
    }

    @Override
    public void enviarMensaje(MensajeDTO mensaje) {
        try{
            PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String json =  serializador.serealizar(mensaje);
            escritor.println(json);
        }catch (Exception e){
            System.out.println("Error enviando los datos" + e.getMessage());
            e.printStackTrace();
        }
    }
}