package fabricas;

import Interfacez.IBroker;
import Interfacez.ISerializador;
import Server.ServerProxy;

import java.net.Socket;

public class ServerProxyFactory {
    public static ServerProxy crearManjadorCliente(IBroker broker, Socket clienteSocket, ISerializador serializador){
        return new ServerProxy(broker,clienteSocket,serializador);
    }
}
