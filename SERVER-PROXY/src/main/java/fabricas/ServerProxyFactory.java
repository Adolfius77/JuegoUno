package fabricas;

import Interfacez.IBroker;
import Interfacez.IProxy;
import Interfacez.IProxyFactory;
import Interfacez.ISerializador;
import Server.ServerProxy;

import java.net.Socket;

public class ServerProxyFactory implements IProxyFactory {

    @Override
    public IProxy createProxy(IBroker broker, Socket socket, ISerializador serializador) {
        return new ServerProxy(broker, socket, serializador);
    }
}
