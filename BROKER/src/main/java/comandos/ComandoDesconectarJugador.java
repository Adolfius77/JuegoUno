/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import dtos.MensajeDTO;
import interfaces.IComandoServidor;

/**
 *
 * @author USER
 */
public class ComandoDesconectarJugador implements IComandoServidor {

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        System.out.println("[COMANDO-DESCONECTAR] Solicitud recibida, sin accion adicional configurada.");
    }
    
}
