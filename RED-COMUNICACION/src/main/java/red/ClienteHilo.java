package red;

import Controladores.ServerController;
import dtos.MensajeEstadoPartidaDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ClienteHilo extends Thread {
    private ObjectInputStream in;
    private ServerController gestor;
    private boolean escuchando = true;

    public ClienteHilo(ObjectInputStream in, ServerController gestor) {
        this.in = in;
        this.gestor = gestor;
    }

    @Override
    public void run() {
        try {
            while (escuchando) {
                Object objeto = in.readObject();

                if (objeto instanceof MensajeListaJugadoresDTO) {

                    gestor.actualizarLobby(((MensajeListaJugadoresDTO) objeto).getJugadores());
                }
                else if (objeto instanceof MensajeEstadoPartidaDTO) {
                    MensajeEstadoPartidaDTO msg = (MensajeEstadoPartidaDTO) objeto;
                    gestor.actualizarEstadoPartida(msg.getPartida());
                    System.out.println("[cliente-hilo] Partida recibida. ¡A jugar!");

                }
                else if (objeto instanceof MensajeNotificacionDTO) {
                    MensajeNotificacionDTO notif = (MensajeNotificacionDTO) objeto;
                    System.out.println("[cliente-hilo] Notificacion: " + notif.getTextoMensaje());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[cliente-hilo] Conexion con el servidor perdida.");
            escuchando = false;
        }
    }
}