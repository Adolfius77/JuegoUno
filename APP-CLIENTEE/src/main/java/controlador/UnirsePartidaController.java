package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

import vista.LobbyView;
import vista.unirsePartidaView;

public class UnirsePartidaController {

    private final IVista vista;
    private final unirsePartidaView vistaUnirse;
    private final ClienteProxy proxy;

    private String nombreInvitadoTemporal;
    private String codigoSalaTemporal;
    private boolean esperandoRespuestaUnirse;

    private final Map<String, Consumer<MensajeDTO>> manejadoresEventos;

    public UnirsePartidaController(unirsePartidaView vista, ClienteProxy proxy) {
        this.vista = vista;
        this.vistaUnirse = vista;
        this.proxy = proxy;
        this.manejadoresEventos = new HashMap<>();

        inicializarComandos();

        if (this.proxy != null) {
            this.proxy.setReceptor(this::escucharEventoRed);
        }
    }

    private void inicializarComandos() {
        manejadoresEventos.put("UNIDO_EXITO", this::procesarUnidoExito);
        manejadoresEventos.put("ERROR_UNIRSE", this::procesarErrorUnirse);
        manejadoresEventos.put("LISTA_PARTIDAS_DISPONIBLES", this::procesarListaPartidas);
    }

    public void solicitarListaPartidas() {
        if (proxy == null) {
            return;
        }
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_LISTA_PARTIDAS");
        peticion.setRemitente("CLIENTE");
        proxy.enviarMensaje(peticion);
    }

    public void solicitarUnirse(String nombreInvitado, String codigoSala) {
        if (proxy == null) {
            return;
        }
        String codigoNormalizado = codigoSala != null ? codigoSala.trim().toUpperCase() : "";
        if (codigoNormalizado.isBlank()) {
            return;
        }

        this.nombreInvitadoTemporal = nombreInvitado;
        this.codigoSalaTemporal = codigoNormalizado;
        this.esperandoRespuestaUnirse = true;

        System.out.println("[UNIRSE PARTIDA CONTROLLER] Solicitando unirse a la partida");
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_UNIRSE_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreInvitado);
        peticion.getDatos().put("codigoSala", codigoNormalizado);

        proxy.enviarMensaje(peticion);
    }

    private void escucharEventoRed(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getTipo() == null) {
            return;
        }

        String tipoMensaje = mensaje.getTipo();
        Consumer<MensajeDTO> manejador = manejadoresEventos.get(tipoMensaje);

        if (manejador != null) {
            manejador.accept(mensaje);
        } else {
            System.out.println("[UnirsePartidaController] Evento ignorado: " + tipoMensaje);
        }
    }

    private void procesarUnidoExito(MensajeDTO mensaje) {
        if (!esperandoRespuestaUnirse || mensaje.getDatos() == null) {
            return;
        }

        String codigoSala = (String) mensaje.getDatos().get("codigoSala");
        if (codigoSalaTemporal != null && codigoSala != null
                && !codigoSalaTemporal.equalsIgnoreCase(codigoSala.trim())) {
            return;
        }

        esperandoRespuestaUnirse = false;
        String nombreTemp = (String) mensaje.getDatos().getOrDefault("nombre", nombreInvitadoTemporal);

        List<?> jugadoresRawList = null;
        Object jugadoresRaw = mensaje.getDatos().get("jugadores");
        if (jugadoresRaw instanceof List<?>) {
            jugadoresRawList = (List<?>) jugadoresRaw;
        }

        final String nombreFinal = nombreTemp;
        final String codigoSalaFinal = codigoSala;
        final List<?> jugadoresFinal = jugadoresRawList;

        SwingUtilities.invokeLater(() -> {
            LobbyView lobby = new LobbyView();
            LobbyController control = new LobbyController(proxy, codigoSalaFinal, nombreFinal, false, lobby);
            control.cargarDatosIniciales(jugadoresFinal);

            if (vista != null) {
                vista.cerrarVista();
            }
            lobby.setVisible(true);
        });
    }

    private void procesarErrorUnirse(MensajeDTO mensaje) {
        if (!esperandoRespuestaUnirse) {
            return;
        }

        esperandoRespuestaUnirse = false;

        String motivoTemp = "Error desconocido";
        if (mensaje.getDatos() != null) {
            motivoTemp = String.valueOf(mensaje.getDatos().getOrDefault("motivo", "Error desconocido"));
        }

        final String motivoFinal = motivoTemp;

        SwingUtilities.invokeLater(() -> {
            if (vista != null) {
                vista.mostrarMensaje("Error: " + motivoFinal + " - No se pudo unirse a la partida.");
            }
        });
    }

    private void procesarListaPartidas(MensajeDTO mensaje) {
        Object partidasRaw = mensaje.getDatos() != null ? mensaje.getDatos().get("partidas") : null;

        final List<Map<String, Object>> partidasFinales = normalizarPartidas(partidasRaw);

        SwingUtilities.invokeLater(() -> {
            if (vistaUnirse != null) {
                vistaUnirse.mostrarPartidasDisponibles(partidasFinales);
            }
        });
    }

    private List<Map<String, Object>> normalizarPartidas(Object partidasRaw) {
        List<Map<String, Object>> normalizadas = new ArrayList<>();
        if (!(partidasRaw instanceof List<?> listaRaw)) {
            return normalizadas;
        }

        for (Object partidaRaw : listaRaw) {
            if (!(partidaRaw instanceof Map<?, ?> mapaRaw)) {
                continue;
            }
            Map<String, Object> partida = new HashMap<>();
            partida.put("codigoSala", String.valueOf(valorOMinimo(mapaRaw, "codigoSala", "")));
            partida.put("nombreSala", String.valueOf(valorOMinimo(mapaRaw, "nombreSala", "Sala sin nombre")));
            partida.put("host", String.valueOf(valorOMinimo(mapaRaw, "host", "")));
            partida.put("jugadoresActuales", valorOMinimo(mapaRaw, "jugadoresActuales", 0));
            partida.put("limiteJugadores", valorOMinimo(mapaRaw, "limiteJugadores", 4));
            normalizadas.add(partida);
        }
        return normalizadas;
    }

    private Object valorOMinimo(Map<?, ?> mapaRaw, String llave, Object porDefecto) {
        Object valor = mapaRaw.get(llave);
        return valor != null ? valor : porDefecto;
    }
}
