package red; // O el paquete que corresponda en tu proyecto cliente

import dtos.MensajeDTO;
import Interfacez.IProxy;
import Interfacez.ISerializador;
import Lector.LectorConfiguracion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ClienteProxy extends Thread implements IProxy {

    private static ClienteProxy instance;
    private Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    private boolean escuchando = false;
    private ISerializador serializador;

    private Consumer<MensajeDTO> accionAlRecibirMensaje;

    private ClienteProxy() {
    }

    public static ClienteProxy getInstance() {
        if (instance == null) {
            instance = new ClienteProxy();
        }
        return instance;
    }

    public void setSerializador(ISerializador serializador) {
        this.serializador = serializador;
    }

    public void setReceptor(Consumer<MensajeDTO> accion) {
        this.accionAlRecibirMensaje = accion;
    }

    public void conectar() throws Exception {
        if (serializador == null) {
            throw new IllegalStateException("ClienteProxy: ISerializador no configurado.");
        }

        if (socket == null || socket.isClosed()) {
            LectorConfiguracion config = new LectorConfiguracion();
            String ip = config.getIpServidor();
            int puerto = config.getPuertoServidor();

            System.out.println("ClienteProxy: Conectando a " + ip + ":" + puerto);
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
            System.err.println("ClienteProxy: Error al enviar mensaje - " + e.getMessage());
        }
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

                if (accionAlRecibirMensaje != null) {
                    if (accionAlRecibirMensaje != null) {
                        accionAlRecibirMensaje.accept(mensaje);
                    }

                }else{
                   System.out.println("ClienteProxy: Se recibio un mensaje que no se pudo deserializar."); 
                }
            }
        } catch (Exception e) {
            System.err.println("ClienteProxy: Desconectado - " + e.getMessage());
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
