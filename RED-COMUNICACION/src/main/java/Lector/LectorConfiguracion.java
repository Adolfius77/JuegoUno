package Lector;

import java.io.InputStream;
import java.util.Properties;

/**
 * Lee la configuracion de red desde config.properties del classpath.
 *
 * Una propiedad de sistema tiene prioridad sobre el archivo, para poder apuntar
 * a otro servidor sin recompilar:
 *   java -Dservidor.ip=192.168.1.10 -Dservidor.puerto=9000 ...
 *
 * El archivo debe existir en UN solo modulo ejecutable (Launcher para el
 * cliente, SERVER-PROXY para el servidor). Antes habia cuatro copias con el
 * mismo nombre en el classpath y ganaba la del primer jar, en silencio.
 */
public class LectorConfiguracion {

    private static final String ARCHIVO = "config.properties";
    private final Properties propiedades = new Properties();

    public LectorConfiguracion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(ARCHIVO)) {
            if (input == null) {
                System.out.println("[Configuracion] No se encontro " + ARCHIVO + ", se usan los valores por defecto.");
                return;
            }
            propiedades.load(input);
        } catch (Exception e) {
            System.err.println("[Configuracion] Error leyendo " + ARCHIVO + ": " + e.getMessage());
        }
    }

    public String getIpServidor() {
        return leer("servidor.ip", "localhost");
    }

    public int getPuertoServidor() {
        String puerto = leer("servidor.puerto", "8080");
        try {
            return Integer.parseInt(puerto.trim());
        } catch (NumberFormatException e) {
            System.err.println("[Configuracion] Puerto invalido '" + puerto + "', se usa 8080.");
            return 8080;
        }
    }

    private String leer(String clave, String porDefecto) {
        String delSistema = System.getProperty(clave);
        if (delSistema != null && !delSistema.isBlank()) {
            return delSistema;
        }
        return propiedades.getProperty(clave, porDefecto);
    }
}
