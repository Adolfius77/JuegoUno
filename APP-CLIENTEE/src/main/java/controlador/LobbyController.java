package controlador;

import Interfaces.IVista;
import java.util.ArrayList;
import Controladores.ServerController;
import dtos.MensajeDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeRegistroDTO;
import javax.swing.SwingUtilities;
import red.ClienteProxy;
import vista.GameView;

public class LobbyController {

    private final IVista vista;
    private final ServerController gestor;
    private ClienteProxy clienteProxy;

    public LobbyController(IVista vista, ServerController gestor, ClienteProxy clienteProxy) {
        if (vista == null || gestor == null) {
            throw new IllegalArgumentException("vista y gestor son obligatorios");
        }
        this.clienteProxy = clienteProxy;
        this.vista = vista;
        this.gestor = gestor;
        configurarRecepetorRed();
    }

    private void configurarRecepetorRed() {
        clienteProxy.setReceptor(mensaje -> {
            procesarEventoRed(mensaje);
        });
    }

    public boolean agregarJugador(String nombreJugador) {
        try {
            if (nombreJugador == null) {
                vista.mostrarMensaje("el nombre del jugador es obligatorio");
                return false;
            }
            gestor.procesarRegistro(nombreJugador);
            return true;
        } catch (IllegalArgumentException e) {
            vista.mostrarMensaje(e.getMessage());
            return false;
        }
    }
    public void registrarJugador(String nombreJugador,String avatar){
        if (nombreJugador != null && !nombreJugador.trim().isEmpty()) {
            System.out.println("Controlador: Registrando a " + nombreJugador + " con el " + avatar);
            MensajeRegistroDTO msjRegistro = new MensajeRegistroDTO();
            msjRegistro.setNombre(nombreJugador);
            msjRegistro.setNombreAvatar(avatar);
            msjRegistro.setTipo("REGISTRO_JUGADOR");
            
            clienteProxy.enviarMensaje(msjRegistro);
        }
    }
    public void iniciarPartida() {
        try {
            gestor.iniciarPartida();
        } catch (Exception e) {
            vista.mostrarMensaje("error al iniciar partida: " + e.getMessage());
        }
    }

    public ServerController obtenerGestor() {
        return gestor;
    }

    public void procesarEventoRed(MensajeDTO mensaje) {
        if ("PARTIDA_INICIADA".equals(mensaje)) {
            System.out.println("la partida va a comenzar cambiando de pantalla..");
            vista.cerrarVista();
            GameView vistaJuego = new GameView();
            GameController controladorJuego = new GameController(gestor, vista, new ArrayList<>());
            controladorJuego.procesarEventoRed(mensaje);
        } else if ("LISTA_ACTUALIZADA".equals(mensaje)) {
            MensajeListaJugadoresDTO listaDTO = (MensajeListaJugadoresDTO) mensaje;

            SwingUtilities.invokeLater(() -> {
                System.out.println("Actualizando pantalla con los nuevos jugadores...");
            });

        }
    }
}
