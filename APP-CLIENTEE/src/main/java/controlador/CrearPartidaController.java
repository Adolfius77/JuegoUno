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
        solicitarCreacion(nombreHost, "Sala de " + nombreHost, 4);
    }

    public void solicitarCreacion(String nombreHost, String nombreSala, int limiteJugadores) {
        this.nombreHostTemporal = nombreHost;
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_CREAR_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreHost);
        peticion.getDatos().put("nombreSala", nombreSala);
        peticion.getDatos().put("limiteJugadores", limiteJugadores);
        System.out.println("[PARTIDA CONTROLLER] enviando peticion para crear partida");
        proxy.enviarMensaje(peticion);
    }

    private void procesarEventoRed(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        if (mensaje.getTipo().equals("SALA_CREADA")) {
            String codigoSala = (String) mensaje.getDatos().get("codigoSala");
            String nombreHost = (String) mensaje.getDatos().getOrDefault("nombre", nombreHostTemporal);
            List<?> jugadores = null;
            Object jugadoresRaw = mensaje.getDatos().get("jugadores");
            if (jugadoresRaw instanceof List<?>) {
                jugadores = (List<?>) jugadoresRaw;
            }

            LobbyView lobby = new LobbyView();
            LobbyController lobbyCtrl = new LobbyController(proxy, codigoSala, nombreHost, true, lobby);
            lobbyCtrl.cargarDatosIniciales(jugadores);
            SwingUtilities.invokeLater(() -> {

                vista.cerrarVista();
                lobby.setVisible(true);
            });
        }
    }

}
