package red;

import Interfacez.IProxy;
import Interfacez.ISerializador;
import dtos.MensajeDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ProxyCliente implements IProxy {

    private final Socket socketCliente;
    private final PrintWriter out;
    private final ISerializador serializador;

    public ProxyCliente(Socket socketCliente, ISerializador serializador) throws IOException {
        if (socketCliente == null) {
            throw new IllegalArgumentException("[Servidor Red] El socket del cliente no puede ser nulo.");
        }
        if (serializador == null) {
            throw new IllegalArgumentException("[Servidor Red] El serializador no puede ser nulo.");
        }
        this.socketCliente = socketCliente;
        this.serializador = serializador;
        this.out = new PrintWriter(socketCliente.getOutputStream(), true);
    }

    @Override
    public void enviarMensaje(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        try {
            String jsonEnviar = serializador.serealizar(mensaje);
            out.println(jsonEnviar);
        } catch (Exception e) {
            System.out.println("[Servidor Red] Error enviando mensaje: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        // La recepción de datos se realiza en Servidor.
    }
}
