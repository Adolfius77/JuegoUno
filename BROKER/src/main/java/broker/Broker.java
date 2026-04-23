package broker;


import Interfacez.IBroker;
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
    private Map<String , Socket> socketsJugadores;
    private static final int MAX_JUGADORES = 4;
    private static final int MIN_JUGADORES = 2;
    private boolean partidaEnCurso;
    //nodos conectados
    private List<ServerProxy>NodosConectados;
    private ISerializador serializador;


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
                String ipCliente = clienteSocket.getInetAddress().getHostAddress();

                System.out.println("cliente conectado desde " + ipCliente);
                clientesConectados.add(clienteSocket);
                ServerProxy manejador = ServerProxyFactory.crearManjadorCliente(this, clienteSocket,serializador);
                Thread threadCliente = new Thread(manejador);
                threadCliente.start();
            }catch (IOException e){
                System.out.println("eror aceptando clientes" + e.getMessage());
            }
        }
    }
    public void iniciarServidor(){
        try{
            servidorSocket = new ServerSocket(puerto);
            System.out.println("servidor iniciado en el puerto " + puerto);
            System.out.println(" maximo de jugadores: " + MAX_JUGADORES);
            System.out.println("minimo de jugadores para iniciar: " + MIN_JUGADORES);
            hiloAceptarClientes = new Thread(this::aceptarClientes);
            hiloAceptarClientes.setName("aceptarClientes");
            hiloAceptarClientes.setDaemon(true);
            hiloAceptarClientes.start();
        }catch (IOException e){
            System.err.println("error al iniciar el servidor" + e.getMessage());
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
    public void transmitirAtodos(MensajeDTO mensaje){
        for(ServerProxy proxy : NodosConectados){
            proxy.enviarMensaje(mensaje);
        }
    }
}
