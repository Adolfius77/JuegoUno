/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;


import Entidades.Carta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Entidades.enums.Color;
import Entidades.enums.TipoAccion;

/**
 *
 * @author santi
 */
public class Mazo {

    private List<Carta> listaCartas;

    public Mazo(List<Carta> listaCartas) {
        this.listaCartas = listaCartas;
        barajear();
    }

    public List<Carta> barajear() {
        Collections.shuffle(listaCartas);
        return listaCartas;
    }

    public Carta tomarCarta() {
        Carta ultimaCarta = listaCartas.getLast();
        listaCartas.removeLast();
        return ultimaCarta;
    }

    public List<Carta> recargar(PilaCartas pilaCartas) {
        listaCartas.addAll(pilaCartas.vaciar());
        return listaCartas;
    }

    public List<Carta> entregarCartas() {
        List<Carta> cartasEntregables = new ArrayList<Carta>();
        for (int i = 0; i < 7; i++) {
            cartasEntregables.add(listaCartas.getLast());
            listaCartas.removeLast();
        }
        return cartasEntregables;
    }

    public boolean estaVacio() {
        return listaCartas.isEmpty();
    }
}
