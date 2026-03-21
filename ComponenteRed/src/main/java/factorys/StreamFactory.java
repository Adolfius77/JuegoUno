package factorys;

import java.io.*;
import java.net.Socket;

public class StreamFactory {
    public static ObjectInputStream crearInputStream(Socket socket) throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }
    public static ObjectOutputStream crearOutputStream(Socket socket) throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }
}
