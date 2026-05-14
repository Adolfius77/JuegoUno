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

/**
 *
 * @author emiim
 */
public class ComandoRegistrarJugador implements IComandoServidor {

    private final ManejadorNodos ManejadorNodos;

    public ComandoRegistrarJugador(ManejadorNodos ManejadorNodos) {
        this.ManejadorNodos = ManejadorNodos;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        System.out.println("[COMANDO-REGISTRAR] el broker recibio el msj");
        String nombreJugador = (String) mensaje.getDatos().get("nombre");
        String nombreAvatar = (String) mensaje.getDatos().get("avatar");
        IProxy proxy = (IProxy) mensaje.getDatos().get("proxy");

        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            return;
        }

        if (proxy == null) {
            return;
        }

        NodoCliente nodoTemporal = null;
        List<NodoCliente> nodos = ManejadorNodos.obtenerNodosConectados();

        for (NodoCliente nodo : nodos) {
            if (nodo.getProxy() == proxy) {
                nodoTemporal = nodo;
                break;
            }
        }

        if (nodoTemporal == null) {
            return;
        }

        String nombreTemporal = nodoTemporal.getNombre();
        ManejadorNodos.actualizarIdentidadNodo(nombreTemporal, nombreJugador);

        if (nombreAvatar != null && !nombreAvatar.trim().isEmpty()) {
            nodoTemporal.setAvatar(nombreAvatar);
        }
        nodoTemporal.setEstaListo(false);

        System.out.println("ComandoRegistrarJugador: Jugador " + nombreJugador + " registrado exitosamente");

        MensajeDTO respuestaRegistro = new MensajeDTO();
        respuestaRegistro.setTipo("REGISTRO_EXITOSO");
        respuestaRegistro.setRemitente("SERVIDOR");
        Map<String, Object> datosRespuesta = new HashMap<>();
        datosRespuesta.put("nombre", nombreJugador);
        respuestaRegistro.setDatos(datosRespuesta);
        proxy.enviarMensaje(respuestaRegistro);

//        List<Map<String, String>> listaJugadores = new ArrayList<>();
//
//        for (NodoCliente nodo : ManejadorNodos.obtenerNodosConectados()) {
//            Map<String, String> datosJugador = new HashMap<>();
//            datosJugador.put("nombre", nodo.getNombre());
//
//            if (nodo.getAvatar() != null) {
//                datosJugador.put("avatar", nodo.getAvatar());
//
//            } else {
//                datosJugador.put("avatar", "pfp");
//            }
//            datosJugador.put("estaListo", String.valueOf(nodo.isEstaListo()));
//            listaJugadores.add(datosJugador);
//        }
//        MensajeDTO notificacionLista = new MensajeDTO();
//        notificacionLista.setTipo("LISTA_ACTUALIZADA");
//        notificacionLista.setRemitente("SERVIDOR");
//        Map<String, Object> datosLista = new HashMap<>();
//        datosLista.put("jugadores", listaJugadores);
//        notificacionLista.setDatos(datosLista);
//
//        for (NodoCliente nodo : ManejadorNodos.obtenerNodosConectados()) {
//            nodo.enviarMensaje(notificacionLista);
//        }
    }
}
