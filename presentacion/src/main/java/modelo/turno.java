/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.List;
import modelo.enums.Sentido;

/**
 *
 * @author adolfo
 */
public class turno {
    private int indiceActual;
    private Sentido sentido;
    private List<Jugador> jugadores;

    public turno(int indiceActual, Sentido sentido, List<Jugador> jugadores) {
        this.indiceActual = 0;
        this.sentido = Sentido.HORARIO;
        this.jugadores = jugadores;
    }
    public Jugador getJugadorActual(){
        return jugadores.get(indiceActual);
    }
    public Sentido getSentido(){
        return sentido;
    }
    public void cambiarSentido(){
        if(this.sentido == Sentido.HORARIO){
            this.sentido = Sentido.ANTIHORARIO;
        }else{
            this.sentido = Sentido.HORARIO;
        }
    }
    public void avanzarTurno(int saltos){
        int cantidad = jugadores.size();
        
        if(sentido == Sentido.HORARIO){
            indiceActual = (indiceActual + saltos) %cantidad;
            
            if(indiceActual < 0){
                indiceActual += cantidad;
            }
        }
    }
}
