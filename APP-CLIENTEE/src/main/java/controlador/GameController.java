package controlador;

import Entidades.Jugador;
import Interfaces.IVista;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import interfaces.IGestorPartida;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import vista.GameView;

public class GameController {

    private final IGestorPartida gestor;
    private final IVista vista;
    private final List<String> nombreJugadores;

    public GameController(IGestorPartida gestor, IVista vista, List<String> nombreJugadores) {
        if (gestor == null || vista == null) {
            throw new IllegalArgumentException("Gestor y vista son obligatorios");
        }
        this.gestor = gestor;
        this.vista = vista;
        this.nombreJugadores = nombreJugadores == null
                ? new ArrayList<>()
                : new ArrayList<>(nombreJugadores);
        
    }
    public void procesarEventoRed(MensajeDTO evento){
        if("PARTIDA_INICIADA".equals(evento)){
            PartidaDTO estadoPartida = gestor.obtenerEstadoPartida();
            
            if(estadoPartida != null && vista instanceof GameView){
                GameView gameView = (GameView) vista;
                
                String miNombre = nombreJugadores.isEmpty() ? "" : nombreJugadores.get(0);
                List<CartaDTO> miMano = null;
                
                for (JugadorDTO j : estadoPartida.getJugadores()) {
                    if(j.getNombre().equals(miNombre)){
                        miMano = j.getMano().getCartas();
                        break;
                    }
                }
                if(miMano != null){
                    gameView.mostrarCartas(miMano);
                    gameView.actualizar(estadoPartida.getColorActual());
                    gameView.actualizarMazo(estadoPartida.getCartasRestantesMazo());
                    gameView.mostrarVista();
                }
            }
        }
    }
    public void iniciarJuego() {
        try {
            gestor.iniciarPartida();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al iniciar juego: " + e.getMessage());
        }
    }

    public void jugarCarta(CartaDTO carta) {
        try {
            if(carta == null){
                vista.mostrarMensaje("debes seleccionar una carta para jugar");
                return;
            }
            gestor.jugarCarta(carta);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al jugar carta: " + e.getMessage());
        }
    }

    public void tomarCarta() {
        try {
            gestor.tomarCarta();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al tomar carta: " + e.getMessage());
        }
    }

    public void decirUno() {
        try {
            gestor.decirUno();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al decir UNO: " + e.getMessage());
        }
    }

    public void pasarTurno() {
        try {
            gestor.pasarTurno();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al pasar turno: " + e.getMessage());
        }
    }

    public void pasarrTurno() {
        pasarTurno();
    }

    public List<String> getNombreJugadores() {
        return Collections.unmodifiableList(nombreJugadores);
    }

    public IGestorPartida obtenerGestor() {
        return gestor;
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
