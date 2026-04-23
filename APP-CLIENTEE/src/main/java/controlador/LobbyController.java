package controlador;

import Entidades.Lobby;
import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;

import java.util.Collections;
import java.util.List;

public class LobbyController {

    private IVista vista;
    private ClienteProxy proxy;
    private boolean esHost;

    public LobbyController(IVista vista, ClienteProxy proxy, boolean esHost) {
        this.vista = vista;
        this.proxy = proxy;
        this.esHost = esHost;
    }
    public boolean agregarJugador(String nombreJugador) {
        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            vista.mostrarMensaje("el nombre del jugador es obligatorio");
            return false;
        }
        MensajeDTO peticionUnirse = new MensajeDTO("UNIRSE_LOBBY", nombreJugador.trim());
        proxy.enviarMensaje(peticionUnirse);
        return true;
    }

    public boolean iniciarPartida() {
        if(!esHost){
            vista.mostrarMensaje("solo el host de la sala puede iniciar la partida");
            return false;
        }
        MensajeDTO ordenInicio = new MensajeDTO("INICIAR_PARTIDA", "El host intenta iniciar");
        proxy.enviarMensaje(ordenInicio);
        return true;
    }

}
