package comandos;

import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;

import java.util.HashMap;
import java.util.Map;

/**
 * Confirma la identidad de una sesion ya conectada.
 *
 * Antes el nodo se buscaba comparando el IProxy que venia dentro del mensaje y
 * el nombre era la clave del registro, asi que renombrar implicaba sacar y
 * reinsertar: dos clientes que compartian entrada terminaban siendo uno solo.
 * Ahora el nodo se localiza por idSesion y el nombre es solo un atributo.
 *
 * @author emiim
 */
public class ComandoRegistrarJugador implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;

    public ComandoRegistrarJugador(ManejadorNodos manejadorNodos) {
        this.manejadorNodos = manejadorNodos;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        String idSesion = mensaje.getIdSesion();
        String nombreJugador = (String) mensaje.getDatos().get("nombre");
        String nombreAvatar = (String) mensaje.getDatos().get("avatar");

        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            return;
        }

        NodoCliente nodo = manejadorNodos.obtenerNodoPorSesion(idSesion);
        if (nodo == null) {
            System.out.println("[COMANDO-REGISTRAR] Sesion desconocida: " + idSesion);
            return;
        }

        if (manejadorNodos.nombreEnUsoPorOtro(nombreJugador, idSesion)) {
            System.out.println("[COMANDO-REGISTRAR] El nombre " + nombreJugador + " ya esta en uso.");

            MensajeDTO error = new MensajeDTO();
            error.setTipo("ERROR_REGISTRO");
            error.setRemitente("SERVIDOR");

            Map<String, Object> datosError = new HashMap<>();
            datosError.put("motivo", "Ese nombre ya está en uso. ¡Elige otro!");
            error.setDatos(datosError);

            manejadorNodos.enviarNodo(idSesion, error);
            return;
        }

        nodo.setNombre(nombreJugador.trim());
        if (nombreAvatar != null && !nombreAvatar.trim().isEmpty()) {
            nodo.setAvatar(nombreAvatar);
        }
        nodo.setEstaListo(false);

        System.out.println("ComandoRegistrarJugador: Jugador " + nombreJugador
                + " registrado en la sesion " + idSesion);

        MensajeDTO respuestaRegistro = new MensajeDTO();
        respuestaRegistro.setTipo("REGISTRO_EXITOSO");
        respuestaRegistro.setRemitente("SERVIDOR");
        Map<String, Object> datosRespuesta = new HashMap<>();
        datosRespuesta.put("nombre", nombreJugador);
        respuestaRegistro.setDatos(datosRespuesta);

        manejadorNodos.enviarNodo(idSesion, respuestaRegistro);
    }
}
