/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import cliente.ClienteProxy;
import dtos.MensajeDTO;
import javax.swing.SwingUtilities;
import vista.CrearPartida;
import vista.LobbyView;

/**
 *
 * @author USER
 */
public class CrearPartidaController {

    private CrearPartida vista;
    private ClienteProxy proxy;

    public CrearPartidaController(CrearPartida vista, ClienteProxy proxy) {
        this.vista = vista;
        this.proxy = proxy;
        this.proxy.setReceptor(mensaje -> procesarEventoRed(mensaje));

    }

    public void solicitarCreacion(String nombreHost) {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_CREAR_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreHost);
        System.out.println("[PARTIDA CONTROLLER] enviando peticion para crear partida");
        proxy.enviarMensaje(peticion);
    }

    private void procesarEventoRed(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        if (mensaje.getTipo().equals("SALA_CREADA")) {
            String codigoSala = (String) mensaje.getDatos().get("codigoSala");
            String nombreHost = (String) mensaje.getDatos().get("nombre");

            SwingUtilities.invokeLater(() -> {

                vista.dispose();

                LobbyView lobby = new LobbyView();
                LobbyController lobbyCtrl = new LobbyController(proxy, codigoSala, nombreHost, true, lobby);
                lobby.setVisible(true);
            });
        }
    }

}
