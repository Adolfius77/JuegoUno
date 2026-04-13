package Entidades.fabricas;

import Entidades.Estados.EstadoEsperando;
import Entidades.Estados.EstadoFinalizada;
import Entidades.Estados.EstadoJugando;
import Entidades.Estados.IEstadoPartida;

public class EstadoFactory {
    public static IEstadoPartida crearEstadoEsperando() {
        return new EstadoEsperando();
    }

    public static IEstadoPartida crearEstadoJugando() {
        return new EstadoJugando();
    }

    public static IEstadoPartida crearEstadoFinalizada() {
        return new EstadoFinalizada();
    }

}
