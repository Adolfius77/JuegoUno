package interfaces;
import dtos.paqueteRedDTO;
import red.ServidorHilo;

public interface IReceptorMensajes {
    void procesarMensaje(paqueteRedDTO paquete, ServidorHilo hiloRemintente);
}
