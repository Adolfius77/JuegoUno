/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades.fabricas;

import Entidades.Carta;
import Entidades.CartaAccion;
import Entidades.CartaNumerica;
import Entidades.cartaComodin;
import Entidades.enums.Color;
import Entidades.enums.TipoAccion;




/**
 *
 * @author USER
 */
public class CartaFactory implements ICartaFactory{
    @Override
    public Carta crearNumerica(String id, Color color, int numero){
        return new CartaNumerica(numero, id, color);
    }
    @Override
    public Carta crearAccion(String id, Color color, TipoAccion accion){
        return  new CartaAccion(accion, id, color);
    }
    @Override
    public Carta crearComodin(String id, Color color, boolean esMasCuatro){
        return new cartaComodin(esMasCuatro, id, color);
    }
}
