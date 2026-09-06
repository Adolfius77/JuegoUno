package red;

import Interfacez.IProxy;
import Interfacez.ISerializador;
import observador.observadorRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
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
            anunciarDondeEscucha();

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

    /**
     * Dice por donde se le puede encontrar.
     *
     * Antes se imprimia el valor de configuracion ("y en la ip: localhost"),
     * que no es donde escucha: el ServerSocket se abre sin atarlo a ninguna
     * interfaz, asi que acepta conexiones por todas. Lo que hacia falta saber
     * era que direccion darle a quien se quiere conectar desde fuera.
     */
    private void anunciarDondeEscucha() {
        System.out.println("[Servidor Red] Escuchando en el puerto " + puerto + " (todas las interfaces).");
        System.out.println("[Servidor Red] En esta misma maquina: localhost:" + puerto);

        for (String direccion : direccionesDeLaMaquina()) {
            System.out.println("[Servidor Red] En la red local:     " + direccion + ":" + puerto);
        }
        System.out.println("[Servidor Red] Si esto corre en la nube, reparte la IP publica"
                + " que da el panel de tu proveedor: desde dentro no se ve.");
    }

    /** Las IPv4 de la maquina, sin contar la de loopback. */
    private List<String> direccionesDeLaMaquina() {
        List<String> direcciones = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface red = interfaces.nextElement();
                if (!red.isUp() || red.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> ips = red.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress ip = ips.nextElement();
                    if (ip instanceof Inet4Address) {
                        direcciones.add(ip.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("[Servidor Red] No se pudieron leer las interfaces: " + e.getMessage());
        }
        return direcciones;
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
