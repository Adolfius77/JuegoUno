/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.unojuegodominio;

import com.mycompany.unojuegodominio.enums.EstadoPartida;
import com.mycompany.unojuegodominio.enums.Sentido;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author santi
 */
public class Partida {
    private String id;
    private EstadoPartida estado;
    private Sentido sentido = Sentido.HORARIO;
    private List<Jugador> jugadores;
    private Mazo mazo;
    private PilaCartas pilaCartas;
    private Boolean saltarTurno = false;
    
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
    
    
    
}
