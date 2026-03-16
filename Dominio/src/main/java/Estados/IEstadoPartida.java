package Estados;

import Entidades.Carta;
import Entidades.Jugador;
import Logica.Partida;

public interface IEstadoPartida {
    void agregarJugador(Partida partida, Jugador jugador);
    void iniciarPartida(Partida partida);
    void jugarCarta(Partida partida, Jugador jugador, Carta carta);
}
