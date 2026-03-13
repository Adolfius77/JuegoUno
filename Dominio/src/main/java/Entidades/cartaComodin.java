/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;


import Logica.Partida;
import Entidades.Carta;
import Entidades.enums.Color;



/**
 *
 * @author LABCISCO-PC080
 */
public class cartaComodin extends Carta{
    private final boolean EsMasCuatro;

    public cartaComodin(boolean EsMasCuatro, String id, Color color) {
        super(id, color.NEGRO);
        this.EsMasCuatro = EsMasCuatro;
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
        return true;
    }

    @Override
    public boolean aplicarEfecto(Partida partida) {
        if(this.EsMasCuatro){
            partida.acomularCartas(4);
            partida.saltarTurno();
        }
        return true;
    }
    public boolean esMasCuatro(){
        return EsMasCuatro;
    }
    
}
