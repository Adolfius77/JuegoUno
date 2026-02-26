/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import modelo.CartaNumerica;
import modelo.Carta;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LABCISCO-PC080
 */
public class Mano {
    private List<Carta> cartas;

    public Mano() {
        cartas = new ArrayList<Carta>();
    }

    public Mano(List<Carta> cartas) {
        this.cartas = cartas;
    }
    
    public Carta agregarCarta(Carta carta){
        cartas.add(carta);
        
        return carta;
    }
    
    public Carta eliminarCarta(Carta carta){
        cartas.remove(carta);
        
        return carta;
    }
    
    public List<Carta> cartasJugables(Carta cartaActiva){
        if (cartaActiva instanceof  CartaNumerica) {
         return cartas;   
        }
        else{
         return cartas;
        }       
    }
}
