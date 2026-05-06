package red;

import Interfacez.IProxy;
import Interfacez.ISerializador;
import broker.Broker;
import dtos.MensajeDTO;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServidorHilo implements Runnable, IProxy {

    private final Socket socketCliente;
    private final Broker broker;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ISerializador serializador;
    private volatile boolean escuchando;

    public ServidorHilo(Socket socketCliente, Broker broker, ISerializador serializador) throws IOException {
        if (socketCliente == null) {
            throw new IllegalArgumentException("[Servidor-hilo] El socket del cliente no puede ser nulo.");
        }
        if (broker == null) {
            throw new IllegalArgumentException("[Servidor-hilo] El broker no puede ser nulo.");
        }
        if (serializador == null) {
            throw new IllegalArgumentException("[Servidor-hilo] El serializador no puede ser nulo.");
        }
        this.socketCliente = socketCliente;
        this.broker = broker;
        this.serializador = serializador;
        this.out = new PrintWriter(socketCliente.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
        this.escuchando = true;
    }

    @Override
    public void run() {
        try {
            while (escuchando) {
                String jsonRecibido = in.readLine();

                if (jsonRecibido == null) {
                    break;
                }
                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);
                if (mensaje == null) {
                    System.out.println("[ServidorHilo] No se pudo deserializar el mensaje: " + jsonRecibido);
                    continue;
                }
                if (mensaje.getTipo() == null || mensaje.getTipo().isBlank()) {
                    continue;
                }
                broker.publicar(mensaje.getTipo(), mensaje);
            }
        } catch (IOException e) {
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
            String jsonEnviar = serializador.serealizar(mensaje);
            out.println(jsonEnviar);
        } catch (Exception e) {
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
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (out != null) {
                out.close();
            }
        } catch (Exception ignored) {
        }

        try {
            if (socketCliente != null) {
                socketCliente.close();
            }
        } catch (IOException ignored) {
        }
    }
}
