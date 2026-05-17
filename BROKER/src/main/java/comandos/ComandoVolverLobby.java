package comandos;

import Entidades.Logica.Partida;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import red.JuegoServidor;

public class ComandoVolverLobby implements IComandoServidor {

    private final JuegoServidor juegoServidor;

    public ComandoVolverLobby(JuegoServidor juegoServidor) {
        this.juegoServidor = juegoServidor;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null) return;
        try {
       
            Partida partida = juegoServidor.validarPartidaActiva();
            partida.resetearALobby();
        } catch (Exception e) {
            System.out.println("[SERVER] Error al regresar al lobby: " + e.getMessage());
        }
    }
}