package red;

import Entidades.Lobby;
import interfaces.IReceptorMensajes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    public static final List<ServidorHilo> hilosConectados = new CopyOnWriteArrayList<>();
    private final int puerto;
    private final IReceptorMensajes receptor;
    private final Lobby lobby;
    private boolean escuchando;

    public Servidor(int puerto, IReceptorMensajes receptor) {
        this.puerto = puerto;
        this.receptor = receptor;
        this.lobby = new Lobby();
        this.escuchando = true;
    }
    //metodo para iniciar el servidors
    public void iniciar() {
        try(ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("servidor iniciado en el puerto: " + puerto);

            while(escuchando) {
                Socket socketCliente = serverSocket.accept();
                try{
                    System.out.println("[Servidor Red] Nueva conexión aceptada desde: " + socketCliente.getInetAddress().getHostAddress());

                    ObjectOutputStream out = new ObjectOutputStream(socketCliente.getOutputStream());
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(socketCliente.getInputStream());

                    ServidorHilo nuevoHilo = new ServidorHilo(in, out, this.lobby);
                    hilosConectados.add(nuevoHilo);
                    nuevoHilo.start();
                    
                    System.out.println("[Socket] Flujos inicializados para nueva conexión. Esperando registro...");
                }
                catch (IOException e){
                    System.err.println("Error al manejar la conexión del cliente: " + e.getMessage());
                    socketCliente.close();
                }
            }
        }catch (IOException e){
            System.err.println("error en el servidor en el puerto: " + puerto);
            e.printStackTrace();
        }
    }
    //metodo para apagar el servidor
    public void apagar(){
        this.escuchando = false;
        System.out.println("apagando servidor.....");
    }
}
