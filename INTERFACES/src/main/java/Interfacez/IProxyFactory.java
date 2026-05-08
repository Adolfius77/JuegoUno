package Interfacez;

import java.net.Socket;

public interface IProxyFactory {
    IProxy createProxy(IBroker broker, Socket socket, ISerializador serializador);
}
