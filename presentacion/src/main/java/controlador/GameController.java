/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import controlador.Factorys.PartidaFactory;
import java.util.List;
import modelo.Carta;
import modelo.Jugador;
import modelo.Partida;
import vista.DiseñosExtras.GameView;

/**
 *
 * @author USER
 */
public class GameController {

    private final Partida modelo;
    private final GameView vista;

    public GameController(Partida modelo, GameView vista,  List<String> nombreJugadores) {     
        this.modelo = modelo;
        this.vista = vista;       
    }
    
    public void inicarJuego(){
        modelo.iniciar();
        vista.setVisible(true);
    }
    
    public void jugarCarta(Jugador jugador, Carta carta){
        try {
            Jugador jugadorActual = modelo.getJugadorActual();
            modelo.jugarCarta(carta, jugadorActual);
        } catch (Exception e) {
            System.err.println("Error!");
        }  
    }
   
    public void tomarCarta(){
        Jugador jugadorActual = modelo.getJugadorActual();
        modelo.tomarCarta(jugadorActual);
    }
    
}
