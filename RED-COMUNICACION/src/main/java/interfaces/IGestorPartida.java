package interfaces;

import dtos.CartaDTO;
import dtos.PartidaDTO;

import java.util.List;

public interface IGestorPartida {
    void procesarRegistro(String mensaje);
    void iniciarPartida();
    void jugarCarta(CartaDTO carta);
    void tomarCarta();
    void pasarTurno();
    void decirUno();
    void actualizarEstadoPartida(PartidaDTO nuevoEstado);
    void actualizarLobby(List<String> nuevaLista);
    PartidaDTO  obtenerEstadoPartida();
    List<String> obtenerJugadoresRegistrados();
    boolean esPartidaActiva();
    void limpiarLobby();
}
