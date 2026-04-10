package interfaces;

import parqueteRed.paqueteRedDTO;
import red.ServidorHilo;

public interface IReceptorMensajes {
void procesarMensaje(paqueteRedDTO paquete, ServidorHilo hiloRemintente);
}
