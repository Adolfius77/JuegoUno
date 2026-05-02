package red;

import Interfacez.IProxy;
import Observer.IObservable;
import Observer.IObserver;
import dtos.*;
import interfaces.IGestorPartida;

import java.util.ArrayList;
import java.util.List;

public class GestorPartida implements IObservable , IGestorPartida {

    private PartidaDTO partidaActualDTO;
    private List<String> jugadoresEnLobby;
    private IProxy miProxyRed;
    private List<IObserver> observadores;

    public GestorPartida() {
        this.jugadoresEnLobby = new ArrayList<>();
        this.partidaActualDTO = null;
        this.observadores = new ArrayList<>();
    }

    public void setProxyRed(IProxy miProxyRed) {
        if (miProxyRed == null) {
            throw new IllegalArgumentException("El proxy de red no puede ser nulo.");
        }
        this.miProxyRed = miProxyRed;
    }

    private void verificarProxy() {
        if (this.miProxyRed == null) {
            throw new IllegalStateException("Error: No se ha asignado un Proxy de Red. Llama a setProxyRed() antes de enviar mensajes.");
        }
    }
    @Override
    public void procesarRegistro(String nombre) {
        verificarProxy();

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador es invalido o esta vacio.");
        }

        MensajeRegistroDTO mensaje = new MensajeRegistroDTO();
        mensaje.setTipo("SOLICITUD_REGISTRO");
        mensaje.setNombre(nombre.trim());

        miProxyRed.enviarMensaje(mensaje);
    }
    @Override
    public void iniciarPartida() {
        verificarProxy();
        MensajeDTO mensaje = new MensajeDTO();
        mensaje.setTipo("INTENCION_INICIAR_PARTIDA");
        miProxyRed.enviarMensaje(mensaje);
    }
    @Override
    public void jugarCarta(CartaDTO carta) {
        verificarProxy();

        if (carta == null) {
            throw new IllegalArgumentException("La carta a jugar no puede ser nula.");
        }

        MensajeJugarCartaDTO mensaje = new MensajeJugarCartaDTO();
        mensaje.setTipo("INTENCION_JUGAR_CARTA");
        mensaje.setCartaJugada(carta);
        miProxyRed.enviarMensaje(mensaje);
    }
    @Override
    public void tomarCarta() {
        verificarProxy();
        MensajeRobarCartaDTO mensaje = new MensajeRobarCartaDTO();
        mensaje.setTipo("INTENCION_ROBAR_CARTA");
        miProxyRed.enviarMensaje(mensaje);
    }
    @Override
    public void pasarTurno() {
        verificarProxy();
        MensajePasarTurnoDTO mensaje = new MensajePasarTurnoDTO();
        mensaje.setTipo("INTENCION_PASAR_TURNO");
        miProxyRed.enviarMensaje(mensaje);
    }
    @Override
    public void decirUno() {
        verificarProxy();
        MensajeGritarUnoDTO mensaje = new MensajeGritarUnoDTO();
        mensaje.setTipo("INTENCION_GRITAR_UNO");
        miProxyRed.enviarMensaje(mensaje);
    }
    @Override
    public void actualizarEstadoPartida(PartidaDTO nuevoEstado) {
        if (nuevoEstado != null) {
            this.partidaActualDTO = nuevoEstado;
            notificarObservador("PARTIDA_INICIADA");
        }
    }
    @Override
    public void actualizarLobby(List<String> nuevaLista) {
        if (nuevaLista != null) {
            this.jugadoresEnLobby = new ArrayList<>(nuevaLista);
            notificarObservador("LOBBY_ACTUALIZADO");
        }
    }
    @Override
    public PartidaDTO obtenerEstadoPartida() {
        return partidaActualDTO;
    }
    @Override
    public List<String> obtenerJugadoresRegistrados() {
        return jugadoresEnLobby;
    }
    @Override
    public boolean esPartidaActiva() {
        return partidaActualDTO != null && partidaActualDTO.isEnCurso();
    }
    @Override
    public void limpiarLobby() {
        if (jugadoresEnLobby != null) {
            jugadoresEnLobby.clear();
        }
    }

    @Override
    public void agregarObservador(IObserver obs) {
        this.observadores.add(obs);
    }

    @Override
    public void eliminarObservador(IObserver obs) {
        this.observadores.remove(obs);
    }

    @Override
    public void notificarObservador(String evento) {
        for (IObserver obs : observadores) {
            obs.actualizar(evento);
        }
    }
}