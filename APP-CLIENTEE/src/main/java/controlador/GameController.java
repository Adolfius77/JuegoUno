package controlador;

import Entidades.Carta;
import Entidades.Jugador;
import Interfaces.IVista;
import dtos.CartaDTO;
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

    public void jugarCarta(CartaDTO carta) {
        try {
            if(carta == null){
                vista.mostrarMensaje("debes seleccionar una carta para jugar");
                return;
            }
            gestor.jugarCarta(carta);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al jugar carta: " + e.getMessage());
        }
    }

    public void tomarCarta() {
        try {
            gestor.tomarCarta();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al tomar carta: " + e.getMessage());
        }
    }

    public void decirUno() {
        try {
            gestor.decirUno();
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
