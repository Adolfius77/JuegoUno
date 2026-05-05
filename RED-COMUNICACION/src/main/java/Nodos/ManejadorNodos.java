package Nodos;

import Interfacez.IProxy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ManejadorNodos {
    private final CopyOnWriteArrayList<NodoCliente> nodosConectados;

    public ManejadorNodos() {
        this.nodosConectados = new CopyOnWriteArrayList<>();
    }

    public void registrarNuevoJugador(NodoCliente nuevoNodo) {
        if (nuevoNodo == null) {
            throw new IllegalArgumentException("El nodo a registrar no puede ser nulo.");
        }
        nodosConectados.add(nuevoNodo);
    }

    public void eliminarPorProxy(IProxy proxy) {
        if (proxy == null) {
            return;
        }
        nodosConectados.removeIf(nodo -> nodo.getProxy() == proxy);
    }

    public List<NodoCliente> obtenerNodosConectados() {
        return List.copyOf(nodosConectados);
    }

    public List<String> obtenerNombresDeNodosConectados() {
        return nodosConectados.stream()
                .map(NodoCliente::getNombre)
                .toList();
    }
}
