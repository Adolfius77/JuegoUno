package comandos;

import Entidades.Logica.Partida;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import servidor.JuegoServidor;

public class ComandoVolverLobby implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final JuegoServidor juegoServidor;

    public ComandoVolverLobby(ManejadorNodos manejadorNodos, JuegoServidor juegoServidor) {
        this.manejadorNodos = manejadorNodos;
        this.juegoServidor = juegoServidor;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null) return;
        try {
            NodoCliente nodo = manejadorNodos.obtenerNodoPorSesion(mensaje.getIdSesion());
            if (nodo == null || nodo.getCodigoSala() == null) {
                return;
            }
            Partida partida = juegoServidor.validarPartidaActiva(nodo.getCodigoSala());
            partida.resetearALobby();
        } catch (Exception e) {
            System.out.println("[SERVER] Error al regresar al lobby: " + e.getMessage());
        }
    }
}