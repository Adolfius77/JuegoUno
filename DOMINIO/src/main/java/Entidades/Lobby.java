package Entidades;



import Observer.IObservable;
import Observer.IObserver;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements IObservable {

    private final List<String> nombreJugadores = new ArrayList<>();
    private List<IObserver> observadores = new ArrayList<>();

    public void agregarJugador(String nombre) {
        if (nombreJugadores.size() < 4 && !nombre.trim().isEmpty()) {
            nombreJugadores.add(nombre);
            notificarObservador("LISTA_ACTUALIZADA");
        } else {
            throw new IllegalArgumentException("sala llena o nombre invalido");
        }
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
            obs.actualizar();
        }
    }
}
