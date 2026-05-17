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

public class Servidor {

    private final int puerto;
    private final String ip;
    private final ISerializador serializador;
    private volatile boolean escuchando;
    private final List<observadorRed> observadores;
    private final Map<String, IProxy> proxiesPorIp;

    public Servidor(int puerto, String ip, ISerializador serializador) {
        this.puerto = puerto;
        this.ip = ip;
        this.serializador = serializador;
        this.escuchando = true;
        this.observadores = new CopyOnWriteArrayList<>();
        this.proxiesPorIp = new ConcurrentHashMap<>();
    }

    public void agregarObservador(observadorRed observador) {
        if (observador == null) {
            return;
        }
        this.observadores.add(observador);
    }

    public IProxy obtenerProxyPorIp(String ipCliente) {
        return proxiesPorIp.get(ipCliente);
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("[Servidor Red] Servidor iniciado en el puerto: " + puerto + " y en la ip: " + ip);

            while (escuchando) {
                Socket socketCliente = serverSocket.accept();
                String ipCliente = socketCliente.getInetAddress().getHostAddress();
                System.out.println("[Servidor Red] Nueva conexion aceptada desde: " + ipCliente);

                try {
                    ProxyCliente proxy = new ProxyCliente(socketCliente, serializador);
                    proxiesPorIp.put(ipCliente, proxy);
                } catch (IOException e) {
                    System.err.println("[Servidor Red] Error creando proxy para el cliente: " + e.getMessage());
                    try {
                        socketCliente.close();
                    } catch (IOException ignored) {
                    }
                    continue;
                }

                Thread hilo = new Thread(() -> escucharCliente(socketCliente, ipCliente), "ServidorHilo-" + ipCliente);
                hilo.start();
            }
        } catch (IOException e) {
            System.err.println("[Servidor Red] Error critico en el servidor en el puerto: " + puerto);
            e.printStackTrace();
        }
    }

    private void escucharCliente(Socket socketCliente, String ipCliente) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()))) {
            String jsonRecibido;
            while ((jsonRecibido = in.readLine()) != null) {
                notificarObservadores(jsonRecibido, ipCliente);
            }
        } catch (IOException e) {
            System.out.println("[Servidor Red] Conexion finalizada con " + ipCliente + ": " + e.getMessage());
        } finally {
            cerrarConexion(socketCliente, ipCliente);
        }
    }

    private void notificarObservadores(String json, String ipCliente) {
        for (observadorRed observador : observadores) {
            observador.onMensajeRecibido(json, ipCliente);
        }
    }

    private void cerrarConexion(Socket socketCliente, String ipCliente) {
        try {
            if (serializador != null) {
                dtos.MensajeDTO mensaje = new dtos.MensajeDTO();
                mensaje.setTipo("DESCONEXION");
                String json = serializador.serealizar(mensaje);
                notificarObservadores(json, ipCliente);
            }
        } catch (Exception ex) {
            System.out.println("[Servidor Red] Error notificando desconexion: " + ex.getMessage());
        }

        proxiesPorIp.remove(ipCliente);
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
