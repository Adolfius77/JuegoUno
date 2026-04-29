package broker;

import Interfacez.IBroker;
import Interfacez.ISerializador;
import Nodos.NodoCliente;
import Server.ServerProxy;
import dtos.MensajeDTO;
import fabricas.ServerProxyFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Broker implements IBroker {

    private ServerSocket servidorSocket;
    private int puerto;
    private Thread hiloAceptarClientes;
    private Map<String, List<Consumer<MensajeDTO>>> suscriptores = new ConcurrentHashMap<>();
    private Map<String , NodoCliente> NodoClientes = new ConcurrentHashMap<>();
    private ISerializador serializador;

    public Broker(int puerto, ISerializador serializador) {
        this.puerto = puerto;
        this.suscriptores = new ConcurrentHashMap<>();
        this.serializador = serializador;
    }

    private void aceptarClientes(){
        while(true){
            try{
                Socket clienteSocket = servidorSocket.accept();
                String idTemporal = "conexion" + clienteSocket.getPort();
                ServerProxy proxy = ServerProxyFactory.crearManjadorCliente(this, clienteSocket,serializador);
                NodoCliente nuevoNodo = new NodoCliente(clienteSocket, proxy, idTemporal);
                NodoClientes.put(idTemporal, nuevoNodo);
                new Thread(proxy).start();
                System.out.println("nuevo nodo conectado" + idTemporal);
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
    public void actualizarIdentidadNodo(String idTemporal, String nombreReal){
        NodoCliente nodo = NodoClientes.remove(idTemporal);

        if(nodo != null){
            nodo.setIdJugador(nombreReal);
            NodoClientes.put(nombreReal, nodo);
            System.out.println("identidad confirmada" +  idTemporal + nombreReal);
        }
    }
    public void enviarNodo(String idTemporal, MensajeDTO mensaje){
        NodoCliente nodo = NodoClientes.get(idTemporal);
        if(nodo != null){
            nodo.enviarMensaje(mensaje);
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