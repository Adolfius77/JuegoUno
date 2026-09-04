package comandos;

import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.enums.Color; 
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import com.google.gson.Gson;
import dtos.CartaDTO;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import servidor.JuegoServidor;

import java.util.HashMap;
import java.util.Map;

public class ComandoJugarCarta implements IComandoServidor {

    private final Gson gson = new Gson();
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
        String idSesion = mensaje.getIdSesion();
        NodoCliente nodo = resolverNodo(nombreJugador, idSesion);

        try {
            // La sala se toma del nodo del remitente, no de los datos del
            // mensaje: el cliente puede mandarla vacia o equivocada.
            String codigoSala = salaDe(nodo);
           
            CartaDTO cartaDTO = convertirCartaDTO(mensaje.getDatos().get("carta"));
            String colorElegido = mensaje.getDatos().get("colorElegido") != null
                    ? String.valueOf(mensaje.getDatos().get("colorElegido"))
                    : null;

            
            Partida partida = juegoServidor.validarPartidaActiva(codigoSala);
            Jugador jugador = juegoServidor.obtenerJugador(codigoSala, nombreJugador);

          
            juegoServidor.validarTurno(codigoSala, jugador);

            Carta carta = juegoServidor.buscarCartaEnMano(jugador, cartaDTO);
            if (carta == null) {
                throw new IllegalArgumentException("La carta seleccionada no está en la mano del jugador.");
            }

          
            Carta cartaMesa = partida.getPilaCartas() != null && !partida.getPilaCartas().getListaCartas().isEmpty()
                    ? partida.getPilaCartas().obtenerUltimaCarta()
                    : null;

            if (cartaMesa != null && !carta.esJugable(cartaMesa)) {
                throw new IllegalStateException("La carta no es jugable sobre la mesa actual.");
            }

            if (colorElegido != null && !colorElegido.isBlank()) {
                Color colorAplicado = juegoServidor.colorDesdeTexto(colorElegido);
                if (colorAplicado != null) {
                    carta.setColor(colorAplicado);
                }
            }

            // La difusion del nuevo estado la hace ObservadorPartidaRed, que ya
            // esta suscrito a la Partida. Antes este comando volvia a difundir
            // por su cuenta, asi que cada jugada mandaba el tablero tres veces:
            // CARTA_JUGADA y TURNO_CAMBIADO desde el dominio, mas esta.
            partida.jugarCarta(carta, jugador);

        } catch (Exception e) {
            enviarError(nodo, "ERROR_GENERAL", e.getMessage());
        }
    }

    private CartaDTO convertirCartaDTO(Object cartaCruda) {
        if (cartaCruda == null) {
            return null;
        }
        if (cartaCruda instanceof CartaDTO) {
            return (CartaDTO) cartaCruda;
        }
        String json = gson.toJson(cartaCruda);
        return gson.fromJson(json, CartaDTO.class);
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
        datos.put("motivo", motivo != null ? motivo : "No se pudo completar la jugada.");
        error.setDatos(datos);

        if (nodo != null) {
            nodo.enviarMensaje(error);
        }
    }
}
