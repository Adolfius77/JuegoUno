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
    void todosListosRequiereAlMenosUnaSesionEnLaSala() {
        ManejadorNodos manejador = new ManejadorNodos();
        assertFalse(manejador.estanTodosListosEnSala("AAAA"), "sin jugadores no se puede iniciar");

        manejador.registrarNuevoJugador(new NodoCliente("S1", new ProxyEspia(), "Ana", "pfp"));
        manejador.obtenerNodoPorSesion("S1").setCodigoSala("AAAA");
        assertFalse(manejador.estanTodosListosEnSala("AAAA"));

        manejador.obtenerNodoPorSesion("S1").setEstaListo(true);
        assertTrue(manejador.estanTodosListosEnSala("AAAA"));
    }

    // --- Aislamiento entre salas -------------------------------------------

    private ManejadorNodos conDosSalas(ProxyEspia pAna, ProxyEspia pBeto, ProxyEspia pCarla) {
        ManejadorNodos m = new ManejadorNodos();
        m.registrarNuevoJugador(new NodoCliente("S1", pAna, "Ana", "pfp"));
        m.registrarNuevoJugador(new NodoCliente("S2", pBeto, "Beto", "pfp"));
        m.registrarNuevoJugador(new NodoCliente("S3", pCarla, "Carla", "pfp"));
        m.obtenerNodoPorSesion("S1").setCodigoSala("AAAA");
        m.obtenerNodoPorSesion("S2").setCodigoSala("AAAA");
        m.obtenerNodoPorSesion("S3").setCodigoSala("BBBB");
        return m;
    }

    @Test
    void notificarASalaNoAlcanzaALaOtraSala() {
        ProxyEspia pAna = new ProxyEspia(), pBeto = new ProxyEspia(), pCarla = new ProxyEspia();
        ManejadorNodos m = conDosSalas(pAna, pBeto, pCarla);

        m.notificarASala("AAAA", mensaje("ACTUALIZACION_MESA"));

        assertEquals(List.of("ACTUALIZACION_MESA"), pAna.enviados);
        assertEquals(List.of("ACTUALIZACION_MESA"), pBeto.enviados);
        assertTrue(pCarla.enviados.isEmpty(), "la sala BBBB no debe ver el tablero de AAAA");
    }

    @Test
    void unaPartidaSoloTomaALosJugadoresDeSuSala() {
        ManejadorNodos m = conDosSalas(new ProxyEspia(), new ProxyEspia(), new ProxyEspia());

        assertEquals(List.of("Ana", "Beto"), m.obtenerNombresDeSala("AAAA").stream().sorted().toList());
        assertEquals(List.of("Carla"), m.obtenerNombresDeSala("BBBB"));
    }

    @Test
    void listosDeUnaSalaNoDependenDeLaOtra() {
        ManejadorNodos m = conDosSalas(new ProxyEspia(), new ProxyEspia(), new ProxyEspia());
        m.obtenerNodoPorSesion("S1").setEstaListo(true);
        m.obtenerNodoPorSesion("S2").setEstaListo(true);

        assertTrue(m.estanTodosListosEnSala("AAAA"), "Carla, en otra sala, no debe bloquear a AAAA");
        assertFalse(m.estanTodosListosEnSala("BBBB"));
    }

    @Test
    void quienNoEntroAUnaSalaSigueEnElLobby() {
        ManejadorNodos m = new ManejadorNodos();
        m.registrarNuevoJugador(new NodoCliente("S1", new ProxyEspia(), "Ana", "pfp"));
        m.registrarNuevoJugador(new NodoCliente("S2", new ProxyEspia(), "Beto", "pfp"));
        m.obtenerNodoPorSesion("S1").setCodigoSala("AAAA");

        assertEquals(1, m.obtenerNodosEnLobby().size());
        assertEquals("Beto", m.obtenerNodosEnLobby().get(0).getNombre());
    }
}
