package Observer;

public interface IObservable {
    void agregarObservador(IObserver obs);
    void eliminarObservador(IObserver obs);
    void notificarObservador(String evento);
}
