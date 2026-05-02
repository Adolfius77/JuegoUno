package factorys;

import red.ClienteHilo;
import Controladores.ServerController;

import java.io.ObjectInputStream;

public class ClienteHiloFactory {
    public static ClienteHilo crearHilo(ObjectInputStream in, ServerController gestor) {
        return new ClienteHilo(in,gestor);
    }
}
