package red;

import interfaces.IReceptorMensajes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private final int puerto;
    private final IReceptorMensajes receptor;
    private boolean escuchando;

    public Servidor(int puerto, IReceptorMensajes receptor) {
        this.puerto = puerto;
        this.receptor = receptor;
        this.escuchando = true;
    }
    public void iniciar() {
        try(ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("servidor iniciado en el puerto: " + puerto);

            while(escuchando) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("[Servidor Red] Nueva conexión aceptada desde: " + socketCliente.getInetAddress().getHostAddress());
                ServidorHilo nuevoHilo = new ServidorHilo(socketCliente, (EnrutadorBroker) this.receptor);
                nuevoHilo.start();
            }
        }catch (IOException e){
            System.err.println("error en el servidor en el puerto: " + puerto);
            e.printStackTrace();
        }
    }
    public void apagar(){
        this.escuchando = false;
        System.out.println("apagando servidor.....");
    }
}