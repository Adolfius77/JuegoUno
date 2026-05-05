package red;

import Entidades.Estados.IEstadoPartida;
import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Entidades.fabricas.MazoClasicoFactory;
import Interfacez.IProxy;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import broker.Broker;
import dtos.MensajeDTO;
import dtos.MensajeEstadoPartidaDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;
import dtos.PartidaDTO;
import facades.GestorJuegoFacade;

import java.util.List;

public class ManejadorPartidaServidor {
    private final Broker broker;
    private final ManejadorNodos manejadorNodos;
    private final ICartaFactory cartaFactory;
    private final IMazoFactory mazoFactory;
    private final IEstadoPartida estadoInicial;
    private GestorJuegoFacade fachadaJuego;

    public ManejadorPartidaServidor(Broker broker) {
        this(broker, new CartaFactory(), new MazoClasicoFactory(), EstadoFactory.crearEstadoEsperando());
    }

    public ManejadorPartidaServidor(Broker broker, ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estadoInicial) {
        if (broker == null) {
            throw new IllegalArgumentException("El broker no puede ser nulo.");
        }
        if (cartaFactory == null || mazoFactory == null || estadoInicial == null) {
            throw new IllegalArgumentException("Las dependencias de partida no pueden ser nulas.");
        }
        this.broker = broker;
        this.manejadorNodos = new ManejadorNodos();
        this.cartaFactory = cartaFactory;
        this.mazoFactory = mazoFactory;
        this.estadoInicial = estadoInicial;
        this.fachadaJuego = new GestorJuegoFacade(this.cartaFactory, this.mazoFactory, this.estadoInicial);

        this.broker.subscribirse("INTENCION_INICIAR_PARTIDA", this::procesarInicioPartida);
        this.broker.subscribirse("CONEXION_CERRADA", this::procesarConexionCerrada);
    }

    public void registrarNuevoJugador(NodoCliente nuevoNodo) {
        manejadorNodos.registrarNuevoJugador(nuevoNodo);
        difundirListaJugadores();
    }

    private void procesarInicioPartida(MensajeDTO mensaje) {
        List<String> nombresJugadores = manejadorNodos.obtenerNombresDeNodosConectados();

        if (nombresJugadores.size() < 2) {
            difundirNotificacion("No hay suficientes jugadores para iniciar la partida.");
            return;
        }

        fachadaJuego = new GestorJuegoFacade(cartaFactory, mazoFactory, estadoInicial);
        fachadaJuego.prepararIniciarPartida(nombresJugadores);

        PartidaDTO estadoInicialDTO = PartidaMapper.toDTO(fachadaJuego.getPartidaActual());
        MensajeEstadoPartidaDTO estadoPartida = new MensajeEstadoPartidaDTO();
        estadoPartida.setTipo("PARTIDA_INICIADA");
        estadoPartida.setPartida(estadoInicialDTO);

        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(estadoPartida);
        }
    }

    private void procesarConexionCerrada(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }
        Object proxy = mensaje.getDatos().get("proxy");
        if (proxy instanceof IProxy proxyDesconectado) {
            manejadorNodos.eliminarPorProxy(proxyDesconectado);
            difundirListaJugadores();
        }
    }

    private void difundirListaJugadores() {
        MensajeListaJugadoresDTO listaActualizada = new MensajeListaJugadoresDTO(manejadorNodos.obtenerNombresDeNodosConectados());
        listaActualizada.setTipo("LISTA_ACTUALIZADA");

        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(listaActualizada);
        }
    }

    private void difundirNotificacion(String texto) {
        MensajeNotificacionDTO notificacion = new MensajeNotificacionDTO("SERVIDOR", true, texto);
        notificacion.setTipo("NOTIFICACION_SERVIDOR");
        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(notificacion);
        }
    }
}
