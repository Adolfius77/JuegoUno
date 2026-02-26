/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.unojuegodominio;

import com.mycompany.unojuegodominio.enums.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author santi
 */
public class PilaCartas {
    private List<Carta> listaCartas;
    private Color colorActivo;

    public PilaCartas() {
        listaCartas = new ArrayList<Carta>();
        
    }
    
    public Carta agregarCarta(Carta carta){
        listaCartas.add(carta);
        colorActivo = carta.getColor();
        
        return carta;
    }
    
    public List<Carta> vaciar(){
        List<Carta> cartasUsadas = listaCartas;
        Carta ultimaCarta = listaCartas.getLast();
        listaCartas.clear();
        listaCartas.add(ultimaCarta);
        return cartasUsadas;
    }
    
    public Carta obtenerUltimaCarta(){
        return listaCartas.getLast();
    }
    
    
}
