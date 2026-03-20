package Red;

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

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String mensaje = in.readUTF();
            System.out.println(mensaje);

            String nombre = scanner.next();
            out.writeUTF(nombre);

            ClienteHilo hilo = new ClienteHilo(out,in);
            hilo.start();
            hilo.join();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
