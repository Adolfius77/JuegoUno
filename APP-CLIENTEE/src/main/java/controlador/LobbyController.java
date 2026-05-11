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

    public LobbyController(ClienteProxy clienteProxy) {
        if (clienteProxy == null) {
            throw new IllegalArgumentException("El ClienteProxy es obligatorio para la red.");
        }
        this.clienteProxy = clienteProxy;
        configurarReceptorRed();
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

            //aqui aplico el patron sobre
            //en esta parte defino el sobre
            MensajeRegistroDTO msjRegistro = new MensajeRegistroDTO();
            msjRegistro.setTipo("REGISTRO_JUGADOR");
            msjRegistro.setRemitente("CLIENTE");
            //metemos los datos al mapa esto es basicamente el contenido del sobre
            Map<String, Object> datos = new HashMap<>();
            datos.put("nombre", nombreJugador);
            datos.put("avatar", avatar);
            //sellamos el sobre y lo enviamos
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
        //CU EMILIANO MARQUEZ
        if ("REGISTRO_EXITOSO".equals(tipoMensaje)) {
            System.out.println("LobbyController: Registro confirmado. Cambiando a SeleccionPartida...");
            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    this.vista.cerrarVista();
                }
                SeleccionPartida seleccionVista = new SeleccionPartida(nombreJugadorTemporal, nombreAvatarTemporal);
                seleccionVista.setVisible(true);
                this.setVista(seleccionVista);
            });
        }
        if ("SALA_CREADA".equals(tipoMensaje)) {
            System.out.println("LobbyController: Registro confirmado. Cambiando a SeleccionPartida...");
            String codigoGenerado = (String)mensaje.getDatos().get("codigoSala");
            String host = (String)mensaje.getDatos().get("host");

            SwingUtilities.invokeLater(() -> {
                System.out.println("sala creada con el codigo: " + codigoGenerado + " y el host es: " + host);
                if (vista != null) {
                    this.vista.cerrarVista();
                }
                LobbyView lobbyView = new LobbyView(codigoGenerado,host);
                this.setVista(lobbyView);
            });
        }
        //CU ADOLFO ORTEGA
        if ("INTENCION_INICIAR_PARTIDA".equals(tipoMensaje)) {
            System.out.println("La partida va a comenzar, cambiando de pantalla...");

            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    this.vista.cerrarVista();
                }

                GameView vistaJuego = new GameView();
                //me falta que reciba el nombre de los jugadores y el estado
                GameController controladorJuego = new GameController(this.clienteProxy, vistaJuego, null);
                vistaJuego.setVisible(true);
            });

        } else if ("LISTA_ACTUALIZADA".equals(tipoMensaje)) {
            if (mensaje.getDatos() != null && mensaje.getDatos().containsKey("jugadores")) {
                List<String> listaJugadores = (List<String>) mensaje.getDatos().get("jugadores");
            }

            SwingUtilities.invokeLater(() -> {
                System.out.println("Actualizando pantalla con los nuevos jugadores...");

            });
        }
    }
}
