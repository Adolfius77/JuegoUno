package Nodos;

import Interfacez.IProxy;
import dtos.MensajeDTO;

/**
 * Sesion de un jugador conectado.
 *
 * La identidad es el idSesion, que no cambia en toda la conexion. El nombre es
 * un atributo mutable: antes se usaba como clave del mapa de nodos y renombrar a
 * un jugador implicaba reinsertarlo, lo que permitia pisar al de al lado.
 */
public class NodoCliente {

    private final String idSesion;
    private String nombre;
    private IProxy proxy;
    private String avatar;
    private boolean estaListo;

    /** Sala en la que esta el jugador; null mientras sigue en el lobby. */
    private String codigoSala;

    public NodoCliente(String idSesion, IProxy proxy, String nombre, String avatar) {
        if (idSesion == null || idSesion.isBlank()) {
            throw new IllegalArgumentException("El idSesion del nodo es obligatorio.");
        }
        this.idSesion = idSesion;
        this.proxy = proxy;
        this.nombre = nombre;
        this.avatar = avatar;
        this.estaListo = false;
    }

    public void enviarMensaje(MensajeDTO mensaje) {
        if (proxy != null) {
            proxy.enviarMensaje(mensaje);
        }
    }

    public String getIdSesion() {
        return idSesion;
    }

    public String getCodigoSala() {
        return codigoSala;
    }

    public void setCodigoSala(String codigoSala) {
        this.codigoSala = codigoSala;
    }

    public boolean estaEnSala(String codigo) {
        return codigoSala != null && codigoSala.equalsIgnoreCase(codigo);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isEstaListo() {
        return estaListo;
    }

    public void setEstaListo(boolean estaListo) {
        this.estaListo = estaListo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public IProxy getProxy() {
        return proxy;
    }

    public void setProxy(IProxy proxy) {
        this.proxy = proxy;
    }
}
