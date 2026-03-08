/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador.Factorys;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import modelo.Jugador;
import modelo.Mazo;
import modelo.Partida;

/**
 *
 * @author emiim
 */
public class PartidaFactory {
    public static Partida fabricadorPartida(List<String> nombreJugadores){
        List<Jugador> jugadores = new ArrayList<>();
        for (String nombre: nombreJugadores) {
            String id = UUID.randomUUID().toString();
            jugadores.add(new Jugador(id, nombre));
        }
        
        Partida partida = new Partida(jugadores); 
        Mazo mazo = MazoFactory.generarMazo();
        partida.setMazo(mazo);
        return partida;
    }
}
