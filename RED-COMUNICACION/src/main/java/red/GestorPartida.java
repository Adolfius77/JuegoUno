package red;

import dtos.*;
import Interfacez.IProxy;
import java.util.ArrayList;
import java.util.List;


public class GestorPartida {

    private PartidaDTO partidaActualDTO;
    private List<String> jugadoresEnLobby;
    private IProxy miProxyRed;

    public GestorPartida() {
        this.jugadoresEnLobby = new ArrayList<>();
        this.partidaActualDTO = null;
    }
    public void setProxyRed(IProxy miProxyRed) {
        this.miProxyRed = miProxyRed;
    }

    public void procesarRegistro(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        MensajeRegistroDTO mensaje = new MensajeRegistroDTO();
        mensaje.setTipo("SOLICITUD_REGISTRO");
        mensaje.setNombre(nombre.trim());

        miProxyRed.enviarMensaje(mensaje);
    }

    public void iniciarPartida() {
        MensajeDTO mensaje = new MensajeDTO();
        mensaje.setTipo("INTENCION_INICIAR_PARTIDA");
        miProxyRed.enviarMensaje(mensaje);
    }

    public void jugarCarta(CartaDTO carta) {
        MensajeJugarCartaDTO mensaje = new MensajeJugarCartaDTO();
        mensaje.setTipo("INTENCION_JUGAR_CARTA");
        mensaje.setCarta(carta);
        miProxyRed.enviarMensaje(mensaje);
    }

    public void tomarCarta() {
        MensajeRobarCartaDTO mensaje = new MensajeRobarCartaDTO();
        mensaje.setTipo("INTENCION_ROBAR_CARTA");
        miProxyRed.enviarMensaje(mensaje);
    }

    public void pasarTurno() {
        MensajePasarTurnoDTO mensaje = new MensajePasarTurnoDTO();
        mensaje.setTipo("INTENCION_PASAR_TURNO");
        miProxyRed.enviarMensaje(mensaje);
    }

    public void decirUno() {
        MensajeGritarUnoDTO mensaje = new MensajeGritarUnoDTO();
        mensaje.setTipo("INTENCION_GRITAR_UNO");
        miProxyRed.enviarMensaje(mensaje);
    }
    public void actualizarEstadoPartida(PartidaDTO nuevoEstado) {
        this.partidaActualDTO = nuevoEstado;
    }

    public void actualizarLobby(List<String> nuevaLista) {
        this.jugadoresEnLobby = nuevaLista;
    }

    public PartidaDTO obtenerEstadoPartida() {
        return partidaActualDTO;
    }

    public List<String> obtenerJugadoresRegistrados() {
        return jugadoresEnLobby;
    }

    public boolean esPartidaActiva() {
        return partidaActualDTO != null && partidaActualDTO.isEnCurso();
    }

    public void limpiarLobby() {
        jugadoresEnLobby.clear();
    }
}