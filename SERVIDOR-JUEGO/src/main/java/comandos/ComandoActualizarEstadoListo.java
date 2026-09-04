package comandos;

import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComandoActualizarEstadoListo implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;

    public ComandoActualizarEstadoListo(ManejadorNodos manejadorNodos) {
        this.manejadorNodos = manejadorNodos;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        IProxy proxy = (IProxy) mensaje.getDatos().get("proxy");
        if (proxy == null) {
            return;
        }

        NodoCliente nodo = manejadorNodos.obtenerNodoPorProxy(proxy);
        if (nodo == null) {
            return;
        }
        Object valorRecibido = mensaje.getDatos().get("estaListo");
        boolean estaListo = false;
        
        if(valorRecibido != null){
            estaListo = Boolean.parseBoolean(valorRecibido.toString().trim());
        }
        nodo.setEstaListo(estaListo);
        System.out.println("jugador: " + nodo.getNombre() + " Listo: " + estaListo);
        
        MensajeDTO notificacionLista = new MensajeDTO();
        notificacionLista.setTipo("LISTA_ACTUALIZADA");
        notificacionLista.setRemitente("SERVIDOR");
        Map<String, Object> datosLista = new HashMap<>();
        datosLista.put("jugadores", construirListaJugadores());
        notificacionLista.setDatos(datosLista);

        for (NodoCliente jugador : manejadorNodos.obtenerNodosConectados()) {
            jugador.enviarMensaje(notificacionLista);
        }
    }

    private boolean obtenerEstadoListo(Object estado) {
        if (estado instanceof Boolean valor) {
            return valor;
        }
        if (estado instanceof String texto) {
            return Boolean.parseBoolean(texto.trim());
        }
        return false;
    }

    private List<Map<String, String>> construirListaJugadores() {
        List<Map<String, String>> lista = new ArrayList<>();
        for (NodoCliente n : manejadorNodos.obtenerNodosConectados()) {
            Map<String, String> jugador = new HashMap<>();
            jugador.put("nombre", n.getNombre());
            jugador.put("avatar", (n.getAvatar() != null && !n.getAvatar().equals("no hay")) ? n.getAvatar() : "pfp");
            jugador.put("estaListo", String.valueOf(n.isEstaListo()));
            lista.add(jugador);
        }
        return lista;
    }
}
