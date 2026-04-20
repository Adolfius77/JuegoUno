package fabricas;

import Server.ServerProxy;
import broker.Broker;
import interfaces.ISerializador;

import java.net.Socket;

public class ServerProxyFactory {
    public static ServerProxy crearManjadorCliente(Broker  broker, Socket clienteSocket, ISerializador serializador){
        return new ServerProxy(broker,clienteSocket,serializador);
    }
}
