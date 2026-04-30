package controlador;

import Interfaces.IVista;
import red.GestorPartida;

public class LobbyController {

    private final IVista vista;
    private final GestorPartida gestor;

    public LobbyController(IVista vista, GestorPartida gestor) {
        if (vista == null || gestor == null) {
            throw new IllegalArgumentException("vista y gestor son obligatorios");
        }
        this.vista = vista;
        this.gestor = gestor;
    }

    public boolean agregarJugador(String nombreJugador) {
        try {
            if (nombreJugador == null) {
                vista.mostrarMensaje("el nombre del jugador es obligatorio");
                return false;
            }
            gestor.procesarRegistro(nombreJugador);
            return true;
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje(e.getMessage());
            return false;
        }
    }

    public void iniciarPartida() {
        try {
            gestor.iniciarPartida();
        } catch (Exception e) {
            vista.mostrarMensaje("error al iniciar partida: " + e.getMessage());
        }
    }

    public GestorPartida obtenerGestor() {
        return gestor;
    }
}
