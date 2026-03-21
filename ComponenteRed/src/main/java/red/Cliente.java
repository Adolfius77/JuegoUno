package red;

import dtos.MensajeRegistroDTO;
import factorys.ClienteHiloFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            Socket socket = new Socket("127.0.0.1", 5000);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Introduce tu nombre de usuario: ");
            String nombre = scanner.next();

            MensajeRegistroDTO registro = new MensajeRegistroDTO(nombre, "avatar_default.png");
            out.writeObject(registro);
            out.flush();

            ClienteHilo hilo = ClienteHiloFactory.crearHilo(in, out, null);
            hilo.start();
            hilo.join();

        } catch (IOException | InterruptedException e) {
            System.err.println("Error en la conexión: " + e.getMessage());
        }

    }

}