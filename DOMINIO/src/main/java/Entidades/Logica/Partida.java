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

    public Partida(List<Jugador> jugadores, Mazo mazo, PilaCartas pilaCartas, IEstadoPartida estado) {
        this.jugadores = jugadores != null ? jugadores : new ArrayList<>();
        this.mazo = mazo;
        this.pilaCartas = pilaCartas;
        this.estado = estado;
        this.id = java.util.UUID.randomUUID().toString();
        this.sentido = Sentido.HORARIO;
        this.turnoActual = 0;
        this.saltarTurno = false;
        this.observadores = new ArrayList<>();
    }

    public void iniciar() {
        estado.iniciarPartida(this);
    }

    public void verificarGanador() {
        for (Jugador jugador : jugadores) {
            if (jugador.getMano() != null && jugador.getMano().getCartas().isEmpty()) {
                this.setEstado(EstadoFactory.crearEstadoFinalizada());
                notificarObservador("PARTIDA_FINALIZADA:" + jugador.getNombre());
                return;
            }
        }
    }

    public void jugarCarta(Carta carta, Jugador jugador) {
        estado.jugarCarta(this, jugador, carta);
        jugador.setDijoUno(false);
        verificarGanador();
    }

    public void tomarCarta(Jugador jugador) {
        if (mazo.estaVacio()) {
            mazo.recargar(pilaCartas);
        }
        Carta cartaNueva = mazo.tomarCarta();
        jugador.recibirCarta(cartaNueva);
        notificarObservador("ACTUALIZACION_MESA");
    }

    public Jugador getJugadorActual() {
        if (jugadores == null || jugadores.isEmpty()) {
            return null;
        }
        return jugadores.get(turnoActual);
    }

    public void saltarTurno() {
        this.saltarTurno = true;

    }

    public void cambiarSentido() {
        if (this.sentido == Sentido.HORARIO) {
            this.sentido = Sentido.ANTIHORARIO;
        } else {
            this.sentido = Sentido.HORARIO;
        }
        if (jugadores.size() == 2) {
            this.saltarTurno = true;
        }
        notificarObservador("SENTIDO_CAMBIADO");
    }

    public void acomularCartas(int cantidad) {
        if (mazo.estaVacio()) {
            mazo.recargar(pilaCartas);
        }

        if (jugadores == null || jugadores.isEmpty()) {
            return;
        }
        int destinoIdx = calcularSiguienteIndice();
        destinoIdx = ((destinoIdx % jugadores.size()) + jugadores.size()) % jugadores.size();
        Jugador destino = jugadores.get(destinoIdx);

        Jugador actual = getJugadorActual();
        System.out.println("[DEBUG] acomularCartas -> cantidad=" + cantidad + ", turnoActualIdx=" + turnoActual
                + ", jugadorActual=" + (actual != null ? actual.getNombre() : "<null>")
                + ", destinoIdx=" + destinoIdx + ", destino=" + (destino != null ? destino.getNombre() : "<null>"));

        for (int i = 0; i < cantidad; i++) {
            destino.recibirCarta(mazo.tomarCarta());
        }
        notificarObservador("CARTAS_ACUMULADAS");
    }

    public void pasarTurno() {

        Jugador jugadorAnterior = jugadores.get(turnoActual);

        if (jugadorAnterior.getMano() != null && jugadorAnterior.getMano().getCartas().size() == 1) {
            if (!jugadorAnterior.isDijoUno()) {
                System.out.println("¡" + jugadorAnterior.getNombre() + " no dijo UNO! Castigo de 3 cartas.");

                for (int i = 0; i < 3; i++) {
                    if (mazo.estaVacio()) {
                        mazo.recargar(pilaCartas);
                    }
                    jugadorAnterior.recibirCarta(mazo.tomarCarta());
                }
            }
        }

        jugadorAnterior.setDijoUno(false);

        turnoActual = calcularSiguienteIndice();

        if (this.saltarTurno) {
            this.saltarTurno = false;
            pasarTurno();
        } else {
            notificarObservador("TURNO_CAMBIADO");
        }
    }

    public Jugador agregarJugador(Jugador jugador) {
        if (this.jugadores.isEmpty()) {
            jugador.setEsHost(true);
        }
        estado.agregarJugador(this, jugador);
        return jugador;
    }

    public int calcularSiguienteIndice() {
        int numeroJugadores = jugadores.size();
        if (numeroJugadores == 0) {
            return 0;
        }

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
            obs.actualizar(evento);
        }
    }

}
