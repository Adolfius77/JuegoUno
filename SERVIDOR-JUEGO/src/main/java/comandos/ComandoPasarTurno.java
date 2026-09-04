package comandos;

import Entidades.Jugador;
import Entidades.Logica.Partida;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import servidor.JuegoServidor;

import java.util.HashMap;
import java.util.Map;

public class ComandoPasarTurno implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final JuegoServidor juegoServidor;

    public ComandoPasarTurno(ManejadorNodos manejadorNodos, JuegoServidor juegoServidor) {
        this.manejadorNodos = manejadorNodos;
        this.juegoServidor = juegoServidor;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }

        String nombreJugador = mensaje.getRemitente();
        String idSesion = mensaje.getIdSesion();
        NodoCliente nodo = resolverNodo(nombreJugador, idSesion);

        try {
            Partida partida = juegoServidor.validarPartidaActiva();
            Jugador jugador = juegoServidor.obtenerJugador(nombreJugador);
            juegoServidor.validarTurno(jugador);
            partida.pasarTurno();
            
        } catch (Exception e) {
            enviarError(nodo, "ERROR_PASAR_TURNO", e.getMessage());
        }
    }

    private NodoCliente resolverNodo(String nombreJugador, String idSesion) {
        NodoCliente nodo = manejadorNodos.obtenerNodoPorSesion(idSesion);
        if (nodo == null) {
            nodo = manejadorNodos.obtenerNodoPorNombre(nombreJugador);
        }
        return nodo;
    }

    private void enviarError(NodoCliente nodo, String tipo, String motivo) {
        MensajeDTO error = new MensajeDTO();
        error.setTipo(tipo);
        error.setRemitente("SERVIDOR");
        Map<String, Object> datos = new HashMap<>();
        datos.put("motivo", motivo != null ? motivo : "No se pudo pasar el turno.");
        error.setDatos(datos);

        if (nodo != null) {
            nodo.enviarMensaje(error);
        }
    }
}

