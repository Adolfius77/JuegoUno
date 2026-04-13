package Entidades.fabricas;

import Estados.EstadoEsperando;
import Estados.EstadoFinalizada;
import Estados.EstadoJugando;
import Estados.IEstadoPartida;

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
