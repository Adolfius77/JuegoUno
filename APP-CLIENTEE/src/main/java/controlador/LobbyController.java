package controlador;

import Entidades.Lobby;
import Interfaces.IVista;
import java.util.Collections;
import java.util.List;

public class LobbyController {

    private final IVista vista;
    private final Lobby modelo;

    public LobbyController(IVista vista, Lobby modelo) {
        if (vista == null || modelo == null) {
            throw new IllegalArgumentException("Vista y modelo son obligatorios");
        }
        this.vista = vista;
        this.modelo = modelo;
        this.modelo.agregarObservador(this.vista);
    }

    public boolean agregarJugador(String nombreJugador) {
        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            vista.mostrarMensaje("El nombre del jugador es obligatorio");
            return false;
        }
        modelo.agregarJugador(nombreJugador.trim());
        return true;
    }

    public boolean iniciarPartida() {
        if (modelo.getNombreJugadores().size() >= 2) {
            vista.cerrarVista();
            return true;
        }
        vista.mostrarMensaje("Se requieren al menos 2 jugadores para iniciar");
        return false;
    }

    public void actualizarListaDesdeRed(dtos.MensajeListaJugadoresDTO dto) {
        if (dto == null) return;
        modelo.limpiarJugadores();

        for (String nombre : dto.getJugadores()) {
            modelo.agregarJugador(nombre);
        }
    }

    public List<String> obtenerJugadores() {
        return Collections.unmodifiableList(modelo.getNombreJugadores());
    }
}
