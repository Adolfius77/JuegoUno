package Launcher;

import broker.Broker;
import cliente.ClienteProxy;
import controlador.Factorys.MVCFactory;
import serealizador.serializador;

/**
 * Clase principal que arranca la aplicación del cliente.
 */
public class launcher {

    public static void main(String[] args) {

        try {

            // Punto de composicion del cliente: aqui se arma el proxy con su
            // serializador y el bus donde publicara lo que llegue del servidor.
            ClienteProxy proxy = new ClienteProxy();
            serializador sere = new serializador();

            proxy.setSerializador(sere);
            proxy.setBroker(new Broker());
            proxy.conectar();

            // La fabrica es el unico lugar que arma vista + controlador; a
            // partir de aqui la capa de presentacion no conoce el proxy.
            MVCFactory.configurar(proxy);
            java.awt.EventQueue.invokeLater(MVCFactory::abrirMenuPrincipal);

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
