package Server;

import Interfacez.IProxy;
import Interfacez.ISerializador;
import Nodos.NodoCliente;
import broker.Broker;
import dtos.MensajeDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import observador.observadorRed;
import red.LobbyServidor;
import red.Servidor;

public class ServerProxy implements observadorRed {

    private final Servidor servidor;
    private final Broker broker;
    private final ISerializador serializador;
    private final LobbyServidor lobbyServidor;
    private final Map<String, NodoCliente> nodosPorIp;
    private int contadorJugadores;

    public ServerProxy(int puerto, String ip, ISerializador serializador) {
        if (serializador == null) {
            throw new IllegalArgumentException("[Servidor-proxy]  El serializador no puede ser nulo.");
        }
        this.serializador = serializador;
        this.broker = new Broker();
        this.lobbyServidor = LobbyServidor.crearLobbyPorDefecto(this.broker);
        this.servidor = new Servidor(puerto, ip, serializador);
        this.servidor.agregarObservador(this);
        this.nodosPorIp = new ConcurrentHashMap<>();
        this.contadorJugadores = 1;
    }

    public void iniciar() {
        servidor.iniciar();
    }

    @Override
    public void onMensajeRecibido(String json, String ip) {
        if (json == null || json.isBlank()) {
            return;
        }
        System.out.println("[SERVER-PROXY] recibi el json pa : " + json);

        MensajeDTO mensaje = serializador.desearealizar(json);
        if (mensaje == null) {
            System.out.println("[Servidor-proxy]  No se pudo deserializar el mensaje: " + json);
            return;
        }
        if (mensaje.getTipo() == null || mensaje.getTipo().isBlank()) {
            return;
        }

        IProxy proxy = servidor.obtenerProxyPorIp(ip);
        NodoCliente nodoExistente = nodosPorIp.get(ip);
        boolean esNuevoPorIp = nodoExistente == null;
        boolean esNuevaConexionMismaIp = nodoExistente != null && nodoExistente.getProxy() != proxy;

        if (proxy != null && (esNuevoPorIp || esNuevaConexionMismaIp)) {
            if (esNuevaConexionMismaIp && nodoExistente.getProxy() != null) {
                lobbyServidor.eliminarJugadorPorProxy(nodoExistente.getProxy());
            }
            String nombreTemporal = "Jugador_" + contadorJugadores++;
            NodoCliente nuevoNodo = new NodoCliente(nombreTemporal, proxy, "no hay");
            nodosPorIp.put(ip, nuevoNodo);
            lobbyServidor.registrarNuevoJugadorTemporal(nuevoNodo);
            System.out.println("[SERVER-PROXY] Jugador conectado temporalmente. Esperando mensaje de registro...");
        }

        if ("REGISTRO_JUGADOR".equals(mensaje.getTipo())
                || "PETICION_CREAR_PARTIDA".equals(mensaje.getTipo())
                || "PETICION_UNIRSE_PARTIDA".equals(mensaje.getTipo())
                || "PETICION_LISTA_PARTIDAS".equals(mensaje.getTipo())
                || "ACTUALIZAR_ESTADO_LISTO".equals(mensaje.getTipo())
                || "INTENCION_INICIAR_PARTIDA".equals(mensaje.getTipo())
                || "PETICION_JUGAR_CARTA".equals(mensaje.getTipo())
                || "PETICION_TOMAR_CARTA".equals(mensaje.getTipo())
                || "PETICION_GRITAR_UNO".equals(mensaje.getTipo())
                || "PETICION_PASAR_TURNO".equals(mensaje.getTipo())) {
            if (mensaje.getDatos() == null) {
                mensaje.setDatos(new HashMap<>());
            }
            if (proxy != null) {
                mensaje.getDatos().put("proxy", proxy);
            }
        }

        broker.publicar(mensaje.getTipo(), mensaje);
    }
}