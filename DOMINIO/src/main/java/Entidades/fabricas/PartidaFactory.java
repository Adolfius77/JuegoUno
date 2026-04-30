/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades.fabricas;

import Entidades.Estados.IEstadoPartida;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.Mazo;
import Entidades.PilaCartas;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



/**
 *
 * @author emiim
 */
public final class PartidaFactory {
    private PartidaFactory() {
    }

    public static Partida crearPartida(List<Jugador> jugadores, ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estado) {
        Objects.requireNonNull(cartaFactory, "cartaFactory es obligatorio");
        Objects.requireNonNull(mazoFactory, "mazoFactory es obligatorio");
        Mazo mazoNuevo = mazoFactory.crearMazo(cartaFactory);
        PilaCartas pilaNueva = new PilaCartas();
        List<Jugador> listaJugadores = jugadores == null ? new ArrayList<>() : jugadores;
        return new Partida(listaJugadores, mazoNuevo, pilaNueva,estado);
    }

    //metodo viejo
    public static Partida fabricadorPartida(List<Jugador> jugadores, ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estado) {
        return crearPartida(jugadores, cartaFactory, mazoFactory, estado);
    }
}
