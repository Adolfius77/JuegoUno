/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador.Factorys;

import modelo.Carta;
import modelo.CartaAccion;
import modelo.CartaNumerica;
import modelo.cartaComodin;
import modelo.enums.Color;
import modelo.enums.TipoAccion;

/**
 *
 * @author USER
 */
public class CartaFactory {
    public static Carta crearNumerica(String id, Color color, int numero){
        return new CartaNumerica(numero, id, color);
    }
    public static Carta crearAccion(String id, Color color, TipoAccion accion){
        return  new CartaAccion(accion, id, color);
    }
    public static Carta crearComodin(String id, Color color, boolean esMasCuatro){
        return new cartaComodin(esMasCuatro, id, color);
    }
}
