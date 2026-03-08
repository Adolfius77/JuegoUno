package Interfaces;

import observador.IObserver;

public interface IVista extends IObserver {
    void mostrarVista();
    void cerrarVista();
    void mostrarMensaje(String mensaje);
}
