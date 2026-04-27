package controlador.Factorys;


import controlador.GameController;
import controlador.LobbyController;
import java.util.List;

import red.GestorPartida;
import vista.GameView;
import vista.LobbyView;

public final class MVCFactory {

    private MVCFactory() {
    }

    public static LobbyController construirLobby() {
        GestorPartida gestor = new GestorPartida();

        LobbyView vistaLobby = new LobbyView();
        vistaLobby.setModelo(gestor.obtenerLobby());

        return new LobbyController(vistaLobby, gestor);
    }

    public static GameController construirJuego(GestorPartida gestor, List<String> nombreJugadores) {
        if (gestor == null) {
            throw new IllegalArgumentException("GestorPartida es obligatorio");
        }

        GameView vistaJuego = new GameView();
        if (gestor.obtenerPartida() != null) {
            vistaJuego.setModelo(gestor.obtenerPartida());
            gestor.obtenerPartida().agregarObservador(vistaJuego);
        }

        return new GameController(gestor,vistaJuego, nombreJugadores);
    }
}
