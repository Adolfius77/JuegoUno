package controlador;

import Entidades.Carta;
import Entidades.Jugador;
import Interfaces.IVista;
import Logica.Partida;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    private final Partida modelo;
    private final IVista vista;
    private final List<String> nombreJugadores;

    public GameController(Partida modelo, IVista vista, List<String> nombreJugadores) {
        if (modelo == null || vista == null) {
            throw new IllegalArgumentException("modelo y vista son obligatorios");
        }
        this.modelo = modelo;
        this.vista = vista;
        this.nombreJugadores = nombreJugadores == null
                ? new ArrayList<>()
                : new ArrayList<>(nombreJugadores);
        this.modelo.agregarObservador(this.vista);
    }

    public void iniciarJuego() {
        modelo.iniciar();
        vista.mostrarVista();
    }

    // Compatibilidad con llamadas existentes.
    public void inicarJuego() {
        iniciarJuego();
    }

    public void jugarCarta(Jugador jugador, Carta carta) {
        if (jugador == null || carta == null) {
            vista.mostrarMensaje("jugador y carta son obligatorios");
            return;
        }

        Jugador jugadorActual = modelo.getJugadorActual();
        if (!esMismoJugador(jugador, jugadorActual)) {
            vista.mostrarMensaje("no es turno de este jugador");
            return;
        }

        modelo.jugarCarta(carta, jugadorActual);
    }

    public void tomarCarta() {
        Jugador jugadorActual = modelo.getJugadorActual();
        if (jugadorActual == null) {
            vista.mostrarMensaje("no hay jugador activo");
            return;
        }
        modelo.tomarCarta(jugadorActual);
    }

    public void decirUno(Jugador jugador) {
        if (jugador == null || jugador.getMano() == null) {
            vista.mostrarMensaje("jugador invalido");
            return;
        }

        if (jugador.getMano().getCartas().size() == 1) {
            vista.mostrarMensaje("el jugador " + jugador.getNombre() + " dijo UNO");
            return;
        }

        vista.mostrarMensaje("no grito UNO, se le dan 2 cartas");
        modelo.tomarCarta(jugador);
        modelo.tomarCarta(jugador);
    }

    public void pasarTurno() {
        modelo.pasarTurno();
    }

    // Compatibilidad con llamadas existentes.
    public void pasarrTurno() {
        pasarTurno();
    }

    public List<String> getNombreJugadores() {
        return Collections.unmodifiableList(nombreJugadores);
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
