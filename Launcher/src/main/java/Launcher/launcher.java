package Launcher;



import cliente.ClienteProxy;
import controlador.LobbyController;
import serealizador.serializador;
import vista.MenuPrincipal;

/**
 * Clase principal que arranca la aplicación del cliente.
 */
public class launcher {

    public static void main(String[] args) {

        try {

            ClienteProxy proxy = ClienteProxy.getInstance();
            serializador sere = new serializador();
            proxy.setSerializador(sere);
            proxy.conectar();

            LobbyController controlador = new LobbyController(proxy);

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {

                    MenuPrincipal menu = new MenuPrincipal(controlador);
                    controlador.setVista(menu);
                    menu.setVisible(true);
                }
            });

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
