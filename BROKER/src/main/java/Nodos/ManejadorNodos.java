package Nodos;

import Interfacez.IProxy;
import dtos.MensajeDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ManejadorNodos {

    private Map<String, NodoCliente> nodosClientes = new ConcurrentHashMap<>();

    public void registrarNuevoJugador(NodoCliente nuevoNodo) {
        if (nuevoNodo != null && nuevoNodo.getNombre() != null) {
            nodosClientes.put(nuevoNodo.getNombre(), nuevoNodo);
            System.out.println("ManejadorNodos: Jugador registrado -> " + nuevoNodo.getNombre());
        }
    }

    public List<NodoCliente> obtenerNodosConectados() {
        return new ArrayList<>(nodosClientes.values());
    }

    public void eliminarPorProxy(IProxy proxy) {
        if (proxy == null) {
            return;
        }

        String idAEliminar = null;

        for (Map.Entry<String, NodoCliente> entry : nodosClientes.entrySet()) {
            if (entry.getValue().getProxy() == proxy) {
                idAEliminar = entry.getKey();
                break;
            }
        }

        if (idAEliminar != null) {
            eliminarNodo(idAEliminar);
        }
    }

    public void actualizarIdentidadNodo(String idTemporal, String nombreReal) {
        NodoCliente nodo = nodosClientes.remove(idTemporal);

        if (nodo != null) {
            nodo.setNombre(nombreReal);
            nodosClientes.put(nombreReal, nodo);
            System.out.println("Identidad confirmada. El ID " + idTemporal + " ahora es: " + nombreReal);
        }
    }

    public void eliminarNodo(String idNodo) {
        if (idNodo == null) {
            return;
        }

        NodoCliente nodo = nodosClientes.remove(idNodo);

        if (nodo != null) {
            try {
                if (nodo.getSocket() != null && !nodo.getSocket().isClosed()) {
                    nodo.getSocket().close();
                }
            } catch (Exception e) {
                System.out.println("Error cerrando socket del nodo " + idNodo + ": " + e.getMessage());
            }
            System.out.println("Nodo eliminado exitosamente del manejador: " + idNodo);
        }
    }

    public void enviarNodo(String idJugador, MensajeDTO mensaje) {
        NodoCliente nodo = nodosClientes.get(idJugador);
        if (nodo != null) {
            nodo.enviarMensaje(mensaje);
        } else {
            System.out.println("Intento de envío fallido. No se encontró el nodo: " + idJugador);
        }
    }

    public List<String> obtenerNombresDeNodosConectados() {
        List<String> nombres = new ArrayList<>();
        for (NodoCliente nodo : nodosClientes.values()) {
            nombres.add(nodo.getNombre());
        }
        return nombres;
    }
}
