package Entidades.Estados;

import Entidades.Carta;
import Entidades.Estados.IEstadoPartida;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.Mano;
import Entidades.fabricas.EstadoFactory;

public class EstadoEsperando implements IEstadoPartida {
    private static final int MIN_JUGADORES = 2;
    private static final int MAX_JUGADORES = 4;
    private static final int CARTAS_INICIALES_POR_JUGADOR = 7;

    public static int getCartasInicialesPorJugador() {
        return CARTAS_INICIALES_POR_JUGADOR;
    }

    @Override
    public void agregarJugador(Partida partida, Jugador jugador) {
        if(partida == null){
            throw new IllegalArgumentException("la partida es obligatoria");
        }
        if(jugador == null){
            throw new IllegalArgumentException("la jugador es obligatoria");
        }
        if(jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()){
            throw new IllegalArgumentException("la nombre del jugador es obligatorio");
        }
        if(partida.getJugadores().size() >= MAX_JUGADORES){
            throw new IllegalArgumentException("la partida ya tiene el maximo de jugadores");
        }
        boolean repetido = partida.getJugadores().stream().anyMatch(j ->
                j.getId() != null && j.getId().equals(jugador.getId())) ||
                jugador.getNombre().equalsIgnoreCase(jugador.getNombre());
        if(repetido){
            throw new IllegalArgumentException("el jugador ya existe en la partida");
        }
        if(jugador.getMano() == null) {
            jugador.setMano(new Mano());
        }
        partida.getJugadores().add(jugador);
        partida.notificarObservador("JUGADOR_AGREGADO");
    }


    @Override
    public void iniciarPartida(Partida partida) {
        if (partida == null) {
            throw new IllegalArgumentException("La partida es obligatoria");
        }
        if (partida.getJugadores().size() < MIN_JUGADORES) {
            throw new IllegalStateException("No hay jugadores suficientes para iniciar");
        }
        if (partida.getJugadores().size() > MAX_JUGADORES) {
            throw new IllegalStateException("Hay más jugadores de los permitidos");
        }
        if (partida.getMazo() == null) {
            throw new IllegalStateException("No existe mazo para iniciar la partida");
        }
        if (partida.getPilaCartas() == null) {
            throw new IllegalStateException("No existe pila de descarte para iniciar la partida");
        }

        for (Jugador jugador : partida.getJugadores()) {
            if (jugador.getMano() == null) {
                jugador.setMano(new Mano());
            }
            jugador.entregarCartas(partida.getMazo().entregarCartas());
        }


        Carta primeraCarta = partida.getMazo().tomarCarta();
        partida.getPilaCartas().agregarCarta(primeraCarta);


        partida.setEstado(EstadoFactory.crearEstadoJugando());
        partida.notificarObservador("PARTIDA_INICIADA");
    }

    @Override
    public void jugarCarta(Partida partida, Jugador jugador, Carta carta) {
        System.out.println("no se puede jugar carta mientras el estado de la partida esta en esperando");
    }
}