package Entidades.Logica;

import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Lobby;
import Entidades.fabricas.PartidaFactory;
import Mappers.LobbyMapper;
import Mappers.PartidaMapper;
import dtos.MensajeListaJugadoresDTO;
import dtos.PartidaDTO;

import java.util.ArrayList;
import java.util.List;

public class GestorPartida {
    private final Lobby lobby;
    private Partida partida;

    public GestorPartida() {
        this.lobby = new Lobby();
        this.partida = null;
    }

    public MensajeListaJugadoresDTO procesarRegistro(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("nombre invalido");
        }

        Jugador nuevo = new Jugador(nombre.trim());
        lobby.agregarJugador(nuevo.getNombre());

        return LobbyMapper.toDTO(lobby);
    }

    public List<String> obtenerJugadoresRegistrados() {
        return lobby.getNombreJugadores();
    }

    public Lobby obtenerLobby() {
        return lobby;
    }

    public Partida obtenerPartida() {
        return partida;
    }

    public void limpiarLobby() {
        lobby.limpiarJugadores();
    }

    public PartidaDTO iniciarPartida() {
        if (partida != null && esPartidaActiva()) {
            throw new IllegalStateException("Ya hay una partida en curso");
        }

        List<String> nombresJugadores = lobby.getNombreJugadores();
        if (nombresJugadores.size() < 2) {
            throw new IllegalStateException("Se requieren al menos 2 jugadores");
        }

        List<Jugador> jugadores = new ArrayList<>();
        for (String nombreJugador : nombresJugadores) {
            jugadores.add(new Jugador(nombreJugador));
        }

        this.partida = PartidaFactory.crearPartida(jugadores, null, null);
        this.partida.iniciar();
        return PartidaMapper.toDTO(this.partida);
    }

    public PartidaDTO jugarCarta(Jugador jugador, Carta carta) {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        partida.jugarCarta(carta, jugador);
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO tomarCarta(Jugador jugador) {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        partida.tomarCarta(jugador);
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO pasarTurno() {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        partida.pasarTurno();
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO saltarTurno() {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        partida.saltarTurno();
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO cambiarSentido() {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        partida.cambiarSentido();
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO acumularCartas(int cantidad) {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        partida.acomularCartas(cantidad);
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO decirUno(Jugador jugador) {
        if (partida == null || !esPartidaActiva()) {
            throw new IllegalStateException("No hay partida activa");
        }
        jugador.setDijoUno(true);
        return PartidaMapper.toDTO(partida);
    }

    public PartidaDTO obtenerEstadoPartida() {
        if (partida == null) {
            return null;
        }
        return PartidaMapper.toDTO(partida);
    }

    public Jugador obtenerJugadorActual() {
        if (partida == null) {
            return null;
        }
        return partida.getJugadorActual();
    }

    public boolean esPartidaActiva() {
        if (partida == null) {
            return false;
        }
        return partida.getEstado() != null &&
               partida.getEstado().toString().contains("Jugando");
    }

    public void finalizarPartida() {
        if (partida != null) {
            this.partida = null;
        }
    }
}

