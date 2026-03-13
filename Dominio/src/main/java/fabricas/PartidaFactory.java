/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fabricas;

import Entidades.Jugador;
import Entidades.Mazo;
import Entidades.PilaCartas;
import Logica.Partida;
import java.util.List;



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
