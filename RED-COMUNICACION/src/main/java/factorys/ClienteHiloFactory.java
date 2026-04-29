package factorys;

import red.ClienteHilo;
import red.GestorPartida;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClienteHiloFactory {
    public static ClienteHilo crearHilo(ObjectInputStream in, GestorPartida gestor) throws IOException {
        return new ClienteHilo(in,gestor);
    }
}
