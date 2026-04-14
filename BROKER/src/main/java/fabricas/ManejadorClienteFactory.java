package fabricas;

import broker.Broker;
import broker.ManejadorCliente;

import java.io.IOException;
import java.net.Socket;

public class ManejadorClienteFactory {
    public static ManejadorCliente crearManjadorCliente(Broker  broker, Socket clienteSocket){
        return new ManejadorCliente(broker,clienteSocket);
    }
}
