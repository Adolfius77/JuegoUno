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
public class cartaComodin extends Carta{
    private Color colorElegido; 
    private boolean tomarCuatro;

    public cartaComodin(Color colorElegido, boolean tomarCuatro, String id, Color color) {
        super(id, color);
        this.colorElegido = colorElegido;
        this.tomarCuatro = tomarCuatro;
    }

    public Color getColorElegido() {
        return colorElegido;
    }

    public boolean isTomarCuatro() {
        return tomarCuatro;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
    
    
}
