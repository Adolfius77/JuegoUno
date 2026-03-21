package factorys;

import red.ClienteHilo;
import vista.MenuPrincipal;

import java.io.*;

public class ClienteHiloFactory {
    public static ClienteHilo crearHilo(ObjectInputStream in, ObjectOutputStream out, MenuPrincipal vista) throws IOException {
        return new ClienteHilo(in, out, vista);
    }
}
