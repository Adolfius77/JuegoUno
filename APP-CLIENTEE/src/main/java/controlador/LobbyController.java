package controlador;

import Interfaces.IVista;
import dtos.MensajeDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeRegistroDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import red.ClienteProxy;
import vista.GameView;
import vista.SeleccionPartida;

public class LobbyController {

    private IVista vista;
    private ClienteProxy clienteProxy;

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
        if ("REGISTRO_EXITOSO".equals(tipoMensaje)) {
            System.out.println("LobbyController: Registro confirmado. Cambiando a SeleccionPartida...");
            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    vista.cerrarVista();
                }
                SeleccionPartida seleccionVista = new SeleccionPartida();
                seleccionVista.setVisible(true);
                this.setVista(seleccionVista);
            });
        }
        if ("PARTIDA_INICIADA".equals(tipoMensaje)) {
            System.out.println("La partida va a comenzar, cambiando de pantalla...");

            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    vista.cerrarVista();
                }

                GameView vistaJuego = new GameView();
                // Nota: Aquí deberás instanciar tu GameController pasándole el clienteProxy y la vistaJuego
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
