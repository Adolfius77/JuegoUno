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

public class ComandoTomarCarta implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final JuegoServidor juegoServidor;

    public ComandoTomarCarta(ManejadorNodos manejadorNodos, JuegoServidor juegoServidor) {
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
            // La sala se toma del nodo del remitente, no de los datos del
            // mensaje: el cliente puede mandarla vacia o equivocada.
            String codigoSala = salaDe(nodo);
            Partida partida = juegoServidor.validarPartidaActiva(codigoSala);
            Jugador jugador = juegoServidor.obtenerJugador(codigoSala, nombreJugador);
            juegoServidor.validarTurno(codigoSala, jugador);
            partida.tomarCarta(jugador);
            
        } catch (Exception e) {
            enviarError(nodo, "ERROR_TOMAR_CARTA", e.getMessage());
        }
    }

    private String salaDe(NodoCliente nodo) {
        if (nodo == null || nodo.getCodigoSala() == null) {
            throw new IllegalStateException("El jugador no esta en ninguna sala.");
        }
        return nodo.getCodigoSala();
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
        datos.put("motivo", motivo != null ? motivo : "No se pudo robar una carta.");
        error.setDatos(datos);

        if (nodo != null) {
            nodo.enviarMensaje(error);
        }
    }
}

