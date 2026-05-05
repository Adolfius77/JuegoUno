package red;

import Nodos.NodoCliente;
import broker.Broker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private final int puerto;
    private final String ip;
    private final Broker brokerCentral;
    private final ManejadorPartidaServidor manejadorPartida;
    private volatile boolean escuchando;

    public Servidor(int puerto, String ip) {
        this.puerto = puerto;
        this.ip = ip;
        this.brokerCentral = new Broker();
        this.manejadorPartida = new ManejadorPartidaServidor(this.brokerCentral);
        this.escuchando = true;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("[Servidor Red] Servidor iniciado en el puerto: " + puerto + "y en la ip: " + ip);

            int contadorJugadores = 1;

            while (escuchando) {

                Socket socketCliente = serverSocket.accept();

                try {
                    System.out.println("[Servidor Red] Nueva conexion aceptada desde: " + socketCliente.getInetAddress().getHostAddress());
                    ServidorHilo nuevoHilo = new ServidorHilo(socketCliente, brokerCentral);
                    String nombreTemporal = "Jugador_" + contadorJugadores++;
                    NodoCliente nuevoNodo = new NodoCliente(nombreTemporal, nuevoHilo);

                    manejadorPartida.registrarNuevoJugador(nuevoNodo);

                    Thread hilo = new Thread(nuevoHilo, "ServidorHilo-" + nombreTemporal);
                    hilo.start();

                    System.out.println("[Servidor Red] Jugador registrado en el sistema. Esperando mensajes...");

                } catch (Exception e) {
                    System.err.println("Error al manejar la configuración del cliente: " + e.getMessage());
                    try {
                        socketCliente.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error crítico en el servidor en el puerto: " + puerto);
            e.printStackTrace();
        }
    }

    public void apagar() {
        this.escuchando = false;
        System.out.println("[Servidor Red] Apagando servidor...");

    }
}
