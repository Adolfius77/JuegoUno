package cliente;

import dtos.MensajeDTO;
import Interfacez.IBroker;
import Interfacez.IProxy;
import Interfacez.ISerializador;
import Lector.LectorConfiguracion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Proxy del lado cliente del patron Broker: oculta el socket a la aplicacion.
 *
 * Todo lo que llega del servidor se publica en el IBroker del cliente, de modo
 * que cualquier numero de controladores pueda suscribirse al mismo evento. Antes
 * habia un unico Consumer receptor y los controladores se lo sobrescribian entre
 * si, con lo que solo el ultimo registrado recibia mensajes.
 */
public class ClienteProxy extends Thread implements IProxy {

    private static ClienteProxy instance;
    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    private volatile boolean escuchando = false;
    private ISerializador serializador;
    private IBroker broker;

    private ClienteProxy() {
    }
    //singleton para tener una unica instancia hacia el server
    public static synchronized ClienteProxy getInstance() {
        if (instance == null) {
            instance = new ClienteProxy();
        }
        return instance;
    }

    public void setSerializador(ISerializador serializador) {
        this.serializador = serializador;
    }

    /** Bus donde se publican los mensajes entrantes. Lo inyecta el arranque del cliente. */
    public void setBroker(IBroker broker) {
        this.broker = broker;
    }

    public IBroker getBroker() {
        return broker;
    }

    public void conectar() throws Exception {
        if (serializador == null) {
            throw new IllegalStateException("[Cliente-Proxy] ISerializador no configurado.");
        }
        if (broker == null) {
            throw new IllegalStateException("[Cliente-Proxy] IBroker no configurado.");
        }

        if (socket == null || socket.isClosed()) {
            LectorConfiguracion config = new LectorConfiguracion();
            String ip = config.getIpServidor();
            int puerto = config.getPuertoServidor();

            System.out.println("[Cliente-Proxy] Conectando a " + ip + ":" + puerto);
            socket = new Socket(ip, puerto);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            escuchando = true;

            if (!this.isAlive()) {
                this.start();
            }
        }
    }

    @Override
    public synchronized void enviarMensaje(MensajeDTO mensaje) {
        try {
            if (out != null) {

                String json = serializador.serealizar(mensaje);
                out.println(json);
            }
        } catch (Exception e) {
            System.err.println("[Cliente-Proxy] Error al enviar mensaje - " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (escuchando) {
                String jsonRecibido = in.readLine();
                if (jsonRecibido == null) {
                    System.out.println("[Cliente-Proxy] El servidor cerro la conexion.");
                    break;
                }

                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);
                if (mensaje == null || mensaje.getTipo() == null) {
                    System.out.println("[Cliente-Proxy] Se recibio un mensaje que no se pudo deserializar.");
                    continue;
                }

                broker.publicar(mensaje.getTipo(), mensaje);
            }
        } catch (Exception e) {
            System.err.println("[Cliente-Proxy] Desconectado - " + e.getMessage());
            escuchando = false;
        } finally {
            cerrarConexion();
        }
    }

    private void cerrarConexion() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
