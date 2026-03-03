/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador.Factorys;

import java.util.ArrayList;
import java.util.List;
import modelo.Carta;
import modelo.Mazo;
import modelo.enums.Color;
import modelo.enums.TipoAccion;

/**
 *
 * @author USER
 */
public class MazoFactory {
    public static Mazo generarMazo(){
        List<Carta> cartasNuevas = new ArrayList();
        for (Color color : Color.values()) {
            if (color == Color.NEGRO) continue;
            //cartas numericas wey
            for (int numero = 0; numero <= 9; numero++) {
                cartasNuevas.add(CartaFactory.crearNumerica("n1" + numero, color, numero));
            }
            //cartas de accion bro
            cartasNuevas.add(CartaFactory.crearAccion("reversa", color, TipoAccion.REVERSA));
            cartasNuevas.add(CartaFactory.crearAccion("saltar", color, TipoAccion.SALTAR));
            cartasNuevas.add(CartaFactory.crearAccion("mas2", color, TipoAccion.MAS_2));

        }
        //comodines bro
        for (int i = 0; i < 4; i++) {
            cartasNuevas.add(CartaFactory.crearComodin("comodin", Color.NEGRO, false));
            cartasNuevas.add(CartaFactory.crearComodin("mas4", Color.NEGRO, true));

        }

        return new Mazo(cartasNuevas);
    }
}
