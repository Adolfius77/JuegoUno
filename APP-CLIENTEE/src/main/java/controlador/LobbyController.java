package controlador;

import Interfaces.IVista;
import java.util.ArrayList;
import Controladores.ServerController;
import vista.GameView;

public class LobbyController {

    private final IVista vista;
    private final ServerController gestor;

    public LobbyController(IVista vista, ServerController gestor) {
        if (vista == null || gestor == null) {
            throw new IllegalArgumentException("vista y gestor son obligatorios");
        }
        this.vista = vista;
        this.gestor = gestor;
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
    public void procesarEventoRed(String evento){
        if("PARTIDA_INICIADA".equals(evento)){
            System.out.println("la partida va a comenzar cambiando de pantalla..");
            vista.cerrarVista();
            GameView vistaJuego = new GameView();
            GameController controladorJuego = new GameController(gestor, vista, new ArrayList<>());
            controladorJuego.procesarEventoRed(evento);
        }
    }
}
