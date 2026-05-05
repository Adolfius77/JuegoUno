package red;


import Interfacez.IProxyFactory;
import Interfacez.ISerializador;
import Lector.LectorConfiguracion;
import broker.Broker;
import dtos.paqueteRedDTO;
import fabricas.ServerProxyFactory;
import interfaces.IReceptorMensajes;
import serealizador.serializador;

public class servidorMain {

    public static void main(String[] args) throws InterruptedException {
       
        LectorConfiguracion config = new LectorConfiguracion();
        String ip = config.getIpServidor();
        int puerto = config.getPuertoServidor();
        ISerializador serializador = new serializador();
        IProxyFactory proxyFactory = new ServerProxyFactory();
        
        Broker broker = new Broker(puerto , ip, serializador, proxyFactory);
        
        broker.iniciarServidor();
        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("el hilo principal se interrumpio");
        }
        
    }
}
