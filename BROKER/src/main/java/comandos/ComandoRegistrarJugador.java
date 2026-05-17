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

        List<NodoCliente> nodos = ManejadorNodos.obtenerNodosConectados();

        for (NodoCliente nodo : nodos) {
            if (nodo.getNombre() != null && nodo.getNombre().equalsIgnoreCase(nombreJugador.trim())) {
                System.out.println("[COMANDO-REGISTRAR] Error: El nombre " + nombreJugador + " ya está en uso.");
                
                MensajeDTO error = new MensajeDTO();
                error.setTipo("ERROR_REGISTRO"); 
                error.setRemitente("SERVIDOR");
                
                Map<String, Object> datosError = new HashMap<>();
                datosError.put("motivo", "Ese nombre ya está en uso. ¡Elige otro!");
                error.setDatos(datosError);
                
                proxy.enviarMensaje(error);
                return;
            }
        }

        NodoCliente nodoTemporal = null;

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

    }
}