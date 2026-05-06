/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import interfaces.IComandoServidor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import red.JuegoServidor;

/**
 *
 * @author USER
 */
public class comandoIniciarPartida implements IComandoServidor{
   
    private final ManejadorNodos ManejadorNodos;
    private final JuegoServidor JuegoServidor;

    public comandoIniciarPartida(ManejadorNodos ManejadorNodos, JuegoServidor JuegoServidor) {
        this.ManejadorNodos = ManejadorNodos;
        this.JuegoServidor = JuegoServidor;
    }
    
    
    @Override
    public void ejecutar(MensajeDTO mensaje) {
        List<String> jugadores = ManejadorNodos.obtenerNombresDeNodosConectados();
        if(jugadores.size() < 2){
            System.out.println("comando iniciar partida: faltan jugadores deben ser mas de 2 jugadores");
            return;
        }
        PartidaDTO estadoInicialDTO = JuegoServidor.iniciarNuevoJuego(jugadores);
        MensajeDTO estadoPartida = new MensajeDTO();
        estadoPartida.setTipo("PARTIDA_INICIADA");
        estadoPartida.setRemitente("SERVIDOR");
        
        //guardamos los datos en el sobre
        Map<String, Object> datos = new HashMap();
        datos.put("partida", estadoInicialDTO);
        estadoPartida.setDatos(datos);
        
        //enviamos el sobre a todos los jugadores
        for(NodoCliente nodo : ManejadorNodos.obtenerNodosConectados()){
            nodo.enviarMensaje(estadoPartida);
        }
        
    }
    
}
