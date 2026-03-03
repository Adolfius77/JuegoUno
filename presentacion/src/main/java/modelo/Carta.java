/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import modelo.enums.Color;

/**
 *
 * @author LABCISCO-PC080
 */
public abstract class Carta {

    protected String id;
    protected Color color;

    public Carta(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public abstract boolean esJugable(Carta cartaTablero) ;

    public abstract boolean aplicarEfecto(Partida partida) ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


   

}
