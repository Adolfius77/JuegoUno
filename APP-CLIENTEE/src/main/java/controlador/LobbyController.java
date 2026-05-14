package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;
import dtos.MensajeRegistroDTO;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

import vista.GameView;
import vista.LobbyView;
import vista.SeleccionPartida;

public class LobbyController {

    private IVista vista;
    private final ClienteProxy clienteProxy;

    private String nombreJugadorTemporal;
    private String nombreAvatarTemporal;
    private Boolean esHost;

    private String codigoSala;
    private String nombreJugadorLocal;
    private List<Map<String, String>> listaJugadores;
    private LobbyView lobby;

    public LobbyController(ClienteProxy clienteProxy, String codigoSala, String nombreHost, Boolean esHost, LobbyView lobby) {
        if (clienteProxy == null) {
            throw new IllegalArgumentException("El ClienteProxy es obligatorio para la red.");
        }
        this.clienteProxy = clienteProxy;
        this.codigoSala = codigoSala;
        this.nombreJugadorLocal = nombreHost;
        this.esHost = esHost;
        this.lobby = lobby;
        this.vista = lobby;

        if (this.lobby != null) {
            this.lobby.setControlador(this);

            SwingUtilities.invokeLater(() -> {
                if (this.vista != null) {
                    this.vista.actualizar("ACTUALIZACION_INICIAL");
                }
            });
        }
        configurarReceptorRed();
    }

    public void cargarDatosIniciales(List<?> jugadoresIniciales) {
        this.listaJugadores = normalizarJugadores(jugadoresIniciales);
        if (this.vista != null) {
            SwingUtilities.invokeLater(() -> this.vista.actualizar("ACTUALIZACION_INICIAL"));
        }
    }

    public List<Map<String, String>> getListaJugadores() {
        return listaJugadores;
    }

    public String getCodigoSala() {
        return codigoSala;
    }

    public String getNombreJugadorLocal() {
        return nombreJugadorLocal;
    }

    public boolean esHost() {
        return Boolean.TRUE.equals(esHost);
    }

    public boolean estaJugadorLocalListo() {
        if (listaJugadores == null || nombreJugadorLocal == null) {
            return false;
        }
        for (Map<String, String> jugador : listaJugadores) {
            if (nombreJugadorLocal.equals(jugador.get("nombre"))) {
                return Boolean.parseBoolean(jugador.getOrDefault("estaListo", "false"));
            }
        }
        return false;
    }

    public boolean estanTodosListos() {
        if (listaJugadores == null || listaJugadores.isEmpty()) {
            return false;
        }
        for (Map<String, String> jugador : listaJugadores) {
            if (!Boolean.parseBoolean(jugador.getOrDefault("estaListo", "false"))) {
                return false;
            }
        }
        return true;
    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    private void configurarReceptorRed() {
        clienteProxy.setReceptor(mensaje -> {
            procesarEventoRed(mensaje);
        });
    }

    public void registrarJugador(String nombreJugador, String avatar) {
        if (nombreJugador != null && !nombreJugador.trim().isEmpty()) {
            System.out.println("Controlador: Solicitando registro para " + nombreJugador + " con el avatar " + avatar);

            this.nombreJugadorTemporal = nombreJugador;
            this.nombreAvatarTemporal = avatar;

            MensajeRegistroDTO msjRegistro = new MensajeRegistroDTO();
            msjRegistro.setTipo("REGISTRO_JUGADOR");
            msjRegistro.setRemitente("CLIENTE");

            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombreJugador);
            datos.put("avatar", avatar);

            msjRegistro.setDatos(datos);
            clienteProxy.enviarMensaje(msjRegistro);
        } else {
            if (vista != null) {
                vista.mostrarMensaje("El nombre del jugador es obligatorio");
            }
        }
    }

    public void marcarJugadorLocalListo() {
        actualizarEstadoListo(true);
    }

    public void cancelarJugadorLocalListo() {
        actualizarEstadoListo(false);
    }

    private void actualizarEstadoListo(boolean estaListo) {
        MensajeDTO mensajeListo = new MensajeDTO("ACTUALIZAR_ESTADO_LISTO", "CLIENTE");
        Map<String, Object> datos = new HashMap<>();
        datos.put("estaListo", estaListo);
        datos.put("codigoSala", codigoSala);
        mensajeListo.setDatos(datos);
        clienteProxy.enviarMensaje(mensajeListo);
    }

