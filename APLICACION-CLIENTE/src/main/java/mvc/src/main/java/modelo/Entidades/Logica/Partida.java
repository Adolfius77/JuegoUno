/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.Entidades.Logica;

import java.util.ArrayList;
import java.util.List;
import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Mazo;
import Entidades.PilaCartas;
import Entidades.enums.Sentido;
import Estados.IEstadoPartida;
import fabricas.EstadoFactory;
import Observer.IObservable;
import Observer.IObserver;

/**
 *
 * @author santi
 */
public class Partida implements IObservable {

    private String id;
    private IEstadoPartida estado;
    private Sentido sentido = Sentido.HORARIO;
    private List<Jugador> jugadores;
    private Mazo mazo;
    private PilaCartas pilaCartas;
    private Boolean saltarTurno = false;
    private int turnoActual = 0;
    private List<IObserver> observadores;

    public Partida(List<Jugador> jugadores, Mazo mazo, PilaCartas pilaCartas) {
        this.jugadores = jugadores != null ? jugadores : new ArrayList<>();
        this.mazo = mazo;
        this.pilaCartas = pilaCartas;
        
        this.estado = EstadoFactory.crearEstadoEsperando();
        this.id = java.util.UUID.randomUUID().toString(); 
        this.sentido = Sentido.HORARIO;
        this.turnoActual = 0;
        this.saltarTurno = false;
        this.observadores = new ArrayList<>();
    }

    public void iniciar() {
        estado.iniciarPartida(this);
    }

    public Jugador agregarJugador(Jugador jugador) {
        estado.agregarJugador(this,jugador);
        return jugador;
    }

    public static void crear() {

    }

    public void verificarGanador() {

    }

    public void jugarCarta(Carta carta, Jugador jugador) {
       estado.jugarCarta(this, jugador, carta);
    }

    public void tomarCarta(Jugador jugador) {
        if (mazo.estaVacio()) {
            mazo.recargar(pilaCartas);
        }
        Carta cartaNueva = mazo.tomarCarta();
        jugador.recibirCarta(cartaNueva);
        notificarObservador("CARTA_TOMADA");
    }

    public Jugador getJugadorActual() {
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
    //Getters y setters
    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public IEstadoPartida getEstado() {
        return this.estado;
    }

    public void setEstado(IEstadoPartida nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void setMazo(Mazo mazo) {
        this.mazo = mazo;
    }

    public Mazo getMazo() {
        return mazo;
    }

    public PilaCartas getPilaCartas() {
        return pilaCartas;
    }

    public void setPilaCartas(PilaCartas pilaCartas) {
        this.pilaCartas = pilaCartas;
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
