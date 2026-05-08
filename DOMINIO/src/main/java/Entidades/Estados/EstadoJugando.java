package Entidades.Estados;

import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.enums.Color;
import Entidades.fabricas.EstadoFactory;

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
        Color colorActivo = partida.getPilaCartas().getColorActivo(); 

        if (carta.esJugable(cartaEnMesa, colorActivo)) { 
            jugador.getMano().eliminarCarta(carta);
            partida.getPilaCartas().agregarCarta(carta);
            carta.aplicarEfecto(partida);
            partida.notificarObservador("CARTA_JUGADA");
            if (jugador.getMano().getCartas().isEmpty()) {
                System.out.println(jugador.getNombre() + " ganó");
                partida.setEstado(EstadoFactory.crearEstadoFinalizada());
                return;
            }
            partida.pasarTurno();
        } else {
            System.out.println("Jugada invalida.");
        }
    }
}
