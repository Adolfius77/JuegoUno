/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;

/**
 *
 * @author USER
 */
public class ComandoUnirsePartida implements IComandoServidor{
    private ManejadorNodos ManejadorNodos;

    public ComandoUnirsePartida(ManejadorNodos ManejadorNodos) {
        this.ManejadorNodos = ManejadorNodos;
    }
    

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        String nombreInviado = (String) mensaje.getDatos().get("nombre");
        String codigoSala = (String) mensaje.getDatos().get("codigoSala");
        
        System.out.println("[COMANDO-UNIRSE-PARTIDA] " + nombreInviado + "intenta unirse ala partida" + codigoSala) ;
        boolean salaExistente = true;
        
        MensajeDTO respuesta = new MensajeDTO();
        if(salaExistente){
            respuesta.setTipo("UNIDO_EXITO");
            respuesta.getDatos().put("codigoSala", codigoSala);
            respuesta.getDatos().put("nombre", nombreInviado);
            
        }else{
            respuesta.setTipo("ERROR_UNIRSE");
            respuesta.getDatos().put("motivo", "La sala no existe o el codigo es incorrecto");
        }
        respuesta.setRemitente("SERVIDOR");
        
        for(NodoCliente nodo: ManejadorNodos.obtenerNodosConectados()){
                nodo.enviarMensaje(respuesta);
        }
    }
    
    
}
