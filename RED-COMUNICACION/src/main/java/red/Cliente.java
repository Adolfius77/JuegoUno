package red;

import dtos.MensajeRegistroDTO;
import factorys.ClienteHiloFactory;
import factorys.SocketFactory;
import factorys.StreamFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            Socket socket = SocketFactory.crearSocket("127.0.0.1", 5000);

            //entrada de datos
            ObjectInputStream in = StreamFactory.crearInputStream(socket);
            //salida de datos
            ObjectOutputStream out = StreamFactory.crearOutputStream(socket);

            System.out.println("Introduce tu nombre de usuario: ");
            String nombre = scanner.nextLine();

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