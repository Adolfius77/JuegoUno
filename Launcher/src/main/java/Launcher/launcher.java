package Launcher;

import vista.MenuPrincipal;
import java.awt.EventQueue;

/**
 * Clase principal encargada de iniciar la interfaz del cliente.
 * Se ha removido la inicialización del servidor para evitar conflictos 
 * de puertos (BindException) con el ServidorMain y el Broker.
 * 
 * @author emiim
 */
public class launcher {

    public static void main(String[] args) {
        // Registro de actividad en consola para depuración
        System.out.println("[Cliente] Iniciando cargador de interfaces...");
        System.out.println("[Cliente] Construyendo interfaces y controladores...");

        // Iniciamos la GUI en el Event Dispatch Thread (EDT) de Swing
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Instanciamos el menú principal
                    MenuPrincipal menu = new MenuPrincipal();
                    
                    // Centramos y mostramos la ventana
                    menu.setVisible(true);
                    
                    System.out.println("[Cliente] MenuPrincipal desplegado con éxito.");
                } catch (Exception e) {
                    System.err.println("Error crítico al iniciar la interfaz gráfica: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}