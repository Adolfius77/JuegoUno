package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;
import dtos.MensajeRegistroDTO;
import java.util.HashMap;
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

    public LobbyController(ClienteProxy clienteProxy, String codigoSala, String nombreHost, Boolean esHost, LobbyView lobby) {
        if (clienteProxy == null) {
            throw new IllegalArgumentException("El ClienteProxy es obligatorio para la red.");
        }
        this.clienteProxy = clienteProxy;
        this.codigoSala = codigoSala;
        this.nombreJugadorLocal = nombreHost;
        this.esHost = esHost;
        this.vista = lobby;

        configurarReceptorRed();

        SwingUtilities.invokeLater(() -> {
            if (this.vista != null) {
                this.vista.actualizar("ACTUALIZACION_INICIAL");
            }
        });
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

    public void iniciarPartida() {
        System.out.println("Controlador: Solicitando al servidor iniciar la partida...");
        MensajeDTO msjInicio = new MensajeDTO("PETICION_INICIAR_PARTIDA", null);
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
        else if ("INTENCION_INICIAR_PARTIDA".equals(tipoMensaje)) {
            System.out.println("La partida va a comenzar, cambiando de pantalla...");

            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    this.vista.cerrarVista();
                }

                GameView vistaJuego = new GameView();
                GameController controladorJuego = new GameController(this.clienteProxy, vistaJuego, this.getNombreJugadorLocal());
                vistaJuego.setVisible(true);
            });
        } 
        else if ("LISTA_ACTUALIZADA".equals(tipoMensaje)) {
            if (mensaje.getDatos() != null && mensaje.getDatos().containsKey("jugadores")) {

                this.listaJugadores = (List<Map<String, String>>) mensaje.getDatos().get("jugadores");

                SwingUtilities.invokeLater(() -> {
                    System.out.println("Controlador: Notificando a la vista que la lista cambió...");

                    if (this.vista != null) {
                        this.vista.actualizar("CAMBIO_LISTA_JUGADORES");
                    }
                });
            }
        }
    }
}
