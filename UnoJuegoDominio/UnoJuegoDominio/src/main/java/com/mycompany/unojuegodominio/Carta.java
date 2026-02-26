/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.unojuegodominio;

import com.mycompany.unojuegodominio.enums.Color;

/**
 *
 * @author LABCISCO-PC080
 */
public class Carta {
    protected String id;
    protected Color color;

    public Carta(String id, Color color) {
        this.id = id;
        this.color = color;
    }
    
        
        
    public boolean esJugable(Carta carta){
    
        return true;
    }
    
    public boolean aplicarEfecto(){
    
        return true;
    }

    public  String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
    
    
}
