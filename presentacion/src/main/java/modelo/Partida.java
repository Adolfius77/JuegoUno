/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import controlador.Factorys.MazoFactory;
import java.util.ArrayList;
import java.util.List;
import modelo.enums.EstadoPartida;
import modelo.enums.Sentido;
import observador.IObservable;
import observador.IObserver;

/**
 *
 * @author santi
 */
public class Partida implements IObservable {

    private String id;
    private EstadoPartida estado;
    private Sentido sentido = Sentido.HORARIO;
    private List<Jugador> jugadores;
    private Mazo mazo;
    private PilaCartas pilaCartas;
    private Boolean saltarTurno = false;
    private int turnoActual = 0;
    private List<IObserver> observadores;

    public Partida() {
        Mazo mazoPartida = MazoFactory.generarMazo();
        pilaCartas = new PilaCartas();
        jugadores = new ArrayList<Jugador>();
        estado = EstadoPartida.ESPERANDO;
    }

    public Partida(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public void iniciar() {

        for (Jugador jugador : jugadores) {
            jugador.entregarCartas(mazo.entregarCartas());
        }
        estado = EstadoPartida.EN_CURSO;
        pilaCartas.agregarCarta(mazo.tomarCarta());

    }

    public Jugador agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
        return jugador;
    }

    public static void crear() {

    }

    public void verificarGanador() {

    }

    public void jugarCarta(Carta carta, Jugador jugador) {
        Carta carta1 = pilaCartas.obtenerUltimaCarta();
        if (carta.esJugable(carta1)) {
            jugador.getMano().eliminarCarta(carta);
            pilaCartas.agregarCarta(carta);
            carta.aplicarEfecto(this);
            pasarTurno();
            notificarObservador("CARTA_JUGADA");
        } else {
            throw new IllegalArgumentException("La carta no coincide con  el color o el numero que esta encima.");
        }
    }

    public void tomarCarta(Jugador jugador) {
        if (mazo.estaVacio()) {
            mazo.recargar(pilaCartas);
        }
        Carta cartaNueva = mazo.tomarCarta();
        jugador.recibirCarta(cartaNueva);
        notificarObservador("CARTA_TOMADA");
    }

    public Jugador getJugadorActual(){
        return jugadores.get(turnoActual);
    }
            
    public void saltarTurno() {

    }

    public void cambiarSentido() {

    }

    public void acomularCartas(int cantidad) {

    }

    public void pasarTurno() {

    }

    public void calcularSiguienteIndice() {

    }

    public void avanzarSiguienteIndice() {

    }

    
    public void setMazo(Mazo mazo) {
        this.mazo = mazo;
    }

    public Mazo getMazo() {
        return mazo;
    }

    @Override
    public void agregarObservador(IObserver obs) {
        this.observadores.add(obs);
    }

    @Override
    public void eliminarObservador(IObserver obs) {
        this.observadores.remove(obs);
    }

    @Override
    public void notificarObservador(String evento) {
        for (IObserver obs : observadores) {
            obs.actualizar();
        }
    }

}