    public void iniciarPartida() {
        if (!esHost()) {
            if (vista != null) {
                vista.mostrarMensaje("Solo el host puede iniciar la partida.");
            }
            return;
        }
        if (!estanTodosListos()) {
            if (vista != null) {
                vista.mostrarMensaje("Todos los jugadores deben estar listos para iniciar.");
            }
            return;
        }
        System.out.println("Controlador: Solicitando al servidor iniciar la partida...");
        MensajeDTO msjInicio = new MensajeDTO("INTENCION_INICIAR_PARTIDA", "CLIENTE");
        Map<String, Object> datos = new HashMap<>();
        datos.put("codigoSala", codigoSala);
        msjInicio.setDatos(datos);
        clienteProxy.enviarMensaje(msjInicio);
    }

    public void procesarEventoRed(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }
        String tipoMensaje = mensaje.getTipo();

        // CU EMILIANO MARQUEZ
        if ("REGISTRO_EXITOSO".equals(tipoMensaje)) {
            System.out.println("LobbyController: Registro confirmado. Cambiando a SeleccionPartida...");
            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    this.vista.cerrarVista();
                }
                SeleccionPartida seleccionVista = new SeleccionPartida(nombreJugadorTemporal, nombreAvatarTemporal, this.clienteProxy);
                seleccionVista.setVisible(true);
                this.setVista(seleccionVista);
            });
        } // CU ADOLFO ORTEGA
        else if ("PARTIDA_INICIADA".equals(tipoMensaje) || "INTENCION_INICIAR_PARTIDA".equals(tipoMensaje)) {
            System.out.println("La partida va a comenzar, cambiando de pantalla...");

            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    this.vista.cerrarVista();
                }

                GameView vistaJuego = new GameView();
                GameController controladorJuego = new GameController(this.clienteProxy, vistaJuego, this.getNombreJugadorLocal());
                if ("PARTIDA_INICIADA".equals(tipoMensaje)) {
                    controladorJuego.procesarEventoRed(mensaje);
                }
                vistaJuego.setVisible(true);
            });
        } else if ("LISTA_ACTUALIZADA".equals(tipoMensaje)) {
            if (mensaje.getDatos() != null && mensaje.getDatos().containsKey("jugadores")) {
                Object jugadores = mensaje.getDatos().get("jugadores");
                if (jugadores instanceof List<?>) {
                    this.listaJugadores = normalizarJugadores((List<?>) jugadores);
                }

                SwingUtilities.invokeLater(() -> {
                    System.out.println("Controlador: Notificando a la vista que la lista cambio...");

                    if (this.vista != null) {
                        this.vista.actualizar("CAMBIO_LISTA_JUGADORES");
                    }
                });
            }
        } else if ("ERROR_INICIAR_PARTIDA".equals(tipoMensaje)) {
            if (vista != null) {
                String motivo = "No se pudo iniciar la partida.";
                if (mensaje.getDatos() != null && mensaje.getDatos().get("motivo") != null) {
                    motivo = String.valueOf(mensaje.getDatos().get("motivo"));
                }
                vista.mostrarMensaje(motivo);
            }
        }
    }

    private List<Map<String, String>> normalizarJugadores(List<?> jugadoresRaw) {
        List<Map<String, String>> normalizados = new ArrayList<>();
        if (jugadoresRaw == null) {
            return normalizados;
        }
        for (Object item : jugadoresRaw) {
            if (item instanceof Map<?, ?>) {
                Map<?, ?> mapaRaw = (Map<?, ?>) item;
                Map<String, String> jugador = new HashMap<>();
                Object nombre = mapaRaw.get("nombre");
                Object avatar = mapaRaw.get("avatar");
                Object estaListo = mapaRaw.get("estaListo");
                jugador.put("nombre", nombre != null ? String.valueOf(nombre) : "");
                jugador.put("avatar", avatar != null ? String.valueOf(avatar) : "pfp");
                jugador.put("estaListo", estaListo != null ? String.valueOf(estaListo) : "false");
                normalizados.add(jugador);
            }
        }
        return normalizados;
    }
}
