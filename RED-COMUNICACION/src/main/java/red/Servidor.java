package red;

import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.fabricas.MazoClasicoFactory;
import Interfacez.ISerializador;
import Nodos.NodoCliente;
import broker.Broker;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private final int puerto;
    private final String ip;
    private final Broker brokerCentral;
    private final ISerializador serializador;
    private volatile boolean escuchando;
    private final LobbyServidor lobbyServidor;

    public Servidor(int puerto, String ip, ISerializador serializador) {
        this.puerto = puerto;
        this.ip = ip;
        this.brokerCentral = new Broker();
        
        JuegoServidor juego = new JuegoServidor(
            this.brokerCentral, 
            new CartaFactory(), 
            new MazoClasicoFactory(), 
            EstadoFactory.crearEstadoEsperando()
        );
        
        this.lobbyServidor = new LobbyServidor(this.brokerCentral, juego);
        this.escuchando = true;
        this.serializador = serializador;
    }

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("[Servidor Red] Servidor iniciado en el puerto: " + puerto + " y en la ip: " + ip);

            int contadorJugadores = 1; 

            while (escuchando) {

                Socket socketCliente = serverSocket.accept();

                try {
                    System.out.println("[Servidor Red] Nueva conexion aceptada desde: " + socketCliente.getInetAddress().getHostAddress());
                    
                    ServidorHilo nuevoHilo = new ServidorHilo(socketCliente, brokerCentral, serializador);
                    String nombreTemporal = "Jugador_" + contadorJugadores++;
                    String nombreFotoTemporal = "no hay";
                    NodoCliente nuevoNodo = new NodoCliente(nombreTemporal, nuevoHilo, nombreFotoTemporal);

                   
                    lobbyServidor.registrarNuevoJugadorTemporal(nuevoNodo);

                    Thread hilo = new Thread(nuevoHilo, "ServidorHilo-" + nombreTemporal);
                    hilo.start();

                    System.out.println("[Servidor Red] Jugador conectado temporalmente. Esperando mensaje de registro...");

                } catch (Exception e) {
                    System.err.println("[Servidor Red] Error al manejar la configuración del cliente: " + e.getMessage());
                    try {
                        socketCliente.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Servidor Red] Error critico en el servidor en el puerto: " + puerto);
            e.printStackTrace();
        }
    }

    public void apagar() {
        this.escuchando = false;
        System.out.println("[Servidor Red] Apagando servidor...");
    }
}