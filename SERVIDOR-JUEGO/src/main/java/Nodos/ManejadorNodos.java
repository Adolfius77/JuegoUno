package Nodos;

import dtos.MensajeDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro de sesiones conectadas, indexado por idSesion.
 *
 * Antes la clave era el nombre del jugador, que es mutable: al confirmar el
 * registro habia que sacar y reinsertar el nodo, y dos clientes que compartian
 * entrada terminaban fundidos en uno solo.
 */
public class ManejadorNodos {

    private final Map<String, NodoCliente> nodosPorSesion = new ConcurrentHashMap<>();

    public void registrarNuevoJugador(NodoCliente nuevoNodo) {
        if (nuevoNodo != null) {
            nodosPorSesion.put(nuevoNodo.getIdSesion(), nuevoNodo);
            System.out.println("ManejadorNodos: Jugador registrado -> " + nuevoNodo.getIdSesion()
                    + " (" + nuevoNodo.getNombre() + ")");
        }
    }

    public List<NodoCliente> obtenerNodosConectados() {
        return new ArrayList<>(nodosPorSesion.values());
    }

    public NodoCliente obtenerNodoPorSesion(String idSesion) {
        return idSesion == null ? null : nodosPorSesion.get(idSesion);
    }

    public NodoCliente obtenerNodoPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (nombre.equalsIgnoreCase(nodo.getNombre())) {
                return nodo;
            }
        }
        return null;
    }

    /** true si el nombre ya lo usa otra sesion distinta de la indicada. */
    public boolean nombreEnUsoPorOtro(String nombre, String idSesionPropia) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (!nodo.getIdSesion().equals(idSesionPropia)
                    && nombre.trim().equalsIgnoreCase(nodo.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public void eliminarNodo(String idSesion) {
        if (idSesion == null) {
            return;
        }
        NodoCliente nodo = nodosPorSesion.remove(idSesion);
        if (nodo != null) {
            System.out.println("Nodo eliminado exitosamente del manejador: " + idSesion
                    + " (" + nodo.getNombre() + ")");
        }
    }

    public void enviarNodo(String idSesion, MensajeDTO mensaje) {
        NodoCliente nodo = obtenerNodoPorSesion(idSesion);
        if (nodo != null) {
            nodo.enviarMensaje(mensaje);
        } else {
            System.out.println("Intento de envio fallido. No se encontro la sesion: " + idSesion);
        }
    }

    public List<String> obtenerNombresDeNodosConectados() {
        List<String> nombres = new ArrayList<>();
        for (NodoCliente nodo : nodosPorSesion.values()) {
            nombres.add(nodo.getNombre());
        }
        return nombres;
    }

    public void notificarATodos(MensajeDTO mensaje) {
        for (NodoCliente nodo : nodosPorSesion.values()) {
            nodo.enviarMensaje(mensaje);
        }
    }

    public boolean estanTodosListos() {
        if (nodosPorSesion.isEmpty()) {
            return false;
        }
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (!nodo.isEstaListo()) {
                return false;
            }
        }
        return true;
    }
}
