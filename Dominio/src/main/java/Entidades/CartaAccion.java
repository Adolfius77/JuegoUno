/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;


import Logica.Partida;
import Entidades.Carta;
import Entidades.enums.Color;
import Entidades.enums.TipoAccion;

/**
 *
 * @author LABCISCO-PC080
 */
public class CartaAccion extends Carta {

    private TipoAccion accion;

    public CartaAccion(TipoAccion accion, String id, Color color) {
        super(id, color);
        this.accion = accion;
    }

    public TipoAccion getAccion() {
        return accion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean esJugable(Carta cartaTablero) {
        if(this.color == cartaTablero.getColor()) return true;
        
        if(cartaTablero instanceof CartaAccion){
            return this.accion == ((CartaAccion)cartaTablero).getAccion();
            
        }
        return false;
    }

    @Override
    public boolean aplicarEfecto(Partida partida) {
        switch(this.accion){
            case SALTAR: partida.saltarTurno(); break;
            case REVERSA: partida.cambiarSentido();break;
            case MAS_2: partida.acomularCartas(2); break;
        }
        return true;
    }

}
