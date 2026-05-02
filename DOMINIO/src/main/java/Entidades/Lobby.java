package Entidades;



import Observer.IObservable;
import Observer.IObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lobby implements IObservable {

    private final List<String> nombreJugadores = new CopyOnWriteArrayList<>();
    private List<IObserver> observadores = new ArrayList<>();

    public synchronized boolean agregarJugador(String nombre) {
        for (String n : nombreJugadores) {
            if (n.equalsIgnoreCase(nombre)) {
                return false;
            }
        }
        return nombreJugadores.add(nombre);
    }

    public List<String> getNombreJugadores() {
        return nombreJugadores;
    }

    public void limpiarJugadores() {
        this.nombreJugadores.clear();
        notificarObservador("LISTA_ACTUALIZADA");
    }

    @Override
    public void agregarObservador(IObserver obs) {
        observadores.add(obs);
    }

    @Override
    public void eliminarObservador(IObserver obs) {
        observadores.remove(obs);
    }

    @Override
    public void notificarObservador(String evento) {
        for (IObserver obs : observadores) {
            obs.actualizar(evento);
        }
    }
}
