package factorys;

import red.ClienteHilo;

import java.io.*;

public class ClienteHiloFactory {
    public static ClienteHilo crearHilo(ObjectInputStream in) throws IOException {
        return new ClienteHilo(in);
    }
}
