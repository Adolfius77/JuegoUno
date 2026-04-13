package broker;

import dtos.MensajeDTO;

import java.net.Socket;

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
        try{
            System.out.println("escuchando desde: " + socket.getInetAddress());
            while(true){
                try{

                }
            }
        }catch (Exception e) {

        }
    }
}
