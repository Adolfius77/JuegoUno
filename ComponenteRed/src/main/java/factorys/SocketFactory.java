package factorys;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory {
    public static Socket crearSocket(String host, int puerto) throws IOException {
        return new Socket(host,puerto);
    }
}
