package Interfacez;

import dtos.MensajeDTO;

public interface ISerializador {
    String serealizar(MensajeDTO mensaje);
    MensajeDTO desearealizar(String json);
}
