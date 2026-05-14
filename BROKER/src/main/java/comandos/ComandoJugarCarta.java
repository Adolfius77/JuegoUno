package comandos;

import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.CartaDTO;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import red.JuegoServidor;

import java.util.HashMap;
import java.util.Map;

public class ComandoJugarCarta implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final JuegoServidor juegoServidor;

    public ComandoJugarCarta(ManejadorNodos manejadorNodos, JuegoServidor juegoServidor) {
        this.manejadorNodos = manejadorNodos;
        this.juegoServidor = juegoServidor;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        String nombreJugador = mensaje.getRemitente();
        CartaDTO carta = (CartaDTO) mensaje.getDatos().get("carta");
        String colorElegido = mensaje.getDatos().get("colorElegido") != null ? String.valueOf(mensaje.getDatos().get("colorElegido")) : null;
        IProxy proxy = (IProxy) mensaje.getDatos().get("proxy");
        NodoCliente nodo = resolverNodo(nombreJugador, proxy);

        try {
            juegoServidor.jugarCarta(nombreJugador, carta, colorElegido);
        } catch (Exception e) {
            enviarError(nodo, proxy, "ERROR_JUGAR_CARTA", e.getMessage());
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
        datos.put("motivo", motivo != null ? motivo : "No se pudo completar la jugada.");
        error.setDatos(datos);

        if (nodo != null) {
            nodo.enviarMensaje(error);
        } else if (proxy != null) {
            proxy.enviarMensaje(error);
        }
    }
}

