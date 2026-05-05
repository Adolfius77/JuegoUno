package red;

import Entidades.Estados.IEstadoPartida;
import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Entidades.fabricas.MazoClasicoFactory;

import Interfacez.IBroker;
import Interfacez.IProxy;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;

import dtos.MensajeDTO;
import dtos.MensajeDesconexionDTO;
import dtos.MensajeEstadoPartidaDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;
import dtos.MensajeRegistroDTO;
import dtos.PartidaDTO;
import facades.GestorJuegoFacade;

import java.util.List;

public class ManejadorPartidaServidor {

    private final IBroker broker;
    private final ManejadorNodos manejadorNodos;
    private final ICartaFactory cartaFactory;
    private final IMazoFactory mazoFactory;
    private final IEstadoPartida estadoInicial;
    private GestorJuegoFacade fachadaJuego;

    public ManejadorPartidaServidor(IBroker broker) {
        this(broker, new CartaFactory(), new MazoClasicoFactory(), EstadoFactory.crearEstadoEsperando());
    }

    public ManejadorPartidaServidor(IBroker broker, ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estadoInicial) {
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

        this.broker.subscribirse("JUGADOR_DESCONECTADO", this::procesarConexionCerrada);
        this.broker.subscribirse("SOLICITUD_REGISTRO", this::procesarRegistroJugador);
    }

    private void procesarRegistroJugador(MensajeDTO mensaje) {
        if (mensaje instanceof MensajeRegistroDTO) {
            MensajeRegistroDTO registroDTO = (MensajeRegistroDTO) mensaje;

            String nombreReal = registroDTO.getNombre();
            String avatar = registroDTO.getNombreAvatar();
            String canalRespuesta = registroDTO.getTipo();

            System.out.println("ManejadorPartidaServidor: Registrando a " + nombreReal + " con avatar " + avatar);

            for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
                if (nodo.getNombre().startsWith("Jugador_")) {
                    String idTemporal = nodo.getNombre();

                    nodo.setAvatar(avatar);

                    manejadorNodos.actualizarIdentidadNodo(idTemporal, nombreReal);
                    break;
                }
            }

            MensajeNotificacionDTO respuestaExito = new MensajeNotificacionDTO("SERVIDOR", false, "Registro exitoso");
            respuestaExito.setTipo(canalRespuesta);
            broker.publicar(canalRespuesta, respuestaExito);

            difundirListaJugadores();
        }
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
        if (mensaje instanceof MensajeDesconexionDTO) {
            MensajeDesconexionDTO desconexionDTO = (MensajeDesconexionDTO) mensaje;
            String nombreJugador = desconexionDTO.getNombreUsuario();

            if (nombreJugador != null && !nombreJugador.isEmpty()) {
                System.out.println("ManejadorPartidaServidor: Retirando a " + nombreJugador + " de la partida.");

                for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
                    if (nombreJugador.equals(nodo.getNombre())) {
                        manejadorNodos.eliminarPorProxy(nodo.getProxy());
                        break;
                    }
                }

                difundirListaJugadores();
            }
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
