/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import modelo.Carta;
import java.util.List;

/**
 *
 * @author santi
 */
public class Jugador {

    private String Id;
    private String nombre;
    private byte[] avatar;
    private Mano mano;
    private Boolean dijoUno;
    private int puntaje;

    public Jugador(String id, String nombre) {
        this.Id = id;
        this.nombre = nombre;
    }
     
    public Carta recibirCarta(Carta carta) {
        return mano.agregarCarta(carta);

    }
    public void seleccionarCarta(){
        
    }
    public Mano entregarCartas(List<Carta> cartasIniciales) {
        for (Carta cartasInicial : cartasIniciales) {
            mano.agregarCarta(cartasInicial);
        }
        return mano;
    }

    public Mano getMano() {
        return this.mano;
    }
    public void removerCarta(Carta carta){
        if(this.mano != null){
            this.mano.eliminarCarta(carta);
        }
    }
    public void setDijoUno() {
        this.dijoUno = dijoUno;
    }

    public boolean isDijoUno() {
        return this.dijoUno;
    }

}
