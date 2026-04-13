package interfaces;

import dtos.MensajeDTO;

import java.io.IOException;
import java.net.Socket;

public interface ISerializador {
    void enviarMensaje( MensajeDTO mensaje) throws IOException;
    MensajeDTO recibirMensaje( MensajeDTO mensaje)throws IOException;
    void cerrarConexion()throws IOException;
}
