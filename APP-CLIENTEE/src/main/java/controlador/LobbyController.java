package controlador;

import Interfaces.IVista;
import dtos.MensajeDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeRegistroDTO;
import javax.swing.SwingUtilities;
import red.ClienteProxy;
import vista.GameView;

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

            MensajeRegistroDTO msjRegistro = new MensajeRegistroDTO();
            msjRegistro.setNombre(nombreJugador);
            msjRegistro.setNombreAvatar(avatar);
            msjRegistro.setTipo("REGISTRO_JUGADOR");

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

        String tipoMensaje = mensaje.getTipo();

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
            MensajeListaJugadoresDTO listaDTO = (MensajeListaJugadoresDTO) mensaje;

            SwingUtilities.invokeLater(() -> {
                System.out.println("Actualizando pantalla con los nuevos jugadores...");

            });
        }
    }
}
