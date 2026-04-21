package broker;

import Interfacez.IBroker;
import Interfacez.IProxy;
import Server.ServerProxy;
import dtos.MensajeDTO;
import fabricas.ServerProxyFactory;
import interfaces.ISerializador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Broker implements IBroker {

    private ServerSocket servidorSocket;
    private int puerto;
    private List<Socket> clientesConectados;
    private Thread hiloAceptarClientes;
    private Map<String, List<Consumer<MensajeDTO>>> suscriptores;
    private Map<String , Socket> socketsJugadores = new HashMap<>();
    private static final int MAX_JUGADORES = 4;
    private static final int MIN_JUGADORES = 2;
    private boolean partidaEnCurso;
    private ISerializador serializador;
    private List<IProxy> listaProxies = new ArrayList<>();

    public Broker(int puerto, int maximoJugadores, ISerializador serializador) {
        this.puerto = puerto;
        this.clientesConectados = new ArrayList<>();
        this.suscriptores = new HashMap<>();
        this.socketsJugadores = new HashMap<>();
        this.partidaEnCurso = false;
        this.serializador = serializador;
    }

    private void aceptarClientes(){
        while(true){
            try{
                Socket clienteSocket = servidorSocket.accept();
                clientesConectados.add(clienteSocket);

                ServerProxy manejador = ServerProxyFactory.crearManjadorCliente(this, clienteSocket, serializador);
                this.agregarProxy(manejador);

                Thread threadCliente = new Thread(manejador);
                threadCliente.start();

                System.out.println("Cliente conectado y proxy registrado.");
            }catch (IOException e){
                System.out.println("Error aceptando clientes: " + e.getMessage());
            }
        }
    }

    public void agregarProxy(IProxy proxy) {
        this.listaProxies.add(proxy);
    }

    @Override
    public synchronized boolean registrarJugador(String nombreJugador, Socket socketCliente) {
        if (socketsJugadores.size() >= MAX_JUGADORES) {
            return false;
        }

        if (socketsJugadores.containsKey(nombreJugador)) {
            return false;
        }

        socketsJugadores.put(nombreJugador, socketCliente);
        this.avisarCambioEnLobby();

        return true;
    }

    public synchronized void avisarCambioEnLobby() {
        String todosLosJugadores = String.join(", ", socketsJugadores.keySet());
        dtos.MensajeNotificacionDTO aviso = new dtos.MensajeNotificacionDTO(
                "SERVIDOR",
                false,
                "ACTUALIZACION_LOBBY:" + todosLosJugadores
        );

        for (IProxy p : listaProxies) {
            p.enviarMensaje(aviso);
        }
    }

    public void iniciarServidor(){
        try{
            servidorSocket = new ServerSocket(puerto);
            hiloAceptarClientes = new Thread(this::aceptarClientes);
            hiloAceptarClientes.setName("aceptarClientes");
            hiloAceptarClientes.setDaemon(true);
            hiloAceptarClientes.start();
            System.out.println("Servidor iniciado en puerto " + puerto);
        }catch (IOException e){
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    @Override
    public void subscribirse(String tipoEvento, Consumer<MensajeDTO> manejador) {
        suscriptores.computeIfAbsent(tipoEvento, k -> new ArrayList<>()).add(manejador);
    }

    @Override
    public void desuscribirse(String tipoEvento, Consumer<MensajeDTO> manejador) {
        List<Consumer<MensajeDTO>> manejadores = suscriptores.get(tipoEvento);
        if (manejadores != null) {
            manejadores.remove(manejador);
        }
    }

    @Override
    public void publicar(String tipoEvento, MensajeDTO mensaje) {
        List<Consumer<MensajeDTO>> interesados = suscriptores.getOrDefault(tipoEvento, new ArrayList<>());
        for (Consumer<MensajeDTO> consumidor : interesados) {
            consumidor.accept(mensaje);
        }
    }
}