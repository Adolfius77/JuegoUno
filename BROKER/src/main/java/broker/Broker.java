package broker;

import Interfacez.IBroker;
import Interfacez.ISerializador;
import Server.ServerProxy;
import dtos.MensajeDTO;
import fabricas.ServerProxyFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Broker implements IBroker {

    private ServerSocket servidorSocket;
    private int puerto;
    private List<Socket> clientesConectados;
    private Thread hiloAceptarClientes;
    private Map<String, List<Consumer<MensajeDTO>>> suscriptores = new ConcurrentHashMap<>();
    private Map<String , Socket> socketsJugadores = new HashMap<>();
    private ISerializador serializador;

    public Broker(int puerto, ISerializador serializador) {
        this.puerto = puerto;
        this.clientesConectados = new ArrayList<>();
        this.suscriptores = new ConcurrentHashMap<>();
        this.socketsJugadores = new HashMap<>();
        this.serializador = serializador;
    }

    private void aceptarClientes(){
        while(true){
            try{
                Socket clienteSocket = servidorSocket.accept();
                clientesConectados.add(clienteSocket);

                ServerProxy manejador = ServerProxyFactory.crearManjadorCliente(this, clienteSocket, serializador);

                Thread threadCliente = new Thread(manejador);
                threadCliente.start();

                System.out.println("Cliente conectado y proxy registrado.");
            }catch (IOException e){
                System.out.println("Error aceptando clientes: " + e.getMessage());
            }
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