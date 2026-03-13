/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Entidades.Lobby;
import Interfaces.IVista;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author USER
 */
public class LobbyController {

    private final IVista vista;
    private final Lobby modelo;

    public LobbyController(IVista vista, Lobby modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.modelo.agregarObservador(this.vista);
    }

    public void agregarJugador(String nombreJugador) {
        try {
            modelo.agregarJugador(nombreJugador);
        } catch (Exception e) {
            vista.mostrarMensaje(e.getMessage());
        }
    }

    public void iniciarPartida() {
        if (modelo.getNombreJugadores().size() >= 2) {
            vista.cerrarVista();
        } else {
            vista.mostrarMensaje("No hay jugadores partida");
        }
    }

}
