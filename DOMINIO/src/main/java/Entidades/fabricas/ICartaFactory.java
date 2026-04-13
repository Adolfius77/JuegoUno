/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades.fabricas;

import Entidades.Carta;
import Entidades.enums.Color;
import Entidades.enums.TipoAccion;



/**
 *
 * @author USER
 */
//familia de objetos lo hice como un abstrat factory
public interface ICartaFactory {
    Carta crearNumerica(String id, Color color, int numero);
    Carta crearAccion(String id, Color color, TipoAccion accion);
    Carta crearComodin(String id, Color color, boolean esMasCuatro);
    
}
