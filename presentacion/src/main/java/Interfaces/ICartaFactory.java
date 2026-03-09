/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Interfaces;

import modelo.Carta;
import modelo.enums.Color;
import modelo.enums.TipoAccion;


/**
 *
 * @author USER
 */
public interface ICartaFactory {
    Carta crearNumerica(String id, Color color, int numero);
    Carta crearAccion(String id, Color color, TipoAccion accion);
    Carta crearComodin(String id, Color color, boolean esMasCuatro);
    
}
