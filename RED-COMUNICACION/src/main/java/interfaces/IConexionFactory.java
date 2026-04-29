package interfaces;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public interface IConexionFactory {
    Socket crearSocket(String ip, int puerto) throws IOException;
    ObjectOutputStream crearOutputStream(Socket socket) throws IOException;
    ObjectInputStream crearInputStream(Socket socket) throws IOException;
    Thread crearHiloCliente(ObjectInputStream in, IGestorPartida gestor);
}