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
public class CartaAccion extends Carta{
    private TipoAccion accion;

    public CartaAccion(TipoAccion accion, String id, Color color) {
        super(id, color);
        this.accion = accion;
    }

    public TipoAccion getAccion() {
        return accion;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }
    
    
}
