package controlador; // Asegúrate de poner el paquete correcto

import Controladores.ServerController;
import Interfaces.IVista;
import red.ClienteProxy; // Ajusta los imports según tus paquetes
import serealizador.serializador;
import vista.MenuPrincipal;

/**
 * Clase principal que arranca la aplicación del cliente.
 */
public class ClienteMain {

    public static void main(String[] args) {

        try {

            ClienteProxy proxy = ClienteProxy.getInstance();
    
            // IMPORTANTE: Aquí debes inyectar tu serializador (el que convierte a JSON)
            // proxy.setSerializador(new TuClaseSerializadora()); 
            
            serializador sere = new serializador();
            proxy.setSerializador(sere);
            proxy.conectar();

            LobbyController controlador = new LobbyController(proxy);

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    MenuPrincipal menu = new MenuPrincipal(controlador);
                    menu.setVisible(true);
                }
            });

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
