package Interfacez;

import dtos.MensajeDTO;

import java.util.function.Consumer;

public interface IBroker {
    void subscribirse(String tipoEvento, Consumer<MensajeDTO> manejador);
    void desuscribirse(String tipoEvento, Consumer<MensajeDTO> manejador);
    void publicar(String tipoEvento, MensajeDTO mensaje);
   
}
