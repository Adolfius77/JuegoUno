package Entidades.Estados;

import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;

public class EstadoFinalizada implements IEstadoPartida {
    @Override
    public void agregarJugador(Partida partida, Jugador jugador) {
        System.out.println("la partida ya termino");
    }

    @Override
    public void iniciarPartida(Partida partida) {
        System.out.println("la partida ya termino");
    }

    @Override
    public void jugarCarta(Partida partida, Jugador jugador, Carta carta) {
        System.out.println("la partida ya termino ya no puedes jugar mas cartas");
    }
}
