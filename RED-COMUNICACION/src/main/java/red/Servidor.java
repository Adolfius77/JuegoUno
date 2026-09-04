package red;

import Interfacez.IProxy;
import Interfacez.ISerializador;
import observador.observadorRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Escucha conexiones entrantes y entrega el texto recibido a sus observadores.
 *
 * Cada conexion aceptada recibe un identificador de sesion propio. Antes se
 * indexaba por direccion IP, con lo que dos clientes de la misma maquina (o
 * detras del mismo NAT) compartian entrada y el segundo desplazaba al primero.
 */
public class Servidor {

    private final int puerto;
    private final String ip;
    private final ISerializador serializador;
    private volatile boolean escuchando;
    private final List<observadorRed> observadores;
    private final Map<String, IProxy> proxiesPorSesion;
    private final AtomicLong secuenciaSesiones = new AtomicLong();

    public Servidor(int puerto, String ip, ISerializador serializador) {
        this.puerto = puerto;
        this.ip = ip;
        this.serializador = serializador;
        this.escuchando = true;
        this.observadores = new CopyOnWriteArrayList<>();
        this.proxiesPorSesion = new ConcurrentHashMap<>();
    }

    public void agregarObservador(observadorRed observador) {
        if (observador == null) {
            return;
        }
        this.observadores.add(observador);
    }

    public IProxy obtenerProxy(String idSesion) {
        return idSesion == null ? null : proxiesPorSesion.get(idSesion);
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("[Servidor Red] Servidor iniciado en el puerto: " + puerto + " y en la ip: " + ip);

            while (escuchando) {
                Socket socketCliente = serverSocket.accept();
                String idSesion = "S" + secuenciaSesiones.incrementAndGet();
                System.out.println("[Servidor Red] Nueva conexion aceptada: " + idSesion
                        + " desde " + socketCliente.getInetAddress().getHostAddress());

                try {
                    ProxyCliente proxy = new ProxyCliente(socketCliente, serializador);
                    proxiesPorSesion.put(idSesion, proxy);
                } catch (IOException e) {
                    System.err.println("[Servidor Red] Error creando proxy para el cliente: " + e.getMessage());
                    try {
                        socketCliente.close();
                    } catch (IOException ignored) {
                    }
                    continue;
                }

                Thread hilo = new Thread(() -> escucharCliente(socketCliente, idSesion), "ServidorHilo-" + idSesion);
                hilo.start();
            }
        } catch (IOException e) {
            System.err.println("[Servidor Red] Error critico en el servidor en el puerto: " + puerto);
            e.printStackTrace();
        }
    }

    private void escucharCliente(Socket socketCliente, String idSesion) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()))) {
            String jsonRecibido;
            while ((jsonRecibido = in.readLine()) != null) {
                notificarObservadores(jsonRecibido, idSesion);
            }
        } catch (IOException e) {
            System.out.println("[Servidor Red] Conexion finalizada con " + idSesion + ": " + e.getMessage());
        } finally {
            cerrarConexion(socketCliente, idSesion);
        }
    }

    private void notificarObservadores(String json, String idSesion) {
        for (observadorRed observador : observadores) {
            observador.onMensajeRecibido(json, idSesion);
        }
    }

    private void cerrarConexion(Socket socketCliente, String idSesion) {
        try {
            if (serializador != null) {
                dtos.MensajeDTO mensaje = new dtos.MensajeDTO();
                mensaje.setTipo("DESCONEXION");
                String json = serializador.serealizar(mensaje);
                notificarObservadores(json, idSesion);
            }
        } catch (Exception ex) {
            System.out.println("[Servidor Red] Error notificando desconexion: " + ex.getMessage());
        }

        proxiesPorSesion.remove(idSesion);
        try {
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
        } catch (IOException ignored) {
        }
    }

    public void apagar() {
        this.escuchando = false;
        System.out.println("[Servidor Red] Apagando servidor...");
    }
}
