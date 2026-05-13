package observador;

import Entidades.Logica.Partida;
import Mappers.PartidaMapper;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import Observer.IObserver;
import dtos.MensajeDTO;
import dtos.PartidaDTO;

public class ObservadorPartidaRed implements IObserver {
    private final Partida partidaObservada;
    private final ManejadorNodos manejadorNodos;

    public ObservadorPartidaRed(Partida partidaObservada, ManejadorNodos manejadorNodos) {
        this.partidaObservada = partidaObservada;
        this.manejadorNodos = manejadorNodos;
    }

    @Override
    public void actualizar(String evento) {
        System.out.println("[ObservadorRed] El tablero cambiO. Enviando actualizacion a todos...");
        PartidaDTO estadoActual = PartidaMapper.toDTO(this.partidaObservada);

        MensajeDTO mensaje = new MensajeDTO();
        mensaje.setTipo("ACTUALIZACION_TABLERO");
        mensaje.setRemitente("SERVIDOR");
        mensaje.getDatos().put("partida", estadoActual);

        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(mensaje);

        }
    }
}
