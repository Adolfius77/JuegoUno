/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.List;

/**
 *
 * @author santi
 */
public class Jugador {

    private String Id;
    private String nombre;
    private String avatar;
    private Mano mano;
    private boolean dijoUno;
    private int puntaje;
    private boolean esHost;
    private boolean estaListo;

    public Jugador() {
        this.dijoUno = false;
    }

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.dijoUno = false;
    }
    
    public Jugador(String id, String nombre) {
        this.Id = id;
        this.nombre = nombre;
        this.dijoUno = false;
    }

    public Carta recibirCarta(Carta carta) {
        return mano.agregarCarta(carta);

    }

    public void seleccionarCarta() {

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

    public void removerCarta(Carta carta) {
        if (this.mano != null) {
            this.mano.eliminarCarta(carta);
        }
    }

    public boolean isDijoUno() {
        return this.dijoUno;
    }

    public boolean isEstaListo() {
        return estaListo;
    }

    public void setEstaListo(boolean estaListo) {
        this.estaListo = estaListo;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    

    public Boolean getDijoUno() {
        return dijoUno;
    }

    public void setDijoUno(Boolean dijoUno) {
        this.dijoUno = dijoUno;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public boolean isEsHost() {
        return esHost;
    }

    public void setEsHost(boolean esHost) {
        this.esHost = esHost;
    }

    
}
