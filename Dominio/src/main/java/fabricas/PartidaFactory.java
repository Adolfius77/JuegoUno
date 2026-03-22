/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fabricas;

import Entidades.Jugador;
import Entidades.Mazo;
import Entidades.PilaCartas;
import Logica.Partida;
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

    public static Partida crearPartida(List<Jugador> jugadores, ICartaFactory cartaFactory, IMazoFactory mazoFactory) {
        Objects.requireNonNull(cartaFactory, "cartaFactory es obligatorio");
        Objects.requireNonNull(mazoFactory, "mazoFactory es obligatorio");
        Mazo mazoNuevo = mazoFactory.crearMazo(cartaFactory);
        PilaCartas pilaNueva = new PilaCartas();
        List<Jugador> listaJugadores = jugadores == null ? new ArrayList<>() : jugadores;
        return new Partida(listaJugadores, mazoNuevo, pilaNueva);
    }

    //metodo viejo
    public static Partida fabricadorPartida(List<Jugador> jugadores, ICartaFactory cartaFactory, IMazoFactory mazoFactory) {
        return crearPartida(jugadores, cartaFactory, mazoFactory);
    }
}
