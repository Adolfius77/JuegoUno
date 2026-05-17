package factorys;

import Interfacez.ISerializador;
import red.Servidor;

public class ServerFactory {
    public static Servidor fabricarServidor(int puerto, String ip, ISerializador serializador) {
        return new Servidor(puerto, ip, serializador);
    }
}
