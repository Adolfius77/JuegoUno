package modelo.Entidades.Estados;

import Entidades.Carta;
import Entidades.Jugador;
import modelo.Entidades.Logica.Partida;
import modelo.Entidades.fabricas.EstadoFactory;

public class EstadoJugando implements IEstadoPartida {
    @Override
    public void agregarJugador(Partida partida, Jugador jugador) {
        System.out.println("la partida ya esta en curso espera a que termine");
    }

    @Override
    public void iniciarPartida(Partida partida) {
        System.out.println("la partida ya esta iniciada");
    }

    @Override
    public void jugarCarta(Partida partida, Jugador jugador, Carta carta) {

        if (!partida.getJugadorActual().equals(jugador)) {
            System.out.println("no es turno " + jugador.getNombre());
            return;
        }
        Carta cartaEnMesa = partida.getPilaCartas().obtenerUltimaCarta();

        if (carta.esJugable(cartaEnMesa)) {

            jugador.getMano().eliminarCarta(carta);
            partida.getPilaCartas().agregarCarta(carta);

            carta.aplicarEfecto(partida);
            partida.notificarObservador("CARTA_JUGADA");

            if (jugador.getMano().getCartas().isEmpty()) {
                System.out.println(jugador.getNombre() + " ha dicho UNO y gano la partida");
                partida.setEstado(EstadoFactory.crearEstadoFinalizada());
                return;
            }
            partida.pasarTurno();

        } else {
            System.out.println("Jugada invalida. La carta no coincide con el color o simbolo de la mesa.");
        }
    }
}
