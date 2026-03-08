/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.util.ArrayList;
import java.util.List;
import modelo.Jugador;
import vista.LobbyView;

/**
 *
 * @author USER
 */
public class LobbyController {

    private final LobbyView lobby;
    private final List<String> nombreJugadores;

    public LobbyController(LobbyView lobby) {
        this.lobby = lobby;
        this.nombreJugadores = new ArrayList<>();
    }

    public void agregarJugador(String nombreJugador) {
        if (nombreJugadores.size() < 4 && !nombreJugador.trim().isEmpty()) {
            nombreJugadores.add(nombreJugador);
            // falta actualizar la lista de jugadores desde la vista
        } else {
            System.out.println("Sala llena o nombre invalido.");
        }
    }

    public void eliminarJugador(String nombreJugador) {
        if (nombreJugadores.remove(nombreJugador)) {
            System.out.println("jugador" + nombreJugador + "eliminado");
        }
    }

    public void iniciarPartida() {
        if (nombreJugadores.size() >= 2) {
            lobby.dispose();

        }
    }

}
