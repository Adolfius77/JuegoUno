package Interfacez;

import dtos.MensajeDTO;

public interface IProxy extends Runnable {
    void enviarMensaje(MensajeDTO mensaje);
}
