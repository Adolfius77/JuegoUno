package red;

import broker.GameHandler;
import Interfacez.IProxyFactory;
import Interfacez.ISerializador;
import broker.Broker;
import broker.ManejadorPartidaServidor;
import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.fabricas.MazoClasicoFactory;
import fabricas.ServerProxyFactory;
import serealizador.serializador;

public class servidorMain {
    public static void main(String[] args) {
        // Componentes base
        ISerializador miSerializador = new serializador();
        IProxyFactory proxyFactory = new ServerProxyFactory();

        Broker broker = new Broker(9090, miSerializador, proxyFactory);

        // GameHandler registra todas las suscripciones del juego
        new GameHandler(broker);

        // Manejador de partida
        new ManejadorPartidaServidor(
            broker,
            new CartaFactory(),
            new MazoClasicoFactory(),
            EstadoFactory.crearEstadoEsperando()
        );

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