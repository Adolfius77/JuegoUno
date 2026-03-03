/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import modelo.Carta;
import modelo.enums.Color;


/**
 *
 * @author LABCISCO-PC080
 */
public class CartaNumerica extends Carta {
    
    private int valor;

    public CartaNumerica(int valor, String id, Color color) {
        super(id, color);
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
    
    

    @Override
    public boolean esJugable(Carta cartaTablero) {
        if(this.color == cartaTablero.getColor()) return true;
        
        if(cartaTablero instanceof CartaNumerica){
            return this.valor == ((CartaNumerica)cartaTablero).getValor();
            
        }
        return false;
    }

    @Override
    public boolean aplicarEfecto(Partida partida) {
        return true;
    }
    
}
