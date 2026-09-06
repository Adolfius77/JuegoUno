package Entidades.Logica;

import Entidades.Carta;
import Entidades.CartaNumerica;
import Entidades.Jugador;
import Entidades.Mano;
import Entidades.Mazo;
import Entidades.PilaCartas;
import Entidades.enums.Color;
import Entidades.fabricas.EstadoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * El paso de turno, con la vuelta de tuerca del salto.
 */
class PartidaTurnosTest {

    private static Jugador jugadorCon(String nombre, int cuantasCartas) {
        Jugador jugador = new Jugador(nombre);
        List<Carta> cartas = new ArrayList<>();
        for (int i = 0; i < cuantasCartas; i++) {
            cartas.add(new CartaNumerica(i, nombre + i, Color.ROJO));
        }
        jugador.setMano(new Mano(cartas));
        return jugador;
    }

    private static Mazo mazoCon(int cuantas) {
        List<Carta> cartas = new ArrayList<>();
        for (int i = 0; i < cuantas; i++) {
            cartas.add(new CartaNumerica(i % 10, "mazo" + i, Color.AZUL));
        }
        return new Mazo(cartas);
    }

    private static Partida partidaDe(Jugador... jugadores) {
        return new Partida(new ArrayList<>(List.of(jugadores)), mazoCon(20),
                new PilaCartas(), EstadoFactory.crearEstadoJugando());
    }

    @Test
    @DisplayName("saltar a alguien con una carta no le cuesta el castigo de UNO")
    void saltarNoCastigaAlSaltado() {
        Jugador ana = jugadorCon("Ana", 3);
        Jugador beto = jugadorCon("Beto", 1);
        beto.setDijoUno(false);   // como queda tras su propio pasarTurno
        Partida partida = partidaDe(ana, beto);

        // Ana juega algo que salta a Beto: un SALTAR, un +2 o un +4.
        partida.saltarTurno();
        partida.pasarTurno();

        assertEquals(1, beto.getMano().getCartas().size(),
                "a Beto lo saltaron, no jugo: no puede recibir el castigo por no gritar UNO");
        assertEquals("Ana", partida.getJugadorActual().getNombre(),
                "con dos jugadores, saltar al rival devuelve el turno a quien jugo");
    }

    @Test
    @DisplayName("quien termina su turno con una carta y no grito UNO si recibe el castigo")
    void elQueNoGritaSiEsCastigado() {
        Jugador ana = jugadorCon("Ana", 1);
        Jugador beto = jugadorCon("Beto", 3);
        ana.setDijoUno(false);
        Partida partida = partidaDe(ana, beto);

        partida.pasarTurno();

        assertEquals(1 + Partida.CASTIGO_NO_GRITAR_UNO, ana.getMano().getCartas().size());
        assertEquals("Beto", partida.getJugadorActual().getNombre());
    }

    @Test
    @DisplayName("si grito UNO, no hay castigo")
    void gritarUnoLibraDelCastigo() {
        Jugador ana = jugadorCon("Ana", 1);
        Jugador beto = jugadorCon("Beto", 3);
        ana.setDijoUno(true);
        Partida partida = partidaDe(ana, beto);

        partida.pasarTurno();

        assertEquals(1, ana.getMano().getCartas().size());
    }

    @Test
    @DisplayName("con tres jugadores, el salto se brinca a uno solo")
    void elSaltoSeBrincaAUnoSolo() {
        Jugador ana = jugadorCon("Ana", 3);
        Jugador beto = jugadorCon("Beto", 3);
        Jugador caro = jugadorCon("Caro", 3);
        Partida partida = partidaDe(ana, beto, caro);

        partida.saltarTurno();
        partida.pasarTurno();

        assertEquals("Caro", partida.getJugadorActual().getNombre(),
                "de Ana, saltando a Beto, le toca a Caro");
    }
}
