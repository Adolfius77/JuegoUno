/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if (listaCartas.isEmpty()) {
            throw new IllegalStateException("No hay cartas disponibles en el mazo");
        }
        Carta ultimaCarta = listaCartas.getLast();
        listaCartas.removeLast();
        return ultimaCarta;
    }

    public List<Carta> recargar(PilaCartas pilaCartas) {
        if (pilaCartas == null) {
            return listaCartas;
        }
        listaCartas.addAll(pilaCartas.vaciar());
        barajear();
        return listaCartas;
    }

    public List<Carta> entregarCartas() {
        List<Carta> cartasEntregables = new ArrayList<Carta>();
        int cartasAEntregar = Math.min(7, listaCartas.size());
        for (int i = 0; i < cartasAEntregar; i++) {
            cartasEntregables.add(listaCartas.getLast());
            listaCartas.removeLast();
        }
        return cartasEntregables;
    }

    public boolean estaVacio() {
        return listaCartas.isEmpty();
    }
    public int getCantidadCartas() {
        return listaCartas != null ? listaCartas.size() : 0;
    }
    public void agregarCarta(Carta carta){
        if(carta != null){
            listaCartas.add(carta);
            barajear();
        }
    }

    public void devolverCartaAlFondo(Carta carta) {
        if (carta != null) {
            listaCartas.add(0, carta);
        }
    }
}
