/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package red;

import Entidades.Estados.IEstadoPartida;
import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.enums.Color;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Interfacez.IBroker;
import Mappers.CartaMapper;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos;
import dtos.PartidaDTO;
import dtos.CartaDTO;
import facades.GestorJuegoFacade;
import observador.ObservadorPartidaRed;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author USER
 */
public class JuegoServidor {

    private final IBroker broker;
    private GestorJuegoFacade fachadaJuego;
    private final ICartaFactory cartaFactory;
    private final IMazoFactory mazoFactory;
    private final IEstadoPartida estadoInicial;
    private final CartaMapper cartaMapper = new CartaMapper();
    private Partida partidaActual;
    private ObservadorPartidaRed observadorPartidaRed;

    public JuegoServidor(IBroker broker, ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estadoInicial) {
        this.broker = broker;
        this.cartaFactory = cartaFactory;
        this.mazoFactory = mazoFactory;
        this.estadoInicial = estadoInicial;
        //aqui pondremos los comandos para el caso de uso inicial que es ejercer turno
    }

    public PartidaDTO iniciarNuevoJuego(List<String> nombreJugadores, ManejadorNodos manejadorNodos) {
        this.fachadaJuego = new GestorJuegoFacade(cartaFactory, mazoFactory, estadoInicial);
        this.fachadaJuego.prepararIniciarPartida(nombreJugadores);

        this.partidaActual = this.fachadaJuego.getPartidaActual();
        this.observadorPartidaRed = new ObservadorPartidaRed(this.partidaActual, manejadorNodos);
        this.partidaActual.agregarObservador(this.observadorPartidaRed);

        return PartidaMapper.toDTO(this.partidaActual);
    }

    public synchronized Partida getPartidaActualEntidad() {
        return partidaActual;
    }

    public synchronized PartidaDTO obtenerEstadoActual() {
        return PartidaMapper.toDTO(this.partidaActual);
    }

    public synchronized PartidaDTO jugarCarta(String nombreJugador, CartaDTO cartaDTO) {
        return jugarCarta(nombreJugador, cartaDTO, null);
    }

    public synchronized PartidaDTO jugarCarta(String nombreJugador, CartaDTO cartaDTO, String colorElegido) {
        Partida partida = validarPartidaActiva();
        Jugador jugador = obtenerJugador(nombreJugador);
        validarTurno(jugador);

        Carta carta = buscarCartaEnMano(jugador, cartaDTO);
        if (carta == null) {
            throw new IllegalArgumentException("La carta seleccionada no esta en la mano del jugador.");
        }

        Carta cartaMesa = partida.getPilaCartas() != null && !partida.getPilaCartas().getListaCartas().isEmpty()
                ? partida.getPilaCartas().obtenerUltimaCarta()
                : null;
        if (cartaMesa != null && !carta.esJugable(cartaMesa)) {
            throw new IllegalStateException("La carta no es jugable sobre la mesa actual.");
        }

        Color colorAplicado = colorDesdeTexto(colorElegido);
        if (colorAplicado != null && cartaDTO != null) {
            carta.setColor(colorAplicado);
        }

        partida.jugarCarta(carta, jugador);
        return PartidaMapper.toDTO(partida);
    }

    public synchronized PartidaDTO tomarCarta(String nombreJugador) {
        Partida partida = validarPartidaActiva();
        Jugador jugador = obtenerJugador(nombreJugador);
        validarTurno(jugador);
        partida.tomarCartasHastaQueSeaJugable(jugador);
        return PartidaMapper.toDTO(partida);
    }

    public synchronized PartidaDTO pasarTurno(String nombreJugador) {
        Partida partida = validarPartidaActiva();
        Jugador jugador = obtenerJugador(nombreJugador);
        validarTurno(jugador);
        partida.pasarTurno();
        return PartidaMapper.toDTO(partida);
    }

    public synchronized PartidaDTO gritarUno(String nombreJugador) {
        Partida partida = validarPartidaActiva();
        Jugador jugador = obtenerJugador(nombreJugador);

        if (jugador.getMano() == null || jugador.getMano().getCartas().size() != 1) {
            throw new IllegalStateException("El jugador no tiene 1 sola carta. UNO inválido.");
        }

        jugador.setDijoUno(true);
        partida.notificarObservador("UNO_GRITADO:" + nombreJugador);
        return PartidaMapper.toDTO(partida);
    }

    public synchronized boolean debeGritarUno(String nombreJugador) {
        Partida partida = validarPartidaActiva();
        Jugador jugador = obtenerJugador(nombreJugador);

        if (jugador.getMano() == null) {
            return false;
        }

        int cartasEnMano = jugador.getMano().getCartas().size();
        return cartasEnMano == 1 && !jugador.isDijoUno();
    }

    public synchronized PartidaDTO penalizarNoUno(String nombreJugador) {
        Partida partida = validarPartidaActiva();
        Jugador jugador = obtenerJugador(nombreJugador);

        if (jugador.getMano() != null && !jugador.isDijoUno()
                && jugador.getMano().getCartas().size() > 1) {
            int cartasActuales = jugador.getMano().getCartas().size();
            if (cartasActuales == 2) {
                for (int i = 0; i < 2; i++) {
                    if (!partida.getMazo().estaVacio()) {
                        partida.tomarCarta(jugador);
                    }
                }
                partida.notificarObservador("PENALIZACION_NO_UNO:" + nombreJugador);
            }
        }

        return PartidaMapper.toDTO(partida);
    }

    private Partida validarPartidaActiva() {
        if (this.partidaActual == null) {
            throw new IllegalStateException("No hay una partida activa.");
        }
        if (this.partidaActual.getEstado() == null || !this.partidaActual.getEstado().toString().contains("Jugando")) {
            throw new IllegalStateException("La partida no esta en curso.");
        }
        return this.partidaActual;
    }

    private Jugador obtenerJugador(String nombreJugador) {
        if (nombreJugador == null || nombreJugador.isBlank()) {
            throw new IllegalArgumentException("El nombre del jugador es obligatorio.");
        }
        Partida partida = validarPartidaActiva();
        return partida.getJugadores().stream()
                .filter(j -> nombreJugador.equalsIgnoreCase(j.getNombre()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No se encontro el jugador en la partida."));
    }

    private void validarTurno(Jugador jugador) {
        Partida partida = validarPartidaActiva();
        if (partida.getJugadorActual() == null) {
            throw new IllegalStateException("La partida aun no tiene turno activo.");
        }
        if (!Objects.equals(partida.getJugadorActual().getNombre(), jugador.getNombre())) {
            throw new IllegalStateException("No es el turno del jugador " + jugador.getNombre());
        }
    }

    private Carta buscarCartaEnMano(Jugador jugador, CartaDTO cartaDTO) {
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

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase();
    }

    private Color colorDesdeTexto(String color) {
        if (color == null || color.isBlank()) {
            return null;
        }
        try {
            return Color.valueOf(normalizar(color));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
