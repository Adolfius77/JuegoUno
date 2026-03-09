/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador.Factorys;

import Interfaces.ICartaFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import modelo.Jugador;
import modelo.Mazo;
import modelo.Partida;
import modelo.PilaCartas;

/**
 *
 * @author emiim
 */
public class PartidaFactory {

    public static Partida fabricadorPartida(List<Jugador> jugadores , ICartaFactory cartaFactory) {
        Mazo mazoNuevo = MazoFactory.generarMazo(cartaFactory);
        PilaCartas pilaNueva = new PilaCartas();

        Partida partida = new Partida(jugadores, mazoNuevo, pilaNueva);
        return partida;
    }
}
