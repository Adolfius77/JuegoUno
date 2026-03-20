package red;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    public static void main(String[] args) {
        try{
            ServerSocket server = new ServerSocket(5000);
            Socket socket;

            System.out.println("Servidor iniciado");
            while(true){
                socket = server.accept();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());


                out.writeUTF("Escribe tu nombre:");
                String nombre = in.readUTF();

                ServidorHilo hilo = new ServidorHilo(in, out, nombre);
                hilo.start();

                System.out.println("Creada la conexion con el cliente " + nombre);

            }

        }catch (IOException ex){
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}