package servidor.observador;

import Entidades.Logica.Partida;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import Observer.IObserver;
import dtos.MensajeDTO;
import dtos.PartidaDTO;



/**
 * Unico responsable de difundir el estado de una partida a sus jugadores.
 *
 * Esta atado a una sala: antes difundia a todas las sesiones del servidor, asi
 * que quien estuviera en otra sala recibia el tablero ajeno.
 */
public class ObservadorPartidaRed implements IObserver {
    private final Partida partidaObservada;
    private final ManejadorNodos manejadorNodos;
    private final String codigoSala;

    public ObservadorPartidaRed(Partida partidaObservada, ManejadorNodos manejadorNodos, String codigoSala) {
        this.partidaObservada = partidaObservada;
        this.manejadorNodos = manejadorNodos;
        this.codigoSala = codigoSala;
    }

    @Override
    public void actualizar(String evento) {
        System.out.println("[ObservadorRed] Cambio el tablero de la sala " + codigoSala + ".");
        PartidaDTO estadoActual = PartidaMapper.toDTO(this.partidaObservada);

        MensajeDTO mensaje = new MensajeDTO();
        if (evento != null && evento.startsWith("PARTIDA_FINALIZADA")) {
            mensaje.setTipo("PARTIDA_FINALIZADA");
            String ganador = evento.contains(":") ? evento.substring(evento.indexOf(":") + 1) : "";
            mensaje.getDatos().put("ganador", ganador);
        } else {
            mensaje.setTipo("ACTUALIZACION_MESA");
        }
        mensaje.setRemitente("SERVIDOR");
        mensaje.getDatos().put("partida", estadoActual);

        manejadorNodos.notificarASala(codigoSala, mensaje);
    }
}
