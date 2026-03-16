package Estados;

import Entidades.Carta;
import Entidades.Jugador;
import Logica.Partida;

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
       if(!partida.getJugadorActual().equals(jugador)){
           System.out.println("no es turno" + jugador.getNombre());
           return;
       }
       Carta cartaEnMesa = partida.getPilaCartas().obtenerUltimaCarta();
       if(jugador.getMano().getCartas().isEmpty()){
           System.out.println(jugador.getNombre() + "ha dicho UNO y ganado la partida");
           partida.setEstado(new EstadoFinalizada());
       }
    }
}
