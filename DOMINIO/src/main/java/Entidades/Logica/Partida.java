/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades.Logica;

import Entidades.Carta;
import Entidades.Estados.IEstadoPartida;
import Entidades.Jugador;
import Entidades.Mazo;
import Entidades.PilaCartas;
import Entidades.enums.Sentido;
import Entidades.fabricas.EstadoFactory;
import Observer.IObservable;
import Observer.IObserver;

import java.util.ArrayList;
import java.util.List;

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


    public void verificarGanador() {
        for (Jugador jugador : jugadores) {
            if (jugador.getMano().getCartas().isEmpty()){
                this.setEstado(EstadoFactory.crearEstadoFinalizada());
                notificarObservador("PARTIDA_FINALIZADA" + jugador.getNombre());
                return;
            }
        }
    }

    public void jugarCarta(Carta carta, Jugador jugador) {
       estado.jugarCarta(this, jugador, carta);
       verificarGanador();
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
        this.saltarTurno = true;
        pasarTurno();
    }

    public void cambiarSentido() {
        if (this.sentido == Sentido.HORARIO) {
            this.sentido = Sentido.ANTIHORARIO;
        } else {
            this.sentido = Sentido.HORARIO;
        }
        notificarObservador("SENTIDO_CAMBIADO");
    }

    public void acomularCartas(int cantidad) {
        if (mazo.estaVacio()) {
            mazo.recargar(pilaCartas);
        }
        Jugador actual =  getJugadorActual();
        for (int i = 0; i < cantidad; i++) {
            actual.recibirCarta(mazo.tomarCarta());
        }
        notificarObservador("CARTAS_ACUMULADAS");
    }

    public void pasarTurno() {
        turnoActual = calcularSiguienteIndice();

        if (this.saltarTurno) {
            this.saltarTurno = false;
            pasarTurno();
        } else {
            notificarObservador("TURNO_CAMBIADO");
        }
    }

    public int calcularSiguienteIndice() {
        int numeroJugadores = jugadores.size();
        if (numeroJugadores == 0) return 0;

        int siguiente;
        if (this.sentido == Sentido.HORARIO) {
            siguiente = (turnoActual + 1) % numeroJugadores;
        } else {
            siguiente = (turnoActual - 1 + numeroJugadores) % numeroJugadores;
        }
        return siguiente;
    }

    public void avanzarSiguienteIndice() {
        this.turnoActual = calcularSiguienteIndice();
    }

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

    public Sentido getSentido() {
        return sentido;
    }

    public void setSentido(Sentido sentido) {
        this.sentido = sentido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
