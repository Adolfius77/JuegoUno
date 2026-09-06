package Launcher;

import controlador.bot.BotController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Con que datos arranca el bot: nombre, sala y nivel.
 *
 * Los toma de la linea de comandos, y lo que falte lo pregunta. Antes faltar un
 * dato era el final del programa: imprimia la sintaxis y se moria, que es justo
 * lo que pasa si uno no se acuerda de memoria del orden de los argumentos.
 *
 * Decidir los valores esta separado de arrancar el bot para poder probarlo: se
 * le pasa por donde lee y por donde escribe, asi que en las pruebas la entrada
 * es un texto fijo y no hay que levantar nada.
 */
public record ConfiguracionBot(String nombre, String sala, String nivel) {

    public static final String NOMBRE_POR_DEFECTO = "Robo";
    public static final String NIVEL_POR_DEFECTO = "facil";
    public static final String NIVEL_DIFICIL = "dificil";

    /** Tras estos intentos fallidos seguidos se deja de insistir con el nivel. */
    private static final int INTENTOS = 3;

    public static final String USO = """
            Uso: java -cp Launcher/target/JuegoUno.jar Launcher.BotMain [nombre] [sala] [nivel]

              nombre  como se llamara el bot en la partida   (por defecto: %s)
              sala    el codigo de una sala ya creada, o la palabra "%s"
                      para que el bot abra la suya y te diga el codigo
              nivel   %s o %s                           (por defecto: %s)

            Lo que no pongas, se pregunta. Sin argumentos tambien vale:

              java -cp Launcher/target/JuegoUno.jar Launcher.BotMain
            """.formatted(NOMBRE_POR_DEFECTO, BotController.CREAR_SALA,
            NIVEL_POR_DEFECTO, NIVEL_DIFICIL, NIVEL_POR_DEFECTO);

    public boolean esNivelDificil() {
        return NIVEL_DIFICIL.equals(nivel);
    }

    /** true si el usuario pidio ayuda; entonces no hay nada que resolver. */
    public static boolean pidioAyuda(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg) || "ayuda".equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Completa con preguntas lo que no venga en los argumentos.
     *
     * Si la entrada esta cerrada -- el bot lanzado en segundo plano, o con la
     * entrada redirigida -- readLine devuelve null para siempre. En ese caso se
     * tiran los valores por defecto y se dice cuales, en vez de dar vueltas
     * sobre una entrada que ya no va a traer nada.
     */
    public static ConfiguracionBot resolver(String[] args, BufferedReader entrada, PrintStream salida) {
        String nombre = argumento(args, 0);
        String sala = argumento(args, 1);
        String nivel = argumento(args, 2);

        if (nombre == null) {
            nombre = preguntar(entrada, salida,
                    "Nombre del bot [" + NOMBRE_POR_DEFECTO + "]: ", NOMBRE_POR_DEFECTO);
        }
        if (sala == null) {
            sala = preguntar(entrada, salida,
                    "Codigo de la sala (Enter para que el bot cree una): ",
                    BotController.CREAR_SALA);
        }
        if (nivel == null) {
            nivel = preguntarNivel(entrada, salida);
        }

        return new ConfiguracionBot(nombre, sala, normalizarNivel(nivel, salida));
    }

    // --- Interioridades ----------------------------------------------------

    private static String argumento(String[] args, int indice) {
        if (args == null || indice >= args.length || args[indice] == null || args[indice].isBlank()) {
            return null;
        }
        return args[indice].trim();
    }

    private static String preguntar(BufferedReader entrada, PrintStream salida,
                                    String pregunta, String porDefecto) {
        salida.print(pregunta);
        salida.flush();
        String leido = leer(entrada);

        if (leido == null) {
            salida.println();
            salida.println("(sin consola: uso \"" + porDefecto + "\")");
            return porDefecto;
        }
        return leido.isBlank() ? porDefecto : leido.trim();
    }

    private static String preguntarNivel(BufferedReader entrada, PrintStream salida) {
        for (int intento = 0; intento < INTENTOS; intento++) {
            String respuesta = preguntar(entrada, salida,
                    "Nivel - " + NIVEL_POR_DEFECTO + " o " + NIVEL_DIFICIL
                            + " [" + NIVEL_POR_DEFECTO + "]: ", NIVEL_POR_DEFECTO);
            if (esNivelValido(respuesta)) {
                return respuesta;
            }
            salida.println("No conozco el nivel \"" + respuesta + "\".");
        }
        salida.println("Sigo con " + NIVEL_POR_DEFECTO + ".");
        return NIVEL_POR_DEFECTO;
    }

    private static String leer(BufferedReader entrada) {
        try {
            return entrada.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean esNivelValido(String nivel) {
        String limpio = nivel == null ? "" : nivel.trim().toLowerCase();
        return NIVEL_POR_DEFECTO.equals(limpio) || NIVEL_DIFICIL.equals(limpio);
    }

    /**
     * Un nivel invalido que llego por argumento no tumba el arranque: se avisa
     * y se juega en facil. Preguntarlo aqui no serviria, porque quien lanza con
     * argumentos suele no tener a nadie delante.
     */
    private static String normalizarNivel(String nivel, PrintStream salida) {
        String limpio = nivel == null ? "" : nivel.trim().toLowerCase();
        if (esNivelValido(limpio)) {
            return limpio;
        }
        salida.println("No conozco el nivel \"" + nivel + "\"; juego en "
                + NIVEL_POR_DEFECTO + ".");
        return NIVEL_POR_DEFECTO;
    }
}
