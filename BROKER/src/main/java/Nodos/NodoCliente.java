package Nodos;

import Interfacez.IProxy;
import dtos.MensajeDTO;

import java.net.Socket;

public class NodoCliente {
    private String idJugador;
    private Socket socket;
    private IProxy proxy;

    public NodoCliente(Socket socket, IProxy proxy, String idJugador) {
        this.socket = socket;
        this.proxy = proxy;
        this.idJugador = idJugador;
    }

    public void enviarMensaje(MensajeDTO mensaje) {
        if (proxy != null) {
            proxy.enviarMensaje(mensaje);
        }
    }
    //getters y setters

    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public IProxy getProxy() {
        return proxy;
    }

    public void setProxy(IProxy proxy) {
        this.proxy = proxy;
    }
}