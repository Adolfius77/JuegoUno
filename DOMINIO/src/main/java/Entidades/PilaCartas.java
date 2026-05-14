/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;


import Entidades.enums.Color;

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
        List<Carta> cartasUsadas = new ArrayList<>();
        if (listaCartas.isEmpty()) {
            return cartasUsadas;
        }

        Carta ultimaCarta = listaCartas.getLast();
        for (int i = 0; i < listaCartas.size() - 1; i++) {
            cartasUsadas.add(listaCartas.get(i));
        }
        listaCartas.clear();
        listaCartas.add(ultimaCarta);
        colorActivo = ultimaCarta.getColor();
        return cartasUsadas;
    }
    
    public Carta obtenerUltimaCarta(){
        return listaCartas.getLast();
    }
    //getters y setters

    public List<Carta> getListaCartas() {
        return listaCartas;
    }

    public void setListaCartas(List<Carta> listaCartas) {
        this.listaCartas = listaCartas;
    }

    public Color getColorActivo() {
        return colorActivo;
    }

    public void setColorActivo(Color colorActivo) {
        this.colorActivo = colorActivo;
    }
}
