package comandos;

import Entidades.Jugador;
import Entidades.Logica.Partida;
import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import red.JuegoServidor;

import java.util.HashMap;
import java.util.Map;

public class ComandoGritarUno implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final JuegoServidor juegoServidor;

    public ComandoGritarUno(ManejadorNodos manejadorNodos, JuegoServidor juegoServidor) {
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

            if (jugador.getMano() == null || jugador.getMano().getCartas().size() != 1) {
                throw new IllegalStateException("El jugador no tiene 1 sola carta. UNO inválido.");
            }
            jugador.setDijoUno(true);
            partida.notificarObservador("UNO_GRITADO" +":" + nombreJugador);

        } catch (Exception e) {
            enviarError(nodo, proxy, "ERROR_GRITAR_UNO", e.getMessage());
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
        datos.put("motivo", motivo != null ? motivo : "No se pudo registrar el UNO.");
        error.setDatos(datos);

        if (nodo != null) {
            nodo.enviarMensaje(error);
        } else if (proxy != null) {
            proxy.enviarMensaje(error);
        }
    }
}
