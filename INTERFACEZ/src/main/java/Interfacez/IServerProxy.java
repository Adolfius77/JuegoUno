package Interfacez;

import dtos.MensajeDTO;

public interface IServerProxy extends Runnable {
    void enviarMensaje(MensajeDTO mensaje);
}
