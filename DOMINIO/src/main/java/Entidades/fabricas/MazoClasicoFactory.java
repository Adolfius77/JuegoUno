/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades.fabricas;


import Entidades.Carta;
import Entidades.Mazo;
import Entidades.enums.Color;
import Entidades.enums.TipoAccion;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author USER
 */
public class MazoClasicoFactory implements IMazoFactory {

    @Override
    public Mazo crearMazo(ICartaFactory cartaFactory) {
        List<Carta> cartasNuevas = new ArrayList();
        for (Color color : Color.values()) {
            if (color == Color.NEGRO) continue;
            //cartas numericas wey
            for (int numero = 0; numero <= 9; numero++) {
                cartasNuevas.add(cartaFactory.crearNumerica("n1" + numero, color, numero));
            }
            //cartas de accion bro
            cartasNuevas.add(cartaFactory.crearAccion("reversa", color, TipoAccion.REVERSA));
            cartasNuevas.add(cartaFactory.crearAccion("saltar", color, TipoAccion.SALTAR));
            cartasNuevas.add(cartaFactory.crearAccion("mas2", color, TipoAccion.MAS_2));

        }
        //comodines bro
        for (int i = 0; i < 4; i++) {
            cartasNuevas.add(cartaFactory.crearComodin("comodin", Color.NEGRO, false));
            cartasNuevas.add(cartaFactory.crearComodin("mas4", Color.NEGRO, true));

        }

        return new Mazo(cartasNuevas);
    }
}
