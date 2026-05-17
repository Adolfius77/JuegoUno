package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import vista.CrearPartida;
import vista.LobbyView;

public class CrearPartidaController {

    private IVista vista;
    private ClienteProxy proxy;
    private String nombreHostTemporal;

     private final Map<String, Consumer<MensajeDTO>> manejadoresEventos;

    public CrearPartidaController(CrearPartida vista, ClienteProxy proxy) {
        this.vista = vista;
        this.proxy = proxy;
        this.manejadoresEventos = new HashMap<>();

        inicializarComandos();
        configurarReceptorRed();
    }

    private void configurarReceptorRed() {
        this.proxy.setReceptor(this::procesarEventoRed);
    }

    private void inicializarComandos() {
        manejadoresEventos.put("SALA_CREADA", this::procesarSalaCreada);
        manejadoresEventos.put("ERROR_CREAR_PARTIDA", this::procesarError);
    }

    public void solicitarCreacion(String nombreHost) {
        solicitarCreacion(nombreHost, "Sala de " + nombreHost, 4);
    }

    public void solicitarCreacion(String nombreHost, String nombreSala, int limiteJugadores) {
        this.nombreHostTemporal = nombreHost;
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_CREAR_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreHost);
        peticion.getDatos().put("nombreSala", nombreSala);
        peticion.getDatos().put("limiteJugadores", limiteJugadores);

        System.out.println("[PARTIDA CONTROLLER] enviando peticion para crear partida");
        proxy.enviarMensaje(peticion);
    }

    private void procesarEventoRed(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getTipo() == null) {
            return;
        }

        String tipoMensaje = mensaje.getTipo();
        Consumer<MensajeDTO> manejador = manejadoresEventos.get(tipoMensaje);

        if (manejador != null) {
            manejador.accept(mensaje);
        } else {

            System.out.println("[CrearPartidaController] Evento ignorado: " + tipoMensaje);
        }
    }

    private void procesarSalaCreada(MensajeDTO mensaje) {
        String codigoSala = (String) mensaje.getDatos().get("codigoSala");
        String nombreHost = (String) mensaje.getDatos().getOrDefault("nombre", nombreHostTemporal);
        List<?> jugadoresTemp = null;
        Object jugadoresRaw = mensaje.getDatos().get("jugadores");

        if (jugadoresRaw instanceof List<?>) {
            jugadoresTemp = (List<?>) jugadoresRaw;
        }

        final List<?> jugadoresFinales = jugadoresTemp;

        SwingUtilities.invokeLater(() -> {
            LobbyView lobby = new LobbyView();
            LobbyController lobbyCtrl = new LobbyController(proxy, codigoSala, nombreHost, true, lobby);
            lobbyCtrl.cargarDatosIniciales(jugadoresFinales);

            vista.cerrarVista();
            lobby.setVisible(true);
        });
    }

    private void procesarError(MensajeDTO mensaje) {
        String motivoTemp = "Error al intentar crear la sala.";
        if (mensaje.getDatos() != null && mensaje.getDatos().get("motivo") != null) {
            motivoTemp = String.valueOf(mensaje.getDatos().get("motivo"));
        }
        final String motivoFinal = motivoTemp;

        SwingUtilities.invokeLater(() -> {
            if (vista != null) {
                vista.mostrarMensaje(motivoFinal);
            }
        });
    }
}
