package Launcher;

import java.util.prefs.Preferences;

/**
 * A que servidor se conecta el juego.
 *
 * Hasta ahora la unica forma de apuntar a otra maquina era arrancar con
 * -Dservidor.ip=..., que no sirve para alguien que solo abre el juego. Esto
 * permite escribirla, y la recuerda para la proxima vez.
 *
 * Analizar el texto esta separado de preguntarlo y de guardarlo: asi se puede
 * probar sin pantalla ni red, igual que se hizo con ConfiguracionBot.
 */
public record DireccionServidor(String maquina, int puerto) {

    public static final String MAQUINA_POR_DEFECTO = "localhost";
    public static final int PUERTO_POR_DEFECTO = 8080;

    /** Claves que ya entiende LectorConfiguracion. */
    public static final String CLAVE_IP = "servidor.ip";
    public static final String CLAVE_PUERTO = "servidor.puerto";

    private static final String RECUERDO = "ultimoServidor";

    public DireccionServidor {
        if (maquina == null || maquina.isBlank()) {
            throw new IllegalArgumentException("La maquina es obligatoria.");
        }
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("Puerto fuera de rango: " + puerto);
        }
        maquina = maquina.trim();
    }

    public static DireccionServidor porDefecto() {
        return new DireccionServidor(MAQUINA_POR_DEFECTO, PUERTO_POR_DEFECTO);
    }

    /**
     * Entiende "maquina" o "maquina:puerto". Sin puerto se usa el 8080.
     *
     * @return null si el texto no vale, para que quien pregunta pueda volver a
     *         preguntar en vez de reventar.
     */
    public static DireccionServidor parsear(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpio = texto.trim();

        // Se admite pegar una direccion con esquema delante, que es un error
        // facil de cometer al copiarla de algun sitio.
        if (limpio.startsWith("//")) {
            limpio = limpio.substring(2);
        }

        int separador = limpio.lastIndexOf(':');
        if (separador < 0) {
            return new DireccionServidor(limpio, PUERTO_POR_DEFECTO);
        }

        String maquina = limpio.substring(0, separador).trim();
        String puertoTexto = limpio.substring(separador + 1).trim();
        if (maquina.isEmpty() || puertoTexto.isEmpty()) {
            return null;
        }
        try {
            int puerto = Integer.parseInt(puertoTexto);
            if (puerto < 1 || puerto > 65535) {
                return null;
            }
            return new DireccionServidor(maquina, puerto);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * La direccion que venga dada por fuera, o null si no hay ninguna.
     *
     * Se mira antes de preguntar nada: si el juego se arranca desde un script o
     * con -Dservidor.ip, no debe salir ningun dialogo o el script se quedaria
     * esperando para siempre a que alguien pulse un boton.
     */
    public static DireccionServidor deLosArgumentos(String[] args) {
        String delSistema = System.getProperty(CLAVE_IP);
        if (delSistema != null && !delSistema.isBlank()) {
            String puerto = System.getProperty(CLAVE_PUERTO);
            DireccionServidor conPuerto = parsear(
                    puerto != null && !puerto.isBlank() ? delSistema + ":" + puerto : delSistema);
            return conPuerto != null ? conPuerto : parsear(delSistema);
        }
        if (args != null && args.length > 0) {
            return parsear(args[0]);
        }
        return null;
    }

    /** La ultima que se uso en esta maquina, o la de por defecto. */
    public static DireccionServidor recordada() {
        try {
            String guardada = preferencias().get(RECUERDO, null);
            DireccionServidor direccion = parsear(guardada);
            return direccion != null ? direccion : porDefecto();
        } catch (Exception e) {
            // Las preferencias pueden no estar disponibles; no es motivo para
            // no dejar jugar.
            return porDefecto();
        }
    }

    public void recordar() {
        try {
            preferencias().put(RECUERDO, toString());
        } catch (Exception e) {
            System.out.println("[Configuracion] No se pudo recordar el servidor: " + e.getMessage());
        }
    }

    /**
     * Deja la direccion donde la red la va a buscar.
     *
     * LectorConfiguracion ya da prioridad a las propiedades de sistema sobre el
     * config.properties del jar, asi que con esto ClienteProxy.conectar() la
     * recoge sin tocar nada de la capa de red.
     */
    public void aplicar() {
        System.setProperty(CLAVE_IP, maquina);
        System.setProperty(CLAVE_PUERTO, String.valueOf(puerto));
    }

    public boolean esLocal() {
        return MAQUINA_POR_DEFECTO.equalsIgnoreCase(maquina)
                || "127.0.0.1".equals(maquina)
                || "::1".equals(maquina);
    }

    @Override
    public String toString() {
        return maquina + ":" + puerto;
    }

    private static Preferences preferencias() {
        return Preferences.userNodeForPackage(DireccionServidor.class);
    }
}
