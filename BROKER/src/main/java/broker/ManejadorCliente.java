package broker;

import dtos.MensajeDTO;

import java.io.ObjectInputStream;
import java.net.Socket;
//esta clase servira para notificar al broker cuando se registra  y se desconecta tambien recibir mensajes del broker
public class ManejadorCliente implements Runnable {
    private Socket socket;
    private Broker broker;
    private String nombreJugador;

    public ManejadorCliente(Broker broker, Socket socket) {
        this.broker = broker;
        this.socket = socket;
        this.nombreJugador = null;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            System.out.println("escuchando desde: " + socket.getInetAddress());
            while(true){
                Object objetoRecibido = in.readObject();
                if(objetoRecibido instanceof MensajeDTO){
                    MensajeDTO mensaje = (MensajeDTO)objetoRecibido;
                    System.out.println("mensaje recibido: " + mensaje);
                }
            }
        }catch (Exception e) {
            System.out.println("el cliente se a desconectado o hay un error: " + e.getMessage());
        }
    }
}
