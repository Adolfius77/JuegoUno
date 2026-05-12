/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Interfaces.IVista;
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

    private IVista vista;
    private ClienteProxy proxy;
    private String nombreHostTemporal;

    public CrearPartidaController(CrearPartida vista, ClienteProxy proxy) {
        this.vista = vista;
        this.proxy = proxy;
        this.proxy.setReceptor(mensaje -> procesarEventoRed(mensaje));

    }

    public void solicitarCreacion(String nombreHost) {
        this.nombreHostTemporal = nombreHost;
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

            LobbyView lobby = new LobbyView();
            LobbyController lobbyCtrl = new LobbyController(proxy, codigoSala, nombreHost, true, lobby);
            SwingUtilities.invokeLater(() -> {

                vista.cerrarVista();
                lobby.setVisible(true);
            });
        }
    }

}
