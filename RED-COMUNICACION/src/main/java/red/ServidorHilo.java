package red;

import Interfacez.IProxy;
import broker.Broker;
import dtos.MensajeDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServidorHilo implements Runnable, IProxy {

    private final Socket socketCliente;
    private final Broker broker;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private volatile boolean escuchando;

    public ServidorHilo(Socket socketCliente, Broker broker) throws IOException {
        if (socketCliente == null) {
            throw new IllegalArgumentException("El socket del cliente no puede ser nulo.");
        }
        if (broker == null) {
            throw new IllegalArgumentException("El broker no puede ser nulo.");
        }
        this.socketCliente = socketCliente;
        this.broker = broker;
        this.out = new ObjectOutputStream(socketCliente.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socketCliente.getInputStream());
        this.escuchando = true;
    }

    @Override
    public void run() {
        try {
            while (escuchando) {
                Object objeto = in.readObject();
                if (!(objeto instanceof MensajeDTO mensaje)) {
                    continue;
                }
                if (mensaje.getTipo() == null || mensaje.getTipo().isBlank()) {
                    continue;
                }
                broker.publicar(mensaje.getTipo(), mensaje);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ServidorHilo] Conexion finalizada: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    @Override
    public synchronized void enviarMensaje(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        try {
            out.writeObject(mensaje);
            out.reset();
            out.flush();
        } catch (IOException e) {
            System.out.println("[ServidorHilo] Error enviando mensaje: " + e.getMessage());
        }
    }

    private void cerrarConexion() {
        escuchando = false;

        MensajeDTO eventoDesconexion = new MensajeDTO();
        eventoDesconexion.setTipo("CONEXION_CERRADA");
        eventoDesconexion.setRemitente("SERVIDOR");
        Map<String, Object> datos = new HashMap<>();
        datos.put("proxy", this);
        eventoDesconexion.setDatos(datos);
        broker.publicar("CONEXION_CERRADA", eventoDesconexion);

        try {
            in.close();
        } catch (IOException ignored) {
        }
        try {
            out.close();
        } catch (IOException ignored) {
        }
        try {
            socketCliente.close();
        } catch (IOException ignored) {
        }
    }
}
