/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;


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
public class Partida implements IObservable{
    private String id;
    private EstadoPartida estado;
    private Sentido sentido = Sentido.HORARIO;
    private List<Jugador> jugadores;
    private Mazo mazo;
    private PilaCartas pilaCartas;
    private Boolean saltarTurno = false;
    private List<IObserver> observadores;
    
    //falta turno
    //cartas pendientes a tomar
    
    public Partida(){
       mazo = new Mazo();
       pilaCartas = new PilaCartas();
       jugadores = new ArrayList<Jugador>();
       estado = EstadoPartida.ESPERANDO;
    }
    
    public Jugador agregarJugador(Jugador jugador){
        jugadores.add(jugador);
        return jugador;
    }
    
    public void iniciar(){
        
        for (Jugador jugador : jugadores) {
            jugador.entregarCartas(mazo.entregarCartas());
        }
        estado = EstadoPartida.EN_CURSO;
        pilaCartas.agregarCarta(mazo.tomarCarta());
    
    }
    //metodos que faltan
    //agregar jugador
    //verificar ganador
    //jugar carta
    //tomar carta
    //
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
        for(IObserver obs: observadores){
            obs.actualizar();
        }
    }
    
    
    
}
