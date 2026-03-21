package red;

import Entidades.Lobby;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    public static List<ServidorHilo> hilosConectados = new ArrayList<>();
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(5000);
            Lobby lobbyGlobal = new Lobby();

            System.out.println("Servidor de UNO iniciado en puerto 5000...");

            while (true) {
                Socket socket = server.accept();

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                ServidorHilo hilo = new ServidorHilo(in, out, lobbyGlobal);

                hilosConectados.add(hilo);
                hilo.start();

                System.out.println("Nueva conexión añadida a la lista de difusion.");
            }

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, "Error en el servidor", ex);
        }
    }
}