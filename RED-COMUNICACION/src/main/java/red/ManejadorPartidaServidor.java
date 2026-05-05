package red;

import Entidades.Estados.IEstadoPartida;
import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Entidades.fabricas.MazoClasicoFactory;

// Importaciones corregidas para tu arquitectura
import Interfacez.IBroker; 
import Interfacez.IProxy;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos; 
import Nodos.NodoCliente;    

import dtos.MensajeDTO;
import dtos.MensajeDesconexionDTO; // Agregado para manejar la nueva desconexión
import dtos.MensajeEstadoPartidaDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;
import dtos.PartidaDTO;
import facades.GestorJuegoFacade;

import java.util.List;

/**
 * Cerebro del servidor que maneja las reglas del juego.
 * Interactúa con la red únicamente a través del IBroker.
 */
public class ManejadorPartidaServidor {
    
    private final IBroker broker; // Usamos la interfaz en lugar de la clase concreta
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

        // Suscripciones a los eventos de la red
        this.broker.subscribirse("INTENCION_INICIAR_PARTIDA", this::procesarInicioPartida);
        // Actualizado para escuchar el evento exacto que lanza tu ServerProxy
        this.broker.subscribirse("JUGADOR_DESCONECTADO", this::procesarConexionCerrada); 
    }

    /**
     * Registra un nodo recién conectado en la lógica del juego.
     */
    public void registrarNuevoJugador(NodoCliente nuevoNodo) {
        manejadorNodos.registrarNuevoJugador(nuevoNodo);
        difundirListaJugadores();
    }

    /**
     * Inicia la partida si se cumplen las reglas (mínimo 2 jugadores).
     */
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

    /**
     * Procesa la desconexión de un jugador y lo saca de la partida.
     */
    private void procesarConexionCerrada(MensajeDTO mensaje) {
        // Verificamos si el mensaje es de la clase correcta que configuramos en ServerProxy
        if (mensaje instanceof MensajeDesconexionDTO) {
            MensajeDesconexionDTO desconexionDTO = (MensajeDesconexionDTO) mensaje;
            String nombreJugador = desconexionDTO.getNombreUsuario();
            
            if (nombreJugador != null && !nombreJugador.isEmpty()) {
                System.out.println("ManejadorPartidaServidor: Retirando a " + nombreJugador + " de la partida.");
                
                // Buscamos el nodo en la lista actual basándonos en su nombre
                for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
                    if (nombreJugador.equals(nodo.getNombre())) {
                        // Una vez encontrado, usamos tu método original para eliminarlo usando su proxy
                        manejadorNodos.eliminarPorProxy(nodo.getProxy());
                        break; // Terminamos la búsqueda
                    }
                }
                
                // Actualizamos las pantallas de los demás jugadores
                difundirListaJugadores();
            }
        }
    }

    /**
     * Envía la lista actualizada a todos los nodos conectados.
     */
    private void difundirListaJugadores() {
        MensajeListaJugadoresDTO listaActualizada = new MensajeListaJugadoresDTO(manejadorNodos.obtenerNombresDeNodosConectados());
        listaActualizada.setTipo("LISTA_ACTUALIZADA");

        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(listaActualizada);
        }
    }

    /**
     * Envía un mensaje global de notificación.
     */
    private void difundirNotificacion(String texto) {
        MensajeNotificacionDTO notificacion = new MensajeNotificacionDTO("SERVIDOR", true, texto);
        notificacion.setTipo("NOTIFICACION_SERVIDOR");
        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(notificacion);
        }
    }
}