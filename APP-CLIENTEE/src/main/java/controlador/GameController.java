package controlador;

import Entidades.Jugador;
import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import vista.GameView;

public class GameController {

    private ClienteProxy proxy;
    private final IVista vista;
    private final List<String> nombreJugadores;

    public GameController(ClienteProxy proxy, IVista vista, List<String> nombreJugadores) {
        if (proxy == null || vista == null) {
            throw new IllegalArgumentException("proxy y vista son obligatorios");
        }
        this.proxy = proxy;
        this.proxy.setReceptor(mensaje ->{
            procesarEventoRed(mensaje);
        });
        this.vista = vista;
        this.nombreJugadores = nombreJugadores == null
                ? new ArrayList<>()
                : new ArrayList<>(nombreJugadores);

    }
    public void procesarEventoRed(MensajeDTO mensaje){
//        if(mensaje == null){
//            return;
//        }
//        String tipoMensaje = mensaje.getTipo();
//        if("PARTIDA_INICIADA".equals(mensaje)){
//            PartidaDTO estadoPartida = gestor.obtenerEstadoPartida();
//
//            if(estadoPartida != null && vista instanceof GameView){
//                GameView gameView = (GameView) vista;
//
//                String miNombre = nombreJugadores.isEmpty() ? "" : nombreJugadores.get(0);
//                List<CartaDTO> miMano = null;
//
//                for (JugadorDTO j : estadoPartida.getJugadores()) {
//                    if(j.getNombre().equals(miNombre)){
//                        miMano = j.getMano().getCartas();
//                        break;
//                    }
//                }
//                if(miMano != null){
//                    gameView.mostrarCartas(miMano);
//                    gameView.actualizar(estadoPartida.getColorActual());
//                    gameView.actualizarMazo(estadoPartida.getCartasRestantesMazo());
//                    gameView.mostrarVista();
//                }
//            }
//        }
    }

    public void jugarCarta(CartaDTO carta) {

    }

    public void tomarCarta() {
    }

    public void decirUno() {

    }

    public void pasarTurno() {

    }



    public List<String> getNombreJugadores() {
        return Collections.unmodifiableList(nombreJugadores);
    }

    private boolean esMismoJugador(Jugador jugadorA, Jugador jugadorB) {
        if (jugadorA == null || jugadorB == null) {
            return false;
        }
        if (jugadorA.equals(jugadorB)) {
            return true;
        }
        String idA = jugadorA.getId();
        String idB = jugadorB.getId();
        return idA != null && idA.equals(idB);
    }

}
