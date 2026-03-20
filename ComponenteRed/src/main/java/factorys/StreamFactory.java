package factorys;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class StreamFactory {
    public static DataInputStream crearInputStream(Socket socket) throws IOException {
        return new DataInputStream(socket.getInputStream());
    }
    public static DataOutputStream crearOutputStream(Socket socket) throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }
}
