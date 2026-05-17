package Entidades.Estados;

import Entidades.*;
import Entidades.Logica.Partida;
import Entidades.fabricas.EstadoFactory;
import java.util.ArrayList;
import java.util.List;

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
                (j.getId() != null && j.getId().equals(jugador.getId()))
                || (j.getNombre() != null && j.getNombre().equalsIgnoreCase(jugador.getNombre())));
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
        Mazo mazoActual = partida.getMazo();
        PilaCartas pilaActual = partida.getPilaCartas();

        for(Jugador jugador : partida.getJugadores()){
            jugador.entregarCartas(mazoActual.entregarCartas());
        }
        Carta primeraCarta = mazoActual.tomarCarta();
        int intentos = 0;
        int maxIntentos = Math.max(1, mazoActual.getCantidadCartas() + 1);
        while (primeraCarta instanceof cartaComodin && intentos < maxIntentos) {
            mazoActual.devolverCartaAlFondo(primeraCarta);
            primeraCarta = mazoActual.tomarCarta();
            intentos++;
        }

        if (primeraCarta instanceof cartaComodin) {
            throw new IllegalStateException("No se pudo iniciar la partida con una carta normal en la mesa.");
        }
        pilaActual.agregarCarta(primeraCarta);
        System.out.println("EstadoEsperando: La primera carta en la mesa es de color: " + pilaActual.getColorActivo());
        partida.setEstado(EstadoFactory.crearEstadoJugando());
        System.out.println("Transicion exitosa: La partida ahora esta en EstadoJugando.");
    }

    @Override
    public void jugarCarta(Partida partida, Jugador jugador, Carta carta) {
        System.out.println("no se puede jugar carta mientras el estado de la partida esta en esperando");
    }
}