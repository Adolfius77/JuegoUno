package interfaces;

import dtos.MensajeDTO;

import java.io.IOException;
import java.net.Socket;

public interface ISerializador {
    String serealizar(MensajeDTO mensaje);
    MensajeDTO desearealizar(String json);
}
