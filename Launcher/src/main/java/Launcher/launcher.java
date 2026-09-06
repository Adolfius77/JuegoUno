package Launcher;

import broker.Broker;
import cliente.ClienteProxy;
import controlador.Factorys.MVCFactory;
import serealizador.serializador;

import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;

/**
 * Clase principal que arranca la aplicación del cliente.
 *
 * Se puede decir a que servidor conectarse de tres formas, de mas fuerte a mas
 * debil: con -Dservidor.ip, como primer argumento, o escribiendola en la
 * ventanita que sale al abrir el juego.
 *
 *   java -jar JuegoUno.jar
 *   java -jar JuegoUno.jar 203.0.113.45
 *   java -jar JuegoUno.jar 203.0.113.45:9000
 */
public class launcher {

    public static void main(String[] args) {

        try {
            // Antes de crear cualquier ventana: sin esto Swing usa Metal.
            vista.tema.Tema.instalar();

            // Punto de composicion del cliente: aqui se arma el proxy con su
            // serializador y el bus donde publicara lo que llegue del servidor.
            ClienteProxy proxy = new ClienteProxy();
            serializador sere = new serializador();

            proxy.setSerializador(sere);
            proxy.setBroker(new Broker());

            if (!conectar(proxy, args)) {
                return;
            }

            // La fabrica es el unico lugar que arma vista + controlador; a
            // partir de aqui la capa de presentacion no conoce el proxy.
            MVCFactory.configurar(proxy);
            java.awt.EventQueue.invokeLater(MVCFactory::abrirMenuPrincipal);

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Conecta, preguntando la direccion si hace falta.
     *
     * @return false si el jugador desistio, para no abrir el menu sin conexion.
     */
    private static boolean conectar(ClienteProxy proxy, String[] args) {
        DireccionServidor dada = DireccionServidor.deLosArgumentos(args);

        // Si la direccion viene por linea de comandos no se pregunta nada: un
        // script o una prueba automatica se quedarian colgados esperando a que
        // alguien pulsara un boton. Lo mismo sin pantalla.
        if (dada != null || GraphicsEnvironment.isHeadless()) {
            DireccionServidor direccion = dada != null ? dada : DireccionServidor.recordada();
            direccion.aplicar();
            try {
                proxy.conectar();
                return true;
            } catch (Exception e) {
                System.err.println("No se pudo conectar con " + direccion + ": " + e.getMessage());
                return false;
            }
        }

        return conectarPreguntando(proxy);
    }

    /**
     * Pide la direccion y reintenta mientras haga falta.
     *
     * Reintentar es el motivo de que esto exista: quien acaba de teclear una
     * direccion se equivoca, o el servidor todavia no esta encendido, y cerrar
     * el juego con un error no le deja hacer nada al respecto.
     */
    private static boolean conectarPreguntando(ClienteProxy proxy) {
        DireccionServidor sugerida = DireccionServidor.recordada();
        String aviso = "";

        while (true) {
            String respuesta = JOptionPane.showInputDialog(null,
                    aviso + "Direccion del servidor:\n"
                            + "(deja localhost si el servidor corre en esta misma maquina)",
                    sugerida.toString());

            if (respuesta == null) {
                return false;   // le dio a cancelar
            }

            DireccionServidor direccion = DireccionServidor.parsear(respuesta);
            if (direccion == null) {
                aviso = "\"" + respuesta.trim() + "\" no vale como direccion.\n"
                        + "Escribe algo como 203.0.113.45 o 203.0.113.45:8080\n\n";
                continue;
            }

            direccion.aplicar();
            try {
                proxy.conectar();
                direccion.recordar();
                System.out.println("[Cliente] Conectado a " + direccion);
                return true;
            } catch (Exception e) {
                sugerida = direccion;
                aviso = "No se pudo conectar con " + direccion + ".\n"
                        + (direccion.esLocal()
                            ? "¿Esta encendido el servidor en esta maquina?\n\n"
                            : "Comprueba la direccion y que el servidor este encendido.\n\n");
            }
        }
    }
}
