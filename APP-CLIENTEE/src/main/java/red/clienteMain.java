package red;

import red.ClienteControlador;
import serealizador.serializador;
import vista.MenuPrincipal;

public class clienteMain {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                ClienteControlador.getInstance()
                        .conectar("localhost", 9090, new serializador());
            } catch (Exception e) {
                System.out.println("No se pudo conectar: " + e.getMessage());
                return;
            }
            new MenuPrincipal().setVisible(true);
        });
    }
}
