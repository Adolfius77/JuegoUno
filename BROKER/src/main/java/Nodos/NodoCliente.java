package Nodos;

import Interfacez.IProxy;
import dtos.MensajeDTO;

import java.net.Socket;

public class NodoCliente {
    private String nombre;
    private Socket socket;
    private IProxy proxy;

    public NodoCliente(Socket socket, IProxy proxy, String nombre) {
        this.socket = socket;
        this.proxy = proxy;
        this.nombre = nombre;
    }

    public void enviarMensaje(MensajeDTO mensaje) {
        if (proxy != null) {
            proxy.enviarMensaje(mensaje);
        }
    }
    //getters y setters


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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