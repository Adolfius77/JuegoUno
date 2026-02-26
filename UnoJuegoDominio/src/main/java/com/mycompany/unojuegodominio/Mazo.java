/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.unojuegodominio;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author santi
 */
public class Mazo {
    
    private List<Carta> listaCartas;
    
    public List<Carta> barajear(){
        Collections.shuffle(listaCartas);
        return listaCartas;
    }
    
    public Carta tomarCarta(){
         Carta ultimaCarta = listaCartas.getLast();
         listaCartas.removeLast();
         return ultimaCarta;
    }
    
    public List<Carta> recargar(PilaCartas pilaCartas){
        listaCartas.addAll(pilaCartas.vaciar());
        return listaCartas;
    }
}
