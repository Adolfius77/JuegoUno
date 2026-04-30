package controlador.Factorys;

import Interfaces.IVista;
import controlador.GameController;
import controlador.LobbyController;
import red.GestorPartida;
import vista.GameView;
import vista.LobbyView;

import java.util.List;

public final class MVCFactory {

    private MVCFactory() {}

    public static LobbyController construirLobby() {
        GestorPartida gestor = new GestorPartida();
        LobbyView vistaLobby = new LobbyView();
        return new LobbyController(vistaLobby, gestor);
    }

    public static GameController construirJuego(GestorPartida gestor, List<String> nombreJugadores) {
        if (gestor == null) {
            throw new IllegalArgumentException("GestorPartida es obligatorio");
        }
        GameView vistaJuego = new GameView();
        return new GameController(gestor, (IVista) vistaJuego, nombreJugadores);
    }
}