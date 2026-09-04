package servidor;

import Entidades.Estados.IEstadoPartida;
import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.enums.Color;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Mappers.CartaMapper;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos;
import dtos.PartidaDTO;
import dtos.CartaDTO;
import facades.GestorJuegoFacade;
import servidor.observador.ObservadorPartidaRed;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mantiene una partida por sala.
 *
 * Antes guardaba una unica partidaActual, asi que el servidor solo soportaba una
 * partida a la vez aunque GestorSalas administrara varias salas: iniciar en una
 * sala pisaba la partida de las demas.
 */
public class JuegoServidor {

    private final ICartaFactory cartaFactory;
    private final IMazoFactory mazoFactory;
    private final IEstadoPartida estadoInicial;
    private final CartaMapper cartaMapper = new CartaMapper();

    private final Map<String, GestorJuegoFacade> partidasPorSala = new ConcurrentHashMap<>();
    /** Un candado por sala: serializa las jugadas sin frenar a las demas salas. */
    private final Map<String, Object> candadosPorSala = new ConcurrentHashMap<>();

    public JuegoServidor(ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estadoInicial) {
        this.cartaFactory = cartaFactory;
        this.mazoFactory = mazoFactory;
        this.estadoInicial = estadoInicial;
    }

    /**
     * @param avataresPorNombre nombre -> avatar de cada jugador de la sala, en
     *                          orden de turno. Antes solo viajaban los nombres y
     *                          el avatar se perdia entre la sesion y la partida.
     */
    public PartidaDTO iniciarNuevoJuego(String codigoSala, Map<String, String> avataresPorNombre,
                                        ManejadorNodos manejadorNodos) {
        GestorJuegoFacade fachada = new GestorJuegoFacade(cartaFactory, mazoFactory, estadoInicial);
        fachada.prepararIniciarPartida(avataresPorNombre);

        Partida partida = fachada.getPartidaActual();
        // El observador solo difunde a los jugadores de esta sala.
        partida.agregarObservador(new ObservadorPartidaRed(partida, manejadorNodos, codigoSala));

        partidasPorSala.put(normalizar(codigoSala), fachada);
        return PartidaMapper.toDTO(partida);
    }

    public Partida getPartidaDeSala(String codigoSala) {
        GestorJuegoFacade fachada = partidasPorSala.get(normalizar(codigoSala));
        return fachada != null ? fachada.getPartidaActual() : null;
    }

    /**
     * Ejecuta una jugada con la partida de la sala tomada en exclusiva.
     *
     * El servidor atiende a cada cliente en su propio hilo, asi que sin esto dos
     * jugadores de la misma sala podian mutar la Partida a la vez: la validacion
     * de turno de uno podia pasar mientras el otro ya estaba cambiando el turno.
     * El candado es por sala, de modo que las partidas de salas distintas siguen
     * corriendo en paralelo.
     */
    public void ejecutarEnPartida(String codigoSala, Runnable jugada) {
        Object candado = candadoDeSala(codigoSala);
        synchronized (candado) {
            jugada.run();
        }
    }

    private Object candadoDeSala(String codigoSala) {
        return candadosPorSala.computeIfAbsent(normalizar(codigoSala), k -> new Object());
    }

    public GestorJuegoFacade getFachadaDeSala(String codigoSala) {
        return partidasPorSala.get(normalizar(codigoSala));
    }

    public void terminarPartida(String codigoSala) {
        partidasPorSala.remove(normalizar(codigoSala));
        candadosPorSala.remove(normalizar(codigoSala));
    }

    public PartidaDTO obtenerEstadoActual(String codigoSala) {
        Partida partida = getPartidaDeSala(codigoSala);
        return partida != null ? PartidaMapper.toDTO(partida) : null;
    }

    public Partida validarPartidaActiva(String codigoSala) {
        Partida partida = getPartidaDeSala(codigoSala);
        if (partida == null) {
            throw new IllegalStateException("No hay una partida activa en la sala " + codigoSala + ".");
        }
        if (partida.getEstado() == null || !partida.getEstado().estaEnCurso()) {
            throw new IllegalStateException("La partida no esta en curso.");
        }
        return partida;
    }

    public Jugador obtenerJugador(String codigoSala, String nombreJugador) {
        if (nombreJugador == null || nombreJugador.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador es obligatorio.");
        }
        Partida partida = validarPartidaActiva(codigoSala);
        return partida.getJugadores().stream()
                .filter(j -> nombreJugador.equalsIgnoreCase(j.getNombre()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el jugador en la partida."));
    }

    public void validarTurno(String codigoSala, Jugador jugador) {
        Partida partida = validarPartidaActiva(codigoSala);
        if (partida.getJugadorActual() == null) {
            throw new IllegalStateException("La partida aun no tiene turno activo.");
        }
        if (!Objects.equals(partida.getJugadorActual().getNombre(), jugador.getNombre())) {
            throw new IllegalStateException("No es el turno del jugador " + jugador.getNombre());
        }
    }

    public Carta buscarCartaEnMano(Jugador jugador, CartaDTO cartaDTO) {
        if (jugador == null || jugador.getMano() == null || cartaDTO == null) {
            return null;
        }
        for (Carta carta : jugador.getMano().getCartas()) {
            CartaDTO cartaActual = cartaMapper.toDTO(carta);
            if (cartaActual != null
                    && Objects.equals(normalizar(cartaActual.getColor()), normalizar(cartaDTO.getColor()))
                    && Objects.equals(normalizar(cartaActual.getValor()), normalizar(cartaDTO.getValor()))) {
                return carta;
            }
        }
        return null;
    }

    public Color colorDesdeTexto(String color) {
        if (color == null || color.isBlank()) {
            return null;
        }
        try {
            return Color.valueOf(normalizar(color));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase();
    }
}
