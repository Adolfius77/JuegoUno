/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.unojuegodominio;

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
    
    public Carta recibirCarta(Carta carta){
        return mano.agregarCarta(carta);
    
    }
    
   
   
    
}
