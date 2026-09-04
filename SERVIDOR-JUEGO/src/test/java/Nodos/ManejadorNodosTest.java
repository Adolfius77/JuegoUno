package Nodos;

import Interfacez.IProxy;
import dtos.MensajeDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManejadorNodosTest {

    /** Proxy de mentira: registra lo que se le manda, sin socket. */
    private static class ProxyEspia implements IProxy {
        final List<String> enviados = new ArrayList<>();
        @Override public void enviarMensaje(MensajeDTO mensaje) { enviados.add(mensaje.getTipo()); }
        @Override public void run() { }
    }

    private MensajeDTO mensaje(String tipo) {
        MensajeDTO m = new MensajeDTO();
        m.setTipo(tipo);
        return m;
    }

    @Test
    void dosSesionesDeLaMismaMaquinaNoSePisan() {
        // Regresion: antes los nodos se indexaban por IP y por nombre, asi que dos
        // clientes del mismo equipo (o detras del mismo NAT) colapsaban en uno.
        ManejadorNodos manejador = new ManejadorNodos();
        ProxyEspia proxyAna = new ProxyEspia();
        ProxyEspia proxyBeto = new ProxyEspia();

        manejador.registrarNuevoJugador(new NodoCliente("S1", proxyAna, "Jugador_1", "pfp"));
        manejador.registrarNuevoJugador(new NodoCliente("S2", proxyBeto, "Jugador_2", "pfp"));

        manejador.obtenerNodoPorSesion("S1").setNombre("Ana");
        manejador.obtenerNodoPorSesion("S2").setNombre("Beto");

        assertEquals(2, manejador.obtenerNodosConectados().size(), "deben seguir siendo dos sesiones");
        assertEquals("Ana", manejador.obtenerNodoPorSesion("S1").getNombre());
        assertEquals("Beto", manejador.obtenerNodoPorSesion("S2").getNombre());
    }

    @Test
    void renombrarNoPierdeLaSesion() {
        ManejadorNodos manejador = new ManejadorNodos();
        manejador.registrarNuevoJugador(new NodoCliente("S1", new ProxyEspia(), "Jugador_1", "pfp"));

        manejador.obtenerNodoPorSesion("S1").setNombre("Ana");

        assertNotNull(manejador.obtenerNodoPorSesion("S1"));
        assertNotNull(manejador.obtenerNodoPorNombre("Ana"));
        assertEquals(1, manejador.obtenerNodosConectados().size());
    }

    @Test
    void enviarNodoLlegaSoloALaSesionIndicada() {
        ManejadorNodos manejador = new ManejadorNodos();
        ProxyEspia proxyAna = new ProxyEspia();
        ProxyEspia proxyBeto = new ProxyEspia();
        manejador.registrarNuevoJugador(new NodoCliente("S1", proxyAna, "Ana", "pfp"));
        manejador.registrarNuevoJugador(new NodoCliente("S2", proxyBeto, "Beto", "pfp"));

        manejador.enviarNodo("S1", mensaje("REGISTRO_EXITOSO"));

        assertEquals(List.of("REGISTRO_EXITOSO"), proxyAna.enviados);
        assertTrue(proxyBeto.enviados.isEmpty(), "el otro jugador no debe recibirlo");
    }

    @Test
    void nombreEnUsoIgnoraLaPropiaSesion() {
        ManejadorNodos manejador = new ManejadorNodos();
        manejador.registrarNuevoJugador(new NodoCliente("S1", new ProxyEspia(), "Ana", "pfp"));

        assertFalse(manejador.nombreEnUsoPorOtro("Ana", "S1"), "reusar el propio nombre es valido");
        assertTrue(manejador.nombreEnUsoPorOtro("Ana", "S2"), "otro no puede tomar el nombre");
        assertTrue(manejador.nombreEnUsoPorOtro("ana", "S2"), "la comparacion ignora mayusculas");
    }

    @Test
    void eliminarSesionNoAfectaALasDemas() {
        ManejadorNodos manejador = new ManejadorNodos();
        manejador.registrarNuevoJugador(new NodoCliente("S1", new ProxyEspia(), "Ana", "pfp"));
        manejador.registrarNuevoJugador(new NodoCliente("S2", new ProxyEspia(), "Beto", "pfp"));

        manejador.eliminarNodo("S1");

        assertNull(manejador.obtenerNodoPorSesion("S1"));
        assertNotNull(manejador.obtenerNodoPorSesion("S2"));
    }

    @Test
    void todosListosRequiereAlMenosUnaSesion() {
        ManejadorNodos manejador = new ManejadorNodos();
        assertFalse(manejador.estanTodosListos(), "sin jugadores no se puede iniciar");

        manejador.registrarNuevoJugador(new NodoCliente("S1", new ProxyEspia(), "Ana", "pfp"));
        assertFalse(manejador.estanTodosListos());

        manejador.obtenerNodoPorSesion("S1").setEstaListo(true);
        assertTrue(manejador.estanTodosListos());
    }
}
