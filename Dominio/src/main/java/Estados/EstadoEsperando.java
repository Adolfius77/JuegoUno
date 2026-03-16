package Estados;

import Entidades.Carta;
import Entidades.Jugador;
import Logica.Partida;
import fabricas.EstadoFactory;

public class EstadoEsperando implements  IEstadoPartida{
    @Override
    public void agregarJugador(Partida partida, Jugador jugador) {
        partida.getJugadores().add(jugador);
        System.out.println(jugador.getNombre() + "se a unido a la partida");
    }

    @Override
    public void iniciarPartida(Partida partida) {
        if(partida.getJugadores().size() >= 2){
            System.out.println("la partida a comenzado se estan repartiendo las cartas...");
            partida.setEstado(EstadoFactory.crearEstadoJugando());
        }else{
            System.out.println("no hay suficientes jugadores para iniciar la partida");
        }
    }

    @Override
    public void jugarCarta(Partida partida, Jugador jugador, Carta carta) {
        System.out.println("todavia no puedes jugar caryas por que todavia no empieza la partida");
    }
}
