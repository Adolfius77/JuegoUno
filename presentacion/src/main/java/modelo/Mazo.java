/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.enums.Color;
import modelo.enums.TipoAccion;

/**
 *
 * @author santi
 */
public class Mazo {
    
    private List<Carta> listaCartas;
    
    public Mazo(){
        inicializar();
    }
    
    public void inicializar(){
        listaCartas = new ArrayList<Carta>();
          for (Color color : Color.values()) {

            for (int numero = 0; numero <= 9; numero++) {
                listaCartas.add(new CartaNumerica("N" + numero, color, numero));
                listaCartas.add(new CartaNumerica("N" + numero, color, numero));
            }

            for (int i = 0; i < 2; i++) {
                listaCartas.add(new CartaAccion(TipoAccion.REVERSA, "REV", color));
                listaCartas.add(new CartaAccion(TipoAccion.SALTAR, "SAL", color));
                listaCartas.add(new CartaAccion(TipoAccion.MAS_2, "MAS2", color));
            }
        }

        for (int i = 0; i < 4; i++) {
            listaCartas.add(new cartaComodin(null, false, "COMODIN", null));
        }

        for (int i = 0; i < 2; i++) {
            listaCartas.add(new cartaComodin(null, true, "MAS4", null));
        }
        
        barajear();
    }
    
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
    
    public List<Carta> entregarCartas(){
        List<Carta> cartasEntregables = new ArrayList<Carta>();
        for (int i = 0; i < 7; i++) {
            cartasEntregables.add(listaCartas.getLast());
            listaCartas.removeLast();
        }
        return cartasEntregables;
    }
    //falta el metodo esta vacio
}
