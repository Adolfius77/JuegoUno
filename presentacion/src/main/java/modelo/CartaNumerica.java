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
    
    private int numero;
    
    public CartaNumerica(String id, Color color, int numero) {
        super(id, color);
        this.numero=numero;
    }

    public int getNumero() {
        return numero;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
    
    
    
}
