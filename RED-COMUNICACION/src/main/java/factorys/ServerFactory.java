package factorys;

import red.Servidor;
import serealizador.serializador;

public class ServerFactory {
    public static Servidor fabricarServidor(int puerto, String ip, serializador serializador) {
        return new Servidor(puerto, ip, serializador);
    }
}
