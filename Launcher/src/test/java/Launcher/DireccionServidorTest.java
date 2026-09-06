package Launcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Como se entiende la direccion del servidor que teclea el jugador.
 *
 * Es texto escrito a mano, asi que lo que importa es que lo raro se rechace de
 * forma limpia: devolver null para poder volver a preguntar, nunca reventar.
 */
class DireccionServidorTest {

    @AfterEach
    void limpiarPropiedades() {
        System.clearProperty(DireccionServidor.CLAVE_IP);
        System.clearProperty(DireccionServidor.CLAVE_PUERTO);
    }

    @Test
    @DisplayName("solo la maquina: se usa el puerto de siempre")
    void soloLaMaquina() {
        DireccionServidor d = DireccionServidor.parsear("203.0.113.45");

        assertNotNull(d);
        assertEquals("203.0.113.45", d.maquina());
        assertEquals(DireccionServidor.PUERTO_POR_DEFECTO, d.puerto());
    }

    @Test
    @DisplayName("maquina y puerto")
    void maquinaConPuerto() {
        DireccionServidor d = DireccionServidor.parsear("mi-servidor.com:9000");

        assertNotNull(d);
        assertEquals("mi-servidor.com", d.maquina());
        assertEquals(9000, d.puerto());
    }

    @Test
    @DisplayName("los espacios de mas no estorban")
    void recortaEspacios() {
        DireccionServidor d = DireccionServidor.parsear("   192.168.1.10 : 8080   ");

        assertNotNull(d);
        assertEquals("192.168.1.10", d.maquina());
        assertEquals(8080, d.puerto());
    }

    @Test
    @DisplayName("un puerto que no es un numero se rechaza, no revienta")
    void puertoQueNoEsNumero() {
        assertNull(DireccionServidor.parsear("192.168.1.10:ocho mil"));
        assertNull(DireccionServidor.parsear("192.168.1.10:"));
    }

    @Test
    @DisplayName("un puerto fuera de rango se rechaza")
    void puertoFueraDeRango() {
        assertNull(DireccionServidor.parsear("192.168.1.10:0"));
        assertNull(DireccionServidor.parsear("192.168.1.10:70000"));
        assertNull(DireccionServidor.parsear("192.168.1.10:-1"));
    }

    @Test
    @DisplayName("texto vacio o nulo se rechaza")
    void textoVacio() {
        assertNull(DireccionServidor.parsear(null));
        assertNull(DireccionServidor.parsear(""));
        assertNull(DireccionServidor.parsear("    "));
        assertNull(DireccionServidor.parsear(":8080"));
    }

    @Test
    @DisplayName("una direccion pegada con // delante se limpia")
    void aguantaLaDireccionPegada() {
        DireccionServidor d = DireccionServidor.parsear("//203.0.113.45:8080");

        assertNotNull(d);
        assertEquals("203.0.113.45", d.maquina());
        assertEquals(8080, d.puerto());
    }

    @Test
    @DisplayName("aplicar deja la direccion donde la busca la capa de red")
    void aplicarDejaLasPropiedades() {
        DireccionServidor.parsear("203.0.113.45:9000").aplicar();

        assertEquals("203.0.113.45", System.getProperty(DireccionServidor.CLAVE_IP));
        assertEquals("9000", System.getProperty(DireccionServidor.CLAVE_PUERTO));
    }

    @Test
    @DisplayName("una direccion por -Dservidor.ip gana, y con ella no se pregunta nada")
    void laPropiedadDeSistemaGana() {
        System.setProperty(DireccionServidor.CLAVE_IP, "203.0.113.45");
        System.setProperty(DireccionServidor.CLAVE_PUERTO, "9000");

        DireccionServidor d = DireccionServidor.deLosArgumentos(new String[]{"otra.maquina"});

        assertNotNull(d, "sin esto, un script se quedaria esperando a un dialogo");
        assertEquals("203.0.113.45", d.maquina());
        assertEquals(9000, d.puerto());
    }

    @Test
    @DisplayName("sin propiedad, vale el primer argumento")
    void elPrimerArgumentoTambienVale() {
        DireccionServidor d = DireccionServidor.deLosArgumentos(new String[]{"203.0.113.45:7000"});

        assertNotNull(d);
        assertEquals("203.0.113.45", d.maquina());
        assertEquals(7000, d.puerto());
    }

    @Test
    @DisplayName("sin nada dado, no hay direccion: hay que preguntarla")
    void sinNadaNoHayDireccion() {
        assertNull(DireccionServidor.deLosArgumentos(new String[0]));
        assertNull(DireccionServidor.deLosArgumentos(null));
    }

    @Test
    @DisplayName("reconoce cuando el servidor es esta misma maquina")
    void reconoceLoLocal() {
        assertTrue(DireccionServidor.parsear("localhost").esLocal());
        assertTrue(DireccionServidor.parsear("127.0.0.1:9000").esLocal());
        assertFalse(DireccionServidor.parsear("203.0.113.45").esLocal());
    }

    @Test
    @DisplayName("se escribe igual que se lee, para poder guardarla y recuperarla")
    void seEscribeIgualQueSeLee() {
        DireccionServidor original = DireccionServidor.parsear("203.0.113.45:9000");
        DireccionServidor devuelta = DireccionServidor.parsear(original.toString());

        assertEquals(original, devuelta);
    }

    @Test
    @DisplayName("construir una direccion imposible falla en el sitio")
    void construirAlgoImposibleFalla() {
        assertThrows(IllegalArgumentException.class, () -> new DireccionServidor("", 8080));
        assertThrows(IllegalArgumentException.class, () -> new DireccionServidor("maquina", 0));
        assertThrows(IllegalArgumentException.class, () -> new DireccionServidor("maquina", 99999));
    }
}
