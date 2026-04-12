package controlador.Factorys;


import modelo.Entidades.Jugador;
import modelo.Entidades.Lobby;
import modelo.Entidades.Logica.Partida;
import modelo.Entidades.fabricas.IMazoFactory;
import modelo.Entidades.fabricas.PartidaFactory;
import modelo.Entidades.fabricas.ICartaFactory;
import controlador.GameController;
import controlador.LobbyController;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import vista.GameView;
import vista.LobbyView;

public final class MVCFactory {

    private MVCFactory() {
    }

    public static LobbyController construirLobby() {
        Lobby modeloLobby = new Lobby();

        LobbyView vistaLobby = new LobbyView();
        vistaLobby.setModelo(modeloLobby);

        LobbyController controlador = new LobbyController(vistaLobby, modeloLobby);
        return controlador;
    }

    public static GameController construirJuego(List<String> nombreJugadores, ICartaFactory cartaFactory, IMazoFactory mazoFactory) {
        List<Jugador> jugadores = new ArrayList<>();
        for (String nombre : nombreJugadores) {
            jugadores.add(new Jugador(UUID.randomUUID().toString(), nombre));

        }

        Partida modeloJuego = PartidaFactory.crearPartida(jugadores, cartaFactory, mazoFactory);
        GameView vistaJuego = new GameView();
        vistaJuego.setModelo(modeloJuego);

        GameController controlador = new GameController(modeloJuego, vistaJuego, nombreJugadores);
        return controlador;
    }
}
