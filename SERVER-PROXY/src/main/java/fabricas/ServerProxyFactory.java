package fabricas;

import Server.ServerProxy;
import Interfacez.IBroker;
import interfaces.ISerializador;

import java.net.Socket;

public class ServerProxyFactory {
    public static ServerProxy crearManjadorCliente(IBroker broker, Socket clienteSocket, ISerializador serializador){
        return new ServerProxy(broker,clienteSocket,serializador);
    }
}
