/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.unojuegodominio;

import com.mycompany.unojuegodominio.enums.EstadoPartida;
import com.mycompany.unojuegodominio.enums.Sentido;
import java.util.List;

/**
 *
 * @author santi
 */
public class Partida {
    private String id;
    private EstadoPartida estado;
    private Sentido sentido;
    private List<Jugador> jugadores;
    private Mazo mazo;
    private PilaCartas pilaCartas;
    private Boolean saltarTurno;
    
    
}
