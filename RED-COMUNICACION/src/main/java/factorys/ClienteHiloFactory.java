package factorys;

import red.ClienteHilo;

import java.io.*;
import red.GestorPartida;

public class ClienteHiloFactory {
    public static ClienteHilo crearHilo(ObjectInputStream in, GestorPartida gestor) throws IOException {
        return new ClienteHilo(in,gestor);
    }
}
