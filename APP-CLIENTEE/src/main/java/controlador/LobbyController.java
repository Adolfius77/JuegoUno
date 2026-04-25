package controlador;

import Entidades.Logica.GestorPartida;
import Interfaces.IVista;
import java.util.Collections;
import java.util.List;

public class LobbyController {

    private final IVista vista;
    private final GestorPartida gestor;

    public LobbyController(IVista vista, GestorPartida gestor) {
        if (vista == null || gestor == null) {
            throw new IllegalArgumentException("vista y gestor son obligatorios");
        }
        this.vista = vista;
        this.gestor = gestor;
        this.gestor.obtenerLobby().agregarObservador(this.vista);
    }

    public boolean agregarJugador(String nombreJugador) {
        try {
            if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
                vista.mostrarMensaje("el nombre del jugador es obligatorio");
                return false;
            }
            gestor.procesarRegistro(nombreJugador.trim());
            return true;
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje(e.getMessage());
            return false;
        }
    }

    public boolean iniciarPartida() {
        try {
            if (gestor.obtenerJugadoresRegistrados().size() >= 2) {
                vista.cerrarVista();
                return true;
            }
            vista.mostrarMensaje("se requieren al menos 2 jugadores para iniciar");
            return false;
        } catch (Exception e) {
            vista.mostrarMensaje("error al iniciar partida: " + e.getMessage());
            return false;
        }
    }

    public List<String> obtenerJugadores() {
        return Collections.unmodifiableList(gestor.obtenerJugadoresRegistrados());
    }

    public GestorPartida obtenerGestor() {
        return gestor;
    }
}
