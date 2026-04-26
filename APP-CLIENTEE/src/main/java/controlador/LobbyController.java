package controlador;

import Entidades.Jugador;
import Entidades.Logica.GestorPartida;
import Interfaces.IVista;
import facades.GestorJuegoFacade;

import java.util.Collections;
import java.util.List;

public class LobbyController {

    private final IVista vista;
    private final GestorJuegoFacade facade;

    public LobbyController(IVista vista, GestorJuegoFacade facade) {
        if (vista == null || facade == null) {
            throw new IllegalArgumentException("vista y gestor son obligatorios");
        }
        this.vista = vista;
        this.facade = facade;
        this.facade.getPartidaActual().agregarObservador(this.vista);
    }

    public boolean agregarJugador(Jugador nombreJugador) {
        try {
            if (nombreJugador == null) {
                vista.mostrarMensaje("el nombre del jugador es obligatorio");
                return false;
            }
            facade.getPartidaActual().agregarJugador(nombreJugador);
            return true;
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje(e.getMessage());
            return false;
        }
    }

    public boolean iniciarPartida() {
        try {
            List<Jugador> jugadores = facade.getPartidaActual().getJugadores();
            if(jugadores.size() < 2){
                vista.mostrarMensaje("se requieren almenos 2 jugadores para iniciar la partida.");
                return false;
            }
            facade.prepararIniciarPartida(jugadores);
            vista.cerrarVista();
            return true;
        } catch (Exception e) {
            vista.mostrarMensaje("error al iniciar partida: " + e.getMessage());
            return false;
        }
    }

    public GestorJuegoFacade obtenerGestor() {
        return facade;
    }
}
