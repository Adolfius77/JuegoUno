package Server;

import Interfacez.IProxy;
import Interfacez.IBroker;
import dtos.MensajeDTO;
import dtos.MensajeNotificacionDTO;
import dtos.MensajeRegistroDTO;
import interfaces.ISerializador;

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

            System.out.println("Escuchando a jugador desde: " + socket.getInetAddress() + ":" + socket.getPort());

            while(true){
                String jsonRecibido = lector.readLine();
                if(jsonRecibido == null){
                    break;
                }

                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);

                if (mensaje instanceof MensajeRegistroDTO) {
                    MensajeRegistroDTO peticionRegistro = (MensajeRegistroDTO) mensaje;
                    String nombreSolicitado = peticionRegistro.getNombre();

                    boolean exito = broker.registrarJugador(nombreSolicitado, this.socket);

                    if (exito) {
                        this.nombreJugador = nombreSolicitado;
                        MensajeNotificacionDTO respuesta = new MensajeNotificacionDTO("SERVIDOR", false, "EXITO: Registro completado");
                        this.enviarMensaje(respuesta);
                    } else {
                        MensajeNotificacionDTO respuesta = new MensajeNotificacionDTO("SERVIDOR", true, "ERROR: Nombre ocupado o sala llena");
                        this.enviarMensaje(respuesta);
                    }
                }
                else {
                    broker.publicar(mensaje.getTipo(), mensaje);
                    System.out.println("Mensaje recibido y publicado: " + mensaje.getTipo());
                }
            }
        } catch (Exception e) {
            System.out.println("El cliente se ha desconectado o hay un error: " + e.getMessage());
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