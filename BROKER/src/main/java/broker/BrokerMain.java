package broker;

import Interfacez.IProxyFactory;
import Interfacez.ISerializador;
import fabricas.ServerProxyFactory;

import serealizador.serializador;

public class BrokerMain {
    public static void main(String[] args) {
        ISerializador miSerializador = new serializador();
        IProxyFactory proxyFactory = new ServerProxyFactory();

        Broker broker = new Broker(9090, miSerializador, proxyFactory);

        // Registrar el GameHandler — este maneja toda la lógica del juego
        new GameHandler(broker);

        broker.iniciarServidor();

        System.out.println("======================================");
        System.out.println("   BROKER DE UNO - ITSON ACTIVADO    ");
        System.out.println("   Escuchando en puerto: 9090        ");
        System.out.println("======================================");

        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                System.out.println("Broker detenido.");
                break;
            }
        }
    }
}