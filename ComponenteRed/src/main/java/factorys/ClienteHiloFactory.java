package factorys;

import red.ClienteHilo;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ClienteHiloFactory {
    public static ClienteHilo crearHilo(DataOutputStream out, DataInputStream in){
        return new ClienteHilo(out,in);
    }
}
