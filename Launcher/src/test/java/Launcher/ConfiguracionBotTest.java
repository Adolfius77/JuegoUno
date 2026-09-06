package Launcher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Como decide el bot con que datos arranca.
 *
 * Todo esto se prueba sin consola ni servidor: la entrada es un texto y la
 * salida un buffer en memoria.
 */
class ConfiguracionBotTest {

    /** Lo que quedo escrito en la "pantalla" durante la prueba. */
    private final ByteArrayOutputStream escrito = new ByteArrayOutputStream();

    private ConfiguracionBot resolver(String tecleado, String... args) {
        PrintStream salida = new PrintStream(escrito, true, StandardCharsets.UTF_8);
        BufferedReader entrada = new BufferedReader(new StringReader(tecleado));
        return ConfiguracionBot.resolver(args, entrada, salida);
    }

    private String pantalla() {
        return escrito.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("con los tres argumentos no pregunta nada")
    void conTodosLosArgumentosNoPregunta() {
        ConfiguracionBot config = resolver("", "Robotin", "AB12", "dificil");

        assertEquals("Robotin", config.nombre());
        assertEquals("AB12", config.sala());
        assertEquals("dificil", config.nivel());
        assertTrue(config.esNivelDificil());
        assertEquals("", pantalla(), "no deberia haber escrito ninguna pregunta");
    }

    @Test
    @DisplayName("sin argumentos toma lo que se teclea")
    void sinArgumentosPreguntaTodo() {
        ConfiguracionBot config = resolver("Robotin\nAB12\ndificil\n");

        assertEquals("Robotin", config.nombre());
        assertEquals("AB12", config.sala());
        assertEquals("dificil", config.nivel());
        assertTrue(pantalla().contains("Nombre del bot"));
        assertTrue(pantalla().contains("Codigo de la sala"));
        assertTrue(pantalla().contains("Nivel"));
    }

    @Test
    @DisplayName("dar solo Enter deja los valores por defecto, y la sala la crea el bot")
    void soloEnterUsaLosPorDefecto() {
        ConfiguracionBot config = resolver("\n\n\n");

        assertEquals(ConfiguracionBot.NOMBRE_POR_DEFECTO, config.nombre());
        assertEquals(controlador.bot.BotController.CREAR_SALA, config.sala());
        assertEquals(ConfiguracionBot.NIVEL_POR_DEFECTO, config.nivel());
        assertFalse(config.esNivelDificil());
    }

    @Test
    @DisplayName("solo pregunta lo que falta")
    void preguntaSoloLoQueFalta() {
        ConfiguracionBot config = resolver("dificil\n", "Robotin", "AB12");

        assertEquals("Robotin", config.nombre());
        assertEquals("AB12", config.sala());
        assertEquals("dificil", config.nivel());
        assertFalse(pantalla().contains("Nombre del bot"), "el nombre vino por argumento");
        assertFalse(pantalla().contains("Codigo de la sala"), "la sala vino por argumento");
        assertTrue(pantalla().contains("Nivel"));
    }

    @Test
    @DisplayName("un nivel mal escrito se vuelve a preguntar")
    void nivelInvalidoSeVuelveAPreguntar() {
        ConfiguracionBot config = resolver("dificilisimo\ndificil\n", "Robotin", "AB12");

        assertEquals("dificil", config.nivel());
        assertTrue(pantalla().contains("No conozco el nivel"));
    }

    @Test
    @DisplayName("si se insiste en un nivel que no existe, sigue en facil sin dar vueltas")
    void nivelInvalidoRepetidoAcabaEnFacil() {
        ConfiguracionBot config = resolver("uno\ndos\ntres\ncuatro\n", "Robotin", "AB12");

        assertEquals(ConfiguracionBot.NIVEL_POR_DEFECTO, config.nivel());
    }

    @Test
    @DisplayName("un nivel invalido por argumento avisa pero no tumba el arranque")
    void nivelInvalidoPorArgumentoNoTumbaNada() {
        ConfiguracionBot config = resolver("", "Robotin", "AB12", "imposible");

        assertEquals(ConfiguracionBot.NIVEL_POR_DEFECTO, config.nivel());
        assertTrue(pantalla().contains("No conozco el nivel"));
    }

    @Test
    @DisplayName("sin consola no se queda esperando: tira de los valores por defecto")
    void sinConsolaUsaLosPorDefecto() {
        // Entrada vacia: readLine devuelve null a la primera, como cuando el bot
        // se lanza en segundo plano o con la entrada redirigida.
        ConfiguracionBot config = resolver("");

        assertEquals(ConfiguracionBot.NOMBRE_POR_DEFECTO, config.nombre());
        assertEquals(controlador.bot.BotController.CREAR_SALA, config.sala());
        assertEquals(ConfiguracionBot.NIVEL_POR_DEFECTO, config.nivel());
        assertTrue(pantalla().contains("sin consola"));
    }

    @Test
    @DisplayName("una entrada que revienta se trata como si no hubiera consola")
    void entradaRotaNoRevientaElArranque() {
        PrintStream salida = new PrintStream(escrito, true, StandardCharsets.UTF_8);
        Reader roto = new Reader() {
            @Override
            public int read(char[] buffer, int desde, int cuantos) throws java.io.IOException {
                throw new java.io.IOException("consola rota");
            }

            @Override
            public void close() {
            }
        };

        ConfiguracionBot config = ConfiguracionBot.resolver(
                new String[0], new BufferedReader(roto), salida);

        assertEquals(ConfiguracionBot.NOMBRE_POR_DEFECTO, config.nombre());
        assertEquals(controlador.bot.BotController.CREAR_SALA, config.sala());
    }

    @Test
    @DisplayName("los espacios de mas no cuentan")
    void recortaLosEspacios() {
        ConfiguracionBot config = resolver("  Robotin  \n  AB12  \n  DIFICIL  \n");

        assertEquals("Robotin", config.nombre());
        assertEquals("AB12", config.sala());
        assertEquals("dificil", config.nivel());
    }

    @Test
    @DisplayName("--help y -h se reconocen")
    void reconoceLaPeticionDeAyuda() {
        assertTrue(ConfiguracionBot.pidioAyuda(new String[]{"--help"}));
        assertTrue(ConfiguracionBot.pidioAyuda(new String[]{"-h"}));
        assertTrue(ConfiguracionBot.pidioAyuda(new String[]{"Robo", "ayuda"}));
        assertFalse(ConfiguracionBot.pidioAyuda(new String[]{"Robo", "AB12"}));
        assertFalse(ConfiguracionBot.pidioAyuda(new String[0]));
    }

    @Test
    @DisplayName("el texto de uso nombra la ruta real del jar")
    void elUsoNombraLaRutaReal() {
        assertTrue(ConfiguracionBot.USO.contains("Launcher/target/JuegoUno.jar"),
                "decia solo JuegoUno.jar, que desde la raiz del repositorio no existe");
    }
}
