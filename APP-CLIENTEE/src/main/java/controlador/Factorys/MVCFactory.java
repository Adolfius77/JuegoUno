package controlador.Factorys;

import cliente.ClienteProxy;
import controlador.CrearPartidaController;
import controlador.GameController;
import controlador.LobbyController;
import controlador.UnirsePartidaController;
import dtos.MensajeDTO;
import vista.CrearPartida;
import vista.GameView;
import vista.LobbyView;
import vista.MenuPrincipal;
import vista.SeleccionPartida;
import vista.unirsePartidaView;

import java.util.List;

/**
 * Punto unico de composicion del cliente: arma cada pareja vista + controlador y
 * abre la pantalla correspondiente.
 *
 * Antes cada vista instanciaba su propio controlador y se pasaba el ClienteProxy
 * de pantalla en pantalla, asi que la capa de presentacion conocia la
 * infraestructura de red. Ahora el proxy se configura una sola vez al arrancar y
 * las vistas solo piden "abre la siguiente pantalla".
 */
public final class MVCFactory {

    private static ClienteProxy proxy;

    private MVCFactory() {
    }

    /** Lo llama el arranque del cliente (Launcher) antes de abrir nada. */
    public static void configurar(ClienteProxy clienteProxy) {
        proxy = clienteProxy;
    }

    private static ClienteProxy proxy() {
        if (proxy == null) {
            throw new IllegalStateException(
                    "MVCFactory sin configurar: llama a MVCFactory.configurar(proxy) al arrancar el cliente.");
        }
        return proxy;
    }

    public static MenuPrincipal abrirMenuPrincipal() {
        LobbyController controlador = new LobbyController(proxy(), "", "", false, null);
        MenuPrincipal menu = new MenuPrincipal(controlador);
        controlador.setVista(menu);
        menu.setVisible(true);
        return menu;
    }

    public static SeleccionPartida abrirSeleccionPartida(String nombreUsuario, String avatarUsuario) {
        SeleccionPartida seleccion = new SeleccionPartida(nombreUsuario, avatarUsuario);
        seleccion.setVisible(true);
        return seleccion;
    }

    public static CrearPartida abrirCrearPartida(String nombreHost, String avatarHost) {
        CrearPartida vista = new CrearPartida(nombreHost, avatarHost);
        vista.setVisible(true);
        return vista;
    }

    public static unirsePartidaView abrirUnirsePartida(String nombreInvitado) {
        unirsePartidaView vista = new unirsePartidaView(nombreInvitado);
        vista.setVisible(true);
        return vista;
    }

    public static LobbyView abrirLobby(String codigoSala, String nombreJugador, boolean esHost,
                                       List<?> jugadoresIniciales) {
        LobbyView lobby = new LobbyView();
        LobbyController controlador = new LobbyController(proxy(), codigoSala, nombreJugador, esHost, lobby);
        controlador.cargarDatosIniciales(jugadoresIniciales);
        lobby.setVisible(true);
        return lobby;
    }

    /**
     * @param mensajeInicial PARTIDA_INICIADA ya consumido por el lobby, que se
     *                       reinyecta porque llego antes de que existiera este
     *                       controlador.
     */
    public static GameView abrirJuego(String nombreJugador, MensajeDTO mensajeInicial) {
        GameView vistaJuego = new GameView();
        GameController controlador = new GameController(proxy(), vistaJuego, nombreJugador);
        vistaJuego.setController(controlador);
        if (mensajeInicial != null) {
            controlador.procesarEventoRed(mensajeInicial);
        }
        vistaJuego.setVisible(true);
        return vistaJuego;
    }

    // --- Controladores de pantallas que se construyen a si mismas -----------

    public static CrearPartidaController controladorDe(CrearPartida vista) {
        return new CrearPartidaController(vista, proxy());
    }

    public static UnirsePartidaController controladorDe(unirsePartidaView vista) {
        return new UnirsePartidaController(vista, proxy());
    }
}
