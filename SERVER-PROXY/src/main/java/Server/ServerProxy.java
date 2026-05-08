package red;

import Interfacez.IBroker;
import Interfacez.IProxy;
import Interfacez.ISerializador;
import dtos.MensajeDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerProxy implements IProxy {

    private final Socket socket;
    private final IBroker broker;
    private final ISerializador serializador;
    private String nombreJugador;
    private String canalPersonal;   // canal broker de este cliente
    private String codigoSala;      // se asigna cuando entra a una sala

    public ServerProxy(IBroker broker, Socket socket, ISerializador serializador) {
        this.broker = broker;
        this.socket = socket;
        this.serializador = serializador;
    }

    @Override
    public void run() {
        try {
            InputStream entrada = socket.getInputStream();
            OutputStream salida = socket.getOutputStream();

            BufferedReader lector = new BufferedReader(
                    new InputStreamReader(entrada, StandardCharsets.UTF_8));

            // Canal único para este cliente (respuestas del servidor hacia él)
            canalPersonal = "CANAL_" + socket.getPort();

            // Suscribirse para recibir mensajes destinados a este cliente
            broker.subscribirse(canalPersonal, msg -> this.enviarMensaje((MensajeDTO) msg));

            System.out.println("[ServerProxy] Cliente conectado desde "
                    + socket.getInetAddress() + ":" + socket.getPort()
                    + " → canal: " + canalPersonal);

            while (true) {
                String jsonRecibido = lector.readLine();
                if (jsonRecibido == null) {
                    break;
                }

                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);

                if ("SOLICITUD_REGISTRO".equals(mensaje.getTipo())) {
                    this.nombreJugador = (String) mensaje.getDatos().get("nombreJugador");
                    mensaje.getDatos().put("canalRespuesta", canalPersonal);
                    broker.publicar("SOLICITUD_REGISTRO", mensaje);
                    System.out.println("[ServerProxy] Solicitud de registro: " + nombreJugador);

                } else if ("DESCONEXION".equals(mensaje.getTipo())) {
                    System.out.println("[ServerProxy] Desconexión solicitada: " + nombreJugador);
                    broker.eliminarNodo(nombreJugador);
                    break;

                } else {
                    enriquecerYPublicar(mensaje);
                }
            }

        } catch (Exception e) {
            System.out.println("[ServerProxy] Cliente desconectado ("
                    + (nombreJugador != null ? nombreJugador : "sin nombre") + "): " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    /**
     * Agrega al mensaje los datos de contexto que el GameHandler necesita
     * (quién lo envía, su canal y su sala) y lo publica en el broker.
     */
    private void enriquecerYPublicar(MensajeDTO mensaje) {
        if (nombreJugador != null) {
            mensaje.getDatos().put("nombreJugador", nombreJugador);
        }
        if (canalPersonal != null) {
            mensaje.getDatos().put("canalRespuesta", canalPersonal);
        }
        // codigoSala se actualiza localmente cuando el GameHandler confirma la entrada
        if (codigoSala != null) {
            mensaje.getDatos().putIfAbsent("codigoSala", codigoSala);
        }

        broker.publicar(mensaje.getTipo(), mensaje);
        System.out.println("[ServerProxy] Publicado → " + mensaje.getTipo()
                + " | jugador: " + nombreJugador);
    }

    private void desconectar() {
        try {
            if (nombreJugador != null && !nombreJugador.isBlank()) {
                broker.eliminarNodo(nombreJugador);
            }
            socket.close();
        } catch (IOException e) {
            System.out.println("[ServerProxy] Error al cerrar socket: " + e.getMessage());
        }
    }

    /**
     * Asigna el código de sala al proxy (llamado por el GameHandler vía
     * broker).
     */
    public void setCodigoSala(String codigoSala) {
        this.codigoSala = codigoSala;
    }

    @Override
    public void enviarMensaje(MensajeDTO mensaje) {
        try {
            PrintWriter escritor = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            escritor.println(serializador.serealizar(mensaje));
        } catch (Exception e) {
            System.out.println("[ServerProxy] Error enviando mensaje: " + e.getMessage());
        }
    }
}
