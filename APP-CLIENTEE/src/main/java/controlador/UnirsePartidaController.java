/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import cliente.ClienteProxy;
import dtos.MensajeDTO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import vista.LobbyView;
import vista.unirsePartidaView;

/**
 *
 * @author USER
 */
public class UnirsePartidaController {
    private unirsePartidaView vista;
    private ClienteProxy proxy;

    public UnirsePartidaController(unirsePartidaView vista, ClienteProxy proxy) {
        this.vista = vista;
        this.proxy = proxy;
    }
    
    private void solicitarUnirse(String nombreInvitado, String codigoSala){
        System.out.println("[UNISER PARTIDA CONTROLLER] Solicitando unirse ala partida");
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_UNIRSE_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreInvitado);
        peticion.getDatos().put("codigoSala", codigoSala);
        proxy.enviarMensaje(peticion);
        
    }
    private void escucharEventoRed(MensajeDTO mensaje){
        if(mensaje == null){
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if(mensaje.getTipo().equals("UNIDO_EXITO")){
                String codigoSala = (String) mensaje.getDatos().get("codigoSala");
                String nombre = (String) mensaje.getDatos().get("nombre");
                
                vista.dispose();
                LobbyView lobby = new LobbyView();
                LobbyController control = new LobbyController(proxy, codigoSala, nombre,false, lobby);
                lobby.setVisible(true);
                
            }else if(mensaje.getTipo().equals("ERROR_UNIRSE")){
                String motivo = (String) mensaje.getDatos().get("motivo");
                JOptionPane.showMessageDialog(vista, "Error" + motivo + "no se pudo unirse ala partida"+ JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
