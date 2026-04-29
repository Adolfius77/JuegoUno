package controlador;

import Entidades.Carta;
import Entidades.Jugador;
import Interfaces.IVista;
import red.GestorPartida;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    private final GestorPartida gestor;
    private final IVista vista;
    private final List<String> nombreJugadores;

    public GameController(GestorPartida gestor, IVista vista, List<String> nombreJugadores) {
        if (gestor == null || vista == null) {
            throw new IllegalArgumentException("Gestor y vista son obligatorios");
        }
        this.gestor = gestor;
        this.vista = vista;
        this.nombreJugadores = nombreJugadores == null
                ? new ArrayList<>()
                : new ArrayList<>(nombreJugadores);
        if (gestor.obtenerEstadoPartida() != null) {
            this.gestor.obtenerEstadoPartida();
        }
    }

    public void iniciarJuego() {
        try {
            gestor.iniciarPartida();
            vista.mostrarVista();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al iniciar juego: " + e.getMessage());
        }
    }

    public void inicarJuego() {
        iniciarJuego();
    }

    public void jugarCarta(Jugador jugador, Carta carta) {
        try {
            if (jugador == null || carta == null) {
                vista.mostrarMensaje("Jugador y carta son obligatorios");
                return;
            }

            Jugador jugadorActual = gestor.obtenerJugadorActual();
            if (!esMismoJugador(jugador, jugadorActual)) {
                vista.mostrarMensaje("No es turno de este jugador");
                return;
            }

            gestor.jugarCarta(jugadorActual, carta);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al jugar carta: " + e.getMessage());
        }
    }

    public void tomarCarta() {
        try {
            Jugador jugadorActual = gestor.obtenerJugadorActual();
            if (jugadorActual == null) {
                vista.mostrarMensaje("No hay jugador activo");
                return;
            }
            gestor.tomarCarta(jugadorActual);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al tomar carta: " + e.getMessage());
        }
    }

    public void decirUno(Jugador jugador) {
        try {
            if (jugador == null || jugador.getMano() == null) {
                vista.mostrarMensaje("Jugador invalido");
                return;
            }

            if (jugador.getMano().getCartas().size() == 1) {
                gestor.decirUno(jugador);
                vista.mostrarMensaje("El jugador " + jugador.getNombre() + " dijo UNO");
                return;
            }

            vista.mostrarMensaje("No grito UNO, se le dan 2 cartas");
            gestor.tomarCarta(jugador);
            gestor.tomarCarta(jugador);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al decir UNO: " + e.getMessage());
        }
    }

    public void pasarTurno() {
        try {
            gestor.pasarTurno();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al pasar turno: " + e.getMessage());
        }
    }

    public void pasarrTurno() {
        pasarTurno();
    }

    public List<String> getNombreJugadores() {
        return Collections.unmodifiableList(nombreJugadores);
    }

    public GestorPartida obtenerGestor() {
        return gestor;
    }

    private boolean esMismoJugador(Jugador jugadorA, Jugador jugadorB) {
        if (jugadorA == null || jugadorB == null) {
            return false;
        }
        if (jugadorA.equals(jugadorB)) {
            return true;
        }
        String idA = jugadorA.getId();
        String idB = jugadorB.getId();
        return idA != null && idA.equals(idB);
    }
}
