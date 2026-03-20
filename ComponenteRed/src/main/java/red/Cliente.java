package red;

import factorys.ClienteHiloFactory;
import factorys.SocketFactory;
import factorys.StreamFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            Socket socket = new Socket("127.0.0.1", 5000);

            //datos entrada
            DataInputStream in = StreamFactory.crearInputStream(socket);
            //datos de salida
            DataOutputStream out = StreamFactory.crearOutputStream(socket);

            String mensaje = in.readUTF();
            System.out.println(mensaje);

            String nombre = scanner.next();
            out.writeUTF(nombre);

            ClienteHilo hilo = ClienteHiloFactory.crearHilo(out, in);
            hilo.start();
            hilo.join();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}