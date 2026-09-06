package Launcher;

import broker.Broker;
import cliente.ClienteProxy;
import controlador.bot.BotController;
import controlador.bot.EstrategiaBot;
import controlador.bot.EstrategiaDificil;
import controlador.bot.EstrategiaFacil;
import serealizador.serializador;

/**
 * Arranca un jugador automatico y lo sienta en una sala.
 *
 * Es el mismo punto de composicion que launcher, pero sin interfaz: arma el
 * proxy con su serializador y su bus, y en vez de abrir ventanas engancha un
 * BotController.
 *
 *   java -cp JuegoUno.jar Launcher.BotMain Robo AB12 dificil
 *   java -cp JuegoUno.jar Launcher.BotMain Robo crear facil
 *
 * El servidor se toma de -Dservidor.ip y -Dservidor.puerto, que ya lee
 * LectorConfiguracion; por omision, el de esta misma maquina.
 */
public class BotMain {

    private static final String USO = """
            Uso: java -cp JuegoUno.jar Launcher.BotMain <nombre> <sala> [facil|dificil]

              <sala>  el codigo de una sala ya creada, o la palabra "crear"
                      para que el bot abra la suya y te diga el codigo.
            """;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println(USO);
            System.exit(2);
        }

        String nombre = args[0];
        String codigoSala = args[1];
        String nivel = args.length > 2 ? args[2].trim().toLowerCase() : "facil";

        EstrategiaBot estrategia = switch (nivel) {
            case "facil" -> new EstrategiaFacil();
            case "dificil" -> new EstrategiaDificil();
            default -> null;
        };
        if (estrategia == null) {
            System.err.println("Nivel desconocido: " + nivel);
            System.err.println(USO);
            System.exit(2);
            return;
        }

        BotController bot = null;
        try {
            ClienteProxy proxy = new ClienteProxy();
            proxy.setSerializador(new serializador());
            proxy.setBroker(new Broker());
            proxy.conectar();

            // Los avatares del juego son avatar1..avatar6; el bot se queda con
            // el ultimo para no pelearse con el que elijas tu.
            bot = new BotController(proxy, nombre, codigoSala, "avatar6", estrategia);
            bot.entrar();
            bot.esperarFinal();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("El bot no pudo entrar: " + e.getMessage());
            System.exit(1);
        } finally {
            if (bot != null) {
                bot.cerrar();
            }
        }

        // El proxy corre en un hilo no demonio que sigue leyendo del socket.
        System.exit(0);
    }
}
