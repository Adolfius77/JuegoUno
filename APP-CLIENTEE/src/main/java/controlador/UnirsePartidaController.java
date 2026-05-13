/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;
import java.util.List;
import javax.swing.SwingUtilities;
import vista.LobbyView;
import vista.unirsePartidaView;

/**
 *
 * @author USER
 */
public class UnirsePartidaController {

    private IVista vista;
    private ClienteProxy proxy;
    private String nombreInvitadoTemporal;

    public UnirsePartidaController(unirsePartidaView vista, ClienteProxy proxy) {
        this.vista = vista;
        this.proxy = proxy;
        this.proxy.setReceptor(mensaje -> escucharEventoRed(mensaje));
    }

    public void solicitarUnirse(String nombreInvitado, String codigoSala) {
        this.nombreInvitadoTemporal = nombreInvitado;
        System.out.println("[UNISER PARTIDA CONTROLLER] Solicitando unirse ala partida");
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_UNIRSE_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreInvitado);
        peticion.getDatos().put("codigoSala", codigoSala);
        proxy.enviarMensaje(peticion);

    }

    private void escucharEventoRed(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        
        if (mensaje.getTipo().equals("UNIDO_EXITO")) {
            String codigoSala = (String) mensaje.getDatos().get("codigoSala");
            String nombre = (String) mensaje.getDatos().getOrDefault("nombre", nombreInvitadoTemporal);
            List<?> jugadores = null;
            Object jugadoresRaw = mensaje.getDatos().get("jugadores");
            if (jugadoresRaw instanceof List<?>) {
                jugadores = (List<?>) jugadoresRaw;
            }

            LobbyView lobby = new LobbyView();
            LobbyController control = new LobbyController(proxy, codigoSala, nombre, false, lobby);
            control.cargarDatosIniciales(jugadores);

            SwingUtilities.invokeLater(() -> {
                vista.cerrarVista();
                lobby.setVisible(true);
            });

        } else if (mensaje.getTipo().equals("ERROR_UNIRSE")) {
            SwingUtilities.invokeLater(() -> {
                String motivo = (String) mensaje.getDatos().get("motivo");
                if (vista != null) {
                    vista.mostrarMensaje("Error: " + motivo + " - No se pudo unirse a la partida.");

                }
            });
        }
    }
}
