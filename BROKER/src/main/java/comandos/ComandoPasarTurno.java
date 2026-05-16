package comandos;

import Entidades.Jugador;
import Entidades.Logica.Partida;
import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import enums.TipoMensaje;
import static enums.TipoMensaje.ERROR_PASAR_TURNO;
import interfaces.IComandoServidor;
import red.JuegoServidor;

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
        IProxy proxy = (IProxy) mensaje.getDatos().get("proxy");
        NodoCliente nodo = resolverNodo(nombreJugador, proxy);

        try {
            Partida partida = juegoServidor.validarPartidaActiva();
            Jugador jugador = juegoServidor.obtenerJugador(nombreJugador);
            juegoServidor.validarTurno(jugador);
            partida.pasarTurno();
            
        } catch (Exception e) {
            enviarError(nodo, proxy, "ERROR_PASAR_TURNO", e.getMessage());
        }
    }

    private NodoCliente resolverNodo(String nombreJugador, IProxy proxy) {
        NodoCliente nodo = null;
        if (proxy != null) {
            nodo = manejadorNodos.obtenerNodoPorProxy(proxy);
        }
        if (nodo == null) {
            nodo = manejadorNodos.obtenerNodoPorNombre(nombreJugador);
        }
        return nodo;
    }

    private void enviarError(NodoCliente nodo, IProxy proxy, String tipo, String motivo) {
        MensajeDTO error = new MensajeDTO();
        error.setTipo(tipo);
        error.setRemitente("SERVIDOR");
        Map<String, Object> datos = new HashMap<>();
        datos.put("motivo", motivo != null ? motivo : "No se pudo pasar el turno.");
        error.setDatos(datos);

        if (nodo != null) {
            nodo.enviarMensaje(error);
        } else if (proxy != null) {
            proxy.enviarMensaje(error);
        }
    }
}

