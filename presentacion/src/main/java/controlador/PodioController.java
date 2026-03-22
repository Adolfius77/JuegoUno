package controlador;

import Entidades.Jugador;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PodioController {

    public List<Jugador> calcularRanking(List<Jugador> jugadores) {
        if (jugadores == null) {
            return new ArrayList<>();
        }

        List<Jugador> ranking = new ArrayList<>(jugadores);
        ranking.sort(Comparator.comparingInt(Jugador::getPuntaje).reversed());
        return ranking;
    }

    public Jugador obtenerGanador(List<Jugador> jugadores) {
        List<Jugador> ranking = calcularRanking(jugadores);
        if (ranking.isEmpty()) {
            return null;
        }
        return ranking.getFirst();
    }
}
