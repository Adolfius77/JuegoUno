package Server;

import Interfacez.IBroker;
import Interfacez.IProxy;
import Interfacez.ISerializador;
import Nodos.NodoCliente;
import broker.Broker;
import dtos.MensajeDTO;
import java.util.concurrent.atomic.AtomicInteger;
import observador.observadorRed;
import red.Servidor;
import servidor.LobbyServidor;

/**
 * Proxy del lado servidor del patron Broker.
 *
 * Su unica responsabilidad es traducir entre la red y el bus: deserializa,
 * marca de que sesion viene el mensaje y lo publica. No conoce los tipos de
 * evento; antes tenia una cadena de diez comparaciones que habia que ampliar
 * cada vez que se agregaba un evento.
 */
public class ServerProxy implements observadorRed {

    private final Servidor servidor;
    private final IBroker broker;
    private final ISerializador serializador;
    private final LobbyServidor lobbyServidor;
    private final AtomicInteger contadorJugadores = new AtomicInteger(1);

    public ServerProxy(int puerto, String ip, ISerializador serializador) {
        if (serializador == null) {
            throw new IllegalArgumentException("[Servidor-proxy]  El serializador no puede ser nulo.");
        }
        this.serializador = serializador;
        this.broker = new Broker();
        this.lobbyServidor = LobbyServidor.crearLobbyPorDefecto(this.broker);
        this.servidor = new Servidor(puerto, ip, serializador);
        this.servidor.agregarObservador(this);
    }

    public void iniciar() {
        servidor.iniciar();
    }

    @Override
    public void onMensajeRecibido(String json, String idSesion) {
        if (json == null || json.isBlank()) {
            return;
        }

        MensajeDTO mensaje = serializador.desearealizar(json);
        if (mensaje == null) {
            System.out.println("[Servidor-proxy]  No se pudo deserializar el mensaje: " + json);
            return;
        }
        if (mensaje.getTipo() == null || mensaje.getTipo().isBlank()) {
            return;
        }

        // La sesion la fija el servidor, nunca el cliente.
        mensaje.setIdSesion(idSesion);

        if ("DESCONEXION".equalsIgnoreCase(mensaje.getTipo())) {
            lobbyServidor.eliminarJugadorPorSesion(idSesion);
            return;
        }

        registrarSesionSiEsNueva(idSesion);

        broker.publicar(mensaje.getTipo(), mensaje);
    }

    private void registrarSesionSiEsNueva(String idSesion) {
        if (lobbyServidor.conoceSesion(idSesion)) {
            return;
        }
        IProxy proxy = servidor.obtenerProxy(idSesion);
        if (proxy == null) {
            return;
        }
        String nombreTemporal = "Jugador_" + contadorJugadores.getAndIncrement();
        lobbyServidor.registrarNuevoJugadorTemporal(
                new NodoCliente(idSesion, proxy, nombreTemporal, "no hay"));
        System.out.println("[SERVER-PROXY] Sesion " + idSesion
                + " conectada como " + nombreTemporal + ". Esperando registro...");
    }
}
