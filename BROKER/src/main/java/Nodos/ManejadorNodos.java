package Nodos;

import dtos.MensajeDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;




public class ManejadorNodos {

    private Map<String, NodoCliente> NodoClientes = new ConcurrentHashMap<>();

    public void actualizarIdentidadNodo(String idTemporal, String nombreReal) {
        NodoCliente nodo = NodoClientes.remove(idTemporal);

        if (nodo != null) {
            nodo.setNombre(nombreReal);
            NodoClientes.put(nombreReal, nodo);
            System.out.println("identidad confirmada" + idTemporal + nombreReal);
        }
    }

    public void eliminarNodo(String idNodo) {
        if (idNodo == null) {
            return;
        }
        NodoCliente nodo = NodoClientes.remove(idNodo);
        if (nodo != null) {
            try {
                if (nodo.getSocket() != null && !nodo.getSocket().isClosed()) {
                    nodo.getSocket().close();
                }
            } catch (Exception e) {
                System.out.println("Error cerrando socket del nodo " + idNodo + ": " + e.getMessage());
            }
            System.out.println("Nodo eliminado: " + idNodo);
        }
    }

    public void enviarNodo(String idTemporal, MensajeDTO mensaje) {
        NodoCliente nodo = NodoClientes.get(idTemporal);
        if (nodo != null) {
            nodo.enviarMensaje(mensaje);
        }
    }

    public List<String> obtenerNombresDeNodosConectados() {
        List<String> nombres = new ArrayList<>();

        for (NodoCliente nodo : NodoClientes.values()) {
            nombres.add(nodo.getNombre());
        }
        return nombres;
    }
}
