package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import vista.GameView;

public class GameController {

    private ClienteProxy proxy;
    private final IVista vista;
    private final List<String> nombreJugadores;
    private final String miNombre;

    public GameController(ClienteProxy proxy, IVista vista, List<String> nombreJugadores) {
        if (proxy == null || vista == null) {
            throw new IllegalArgumentException("proxy y vista son obligatorios");
        }

        this.proxy = proxy;
        this.vista = vista;
        this.nombreJugadores = nombreJugadores == null ? new ArrayList<>() : new ArrayList<>(nombreJugadores);

        this.miNombre = this.nombreJugadores.isEmpty() ? "" : this.nombreJugadores.get(0);

        this.proxy.setReceptor(mensaje -> {
            procesarEventoRed(mensaje);
        });
    }

    public void procesarEventoRed(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }

        String tipoMensaje = mensaje.getTipo();

        if ("PARTIDA_INICIADA".equals(tipoMensaje) || "ACTUALIZACION_MESA".equals(tipoMensaje)) {

            PartidaDTO estadoPartida = (PartidaDTO) mensaje.getDatos().get("partida");

            if (estadoPartida != null && vista instanceof GameView) {

                SwingUtilities.invokeLater(() -> {
                    GameView gameView = (GameView) vista;
                    List<CartaDTO> miMano = null;

                    for (JugadorDTO j : estadoPartida.getJugadores()) {
                        if (j.getNombre().equals(miNombre)) {
                            miMano = j.getMano().getCartas();
                            break;
                        }
                    }

                    if (miMano != null) {
                        gameView.mostrarCartas(miMano);

                        if (estadoPartida.getCartaCentro() != null) {
                            gameView.actualizar(estadoPartida.getCartaCentro().getColor());
                        }
                        gameView.actualizarMazo(estadoPartida.getCartasRestantesMazo());
                        gameView.mostrarVista();
                    }
                });
            }
        }
    }

    public void jugarCarta(CartaDTO carta) {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_JUGAR_CARTA");
        peticion.setRemitente(miNombre);
        peticion.getDatos().put("carta", carta);

        System.out.println("Enviando carta al servidor...");
        proxy.enviarMensaje(peticion);
    }

    public void tomarCarta() {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_TOMAR_CARTA");
        peticion.setRemitente(miNombre);

        System.out.println("Pidiendo carta al servidor...");
        proxy.enviarMensaje(peticion);
    }

    public void decirUno() {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_GRITAR_UNO");
        peticion.setRemitente(miNombre);
        proxy.enviarMensaje(peticion);
    }

    public void pasarTurno() {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_PASAR_TURNO");
        peticion.setRemitente(miNombre);
        proxy.enviarMensaje(peticion);
    }

    public List<String> getNombreJugadores() {
        return Collections.unmodifiableList(nombreJugadores);
    }
}
