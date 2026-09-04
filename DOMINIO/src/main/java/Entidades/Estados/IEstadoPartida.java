package Entidades.Estados;

import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;

public interface IEstadoPartida {
    void agregarJugador(Partida partida, Jugador jugador);
    void iniciarPartida(Partida partida);
    void jugarCarta(Partida partida, Jugador jugador, Carta carta);

    /**
     * true si la partida admite jugadas. Antes se comprobaba con
     * getEstado().toString().contains("Jugando"), que dependia del
     * Object.toString() por defecto y por tanto del nombre de la clase.
     */
    default boolean estaEnCurso() {
        return false;
    }
}
