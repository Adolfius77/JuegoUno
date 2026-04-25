package Entidades;



import Observer.IObservable;
import Observer.IObserver;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements IObservable {

    private final List<String> nombreJugadores = new ArrayList<>();
    private List<IObserver> observadores = new ArrayList<>();

    public void agregarJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("nombre invalido");
        }
        if (nombreJugadores.size() >= 4) {
            throw new IllegalArgumentException("sala llena");
        }

        String nombreNormalizado = nombre.trim();
        boolean nombreExistente = nombreJugadores.stream()
                .anyMatch(j -> j.equalsIgnoreCase(nombreNormalizado));
        if (nombreExistente) {
            throw new IllegalArgumentException("el nombre ya esta en uso");
        }

        nombreJugadores.add(nombreNormalizado);
        notificarObservador("LISTA_ACTUALIZADA");
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
