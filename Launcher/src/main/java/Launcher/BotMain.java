package Launcher;

import broker.Broker;
import cliente.ClienteProxy;
import controlador.bot.BotController;
import controlador.bot.EstrategiaBot;
import controlador.bot.EstrategiaDificil;
import controlador.bot.EstrategiaFacil;
import serealizador.serializador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Arranca un jugador automatico y lo sienta en una sala.
 *
 * Es el mismo punto de composicion que launcher, pero sin interfaz: arma el
 * proxy con su serializador y su bus, y en vez de abrir ventanas engancha un
 * BotController.
 *
 *   java -cp Launcher/target/JuegoUno.jar Launcher.BotMain Robo AB12 dificil
 *   java -cp Launcher/target/JuegoUno.jar Launcher.BotMain Robo crear facil
 *   java -cp Launcher/target/JuegoUno.jar Launcher.BotMain
 *
 * Lo que no venga en los argumentos lo pregunta ConfiguracionBot.
 *
 * El servidor se toma de -Dservidor.ip y -Dservidor.puerto, que ya lee
 * LectorConfiguracion; por omision, el de esta misma maquina.
 */
public class BotMain {

    public static void main(String[] args) {
        if (ConfiguracionBot.pidioAyuda(args)) {
            System.out.println(ConfiguracionBot.USO);
            System.exit(0);
        }

        BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        ConfiguracionBot config = ConfiguracionBot.resolver(args, teclado, System.out);

        EstrategiaBot estrategia = config.esNivelDificil()
                ? new EstrategiaDificil()
                : new EstrategiaFacil();

        BotController bot = null;
        try {
            ClienteProxy proxy = new ClienteProxy();
            proxy.setSerializador(new serializador());
            proxy.setBroker(new Broker());
            proxy.conectar();

            // Los avatares del juego son avatar1..avatar6; el bot se queda con
            // el ultimo para no pelearse con el que elijas tu.
            bot = new BotController(proxy, config.nombre(), config.sala(), "avatar6", estrategia);
            bot.entrar();
            bot.esperarFinal();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("El bot no pudo entrar: " + e.getMessage());
            System.err.println("Comprueba que el servidor este encendido.");
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
