package fabricas;

public class EstadoFactory {
    public static Estados.IEstadoPartida crearEstadoEsperando() {
        return new Estados.EstadoEsperando();
    }

    public static Estados.IEstadoPartida crearEstadoJugando() {
        return new Estados.EstadoJugando();
    }

    public static Estados.IEstadoPartida crearEstadoFinalizada() {
        return new Estados.EstadoFinalizada();
    }

}
