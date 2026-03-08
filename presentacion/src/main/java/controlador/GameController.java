/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Interfaces.IVista;
import controlador.Factorys.PartidaFactory;
import java.util.List;
import modelo.Carta;
import modelo.Jugador;
import modelo.Partida;
import vista.DiseñosExtras.GameView;

/**
 *
 * @author USER
 */
public class GameController {

    private final Partida modelo;
    private final IVista vista;

    public GameController(Partida modelo, IVista vista, List<String> nombreJugadores) {
        this.modelo = modelo;
        this.vista = vista;
        this.modelo.agregarObservador(this.vista);

    }

    public void inicarJuego() {
        modelo.iniciar();
        vista.mostrarVista();
    }

    public void jugarCarta(Jugador jugador, Carta carta) {
        try {
            Jugador jugadorActual = modelo.getJugadorActual();
            if (jugador.equals(jugadorActual)) {
                modelo.jugarCarta(carta, jugadorActual);
            } else {
                vista.mostrarMensaje("no es turno de este jugador");
            }

        } catch (Exception e) {
            vista.mostrarMensaje("a ocurrido un error inesperado " + e.getMessage());
        }
    }

    public void tomarCarta() {
        try {
            Jugador jugadorActual = modelo.getJugadorActual();
            modelo.tomarCarta(jugadorActual);
        } catch (Exception e) {
            vista.mostrarMensaje(e.getMessage());
        }

    }

    public void decirUno(Jugador jugador) {
        if (jugador.getMano().getCartas().size() == 1) {
            System.out.println("el jugador " + jugador + "a dicho UNO");
        } else {
            System.out.println("no grito uno se le dan 2 cartas");
            modelo.tomarCarta(jugador);
            modelo.tomarCarta(jugador);
        }
    }

    public void pasarrTurno() {
        modelo.pasarTurno();
    }
}
