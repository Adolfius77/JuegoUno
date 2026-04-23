package red;

import Interfacez.IBroker;
import Server.ServerProxy;
import fabricas.ServerProxyFactory;
import interfaces.ISerializador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorRed {
    private int puerto;
    private IBroker broker;
    private ISerializador serializador;

    public ServidorRed(int puerto,  IBroker broker,  ISerializador serializador) {
        this.puerto = puerto;
        this.broker = broker;
        this.serializador = serializador;
    }
    public void iniciarServidor(){
        new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(puerto)){
                System.out.println("servidor iniciado escuchando al puerto: " + puerto);
                while(true){
                    Socket clienteSocket = serverSocket.accept();
                    System.out.println("nuevo jugador conectado: " + clienteSocket.getInetAddress());
                    ServerProxy manejador = ServerProxyFactory.crearManjadorCliente(broker, clienteSocket,serializador);
                    new Thread(manejador).start();
                }

            }catch(IOException e){
                System.out.println("error en la red: " + e.getMessage());
            }
        });
    }
}

