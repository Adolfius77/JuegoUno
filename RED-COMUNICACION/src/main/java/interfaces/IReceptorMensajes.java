package interfaces;
import dtos.paqueteRedDTO;
import Server.ServerProxy;

public interface IReceptorMensajes {
    void procesarMensaje(paqueteRedDTO paquete, ServerProxy hiloRemintente);
}
