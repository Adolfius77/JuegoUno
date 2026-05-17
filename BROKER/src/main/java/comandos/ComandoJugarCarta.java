package comandos;

import Entidades.Carta;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.enums.Color; 
import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import com.google.gson.Gson;
import dtos.CartaDTO;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import red.JuegoServidor;

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
        IProxy proxy = (IProxy) mensaje.getDatos().get("proxy");
        NodoCliente nodo = resolverNodo(nombreJugador, proxy);

        try {
           
            CartaDTO cartaDTO = convertirCartaDTO(mensaje.getDatos().get("carta"));
            String colorElegido = mensaje.getDatos().get("colorElegido") != null
                    ? String.valueOf(mensaje.getDatos().get("colorElegido"))
                    : null;

            
            Partida partida = juegoServidor.getPartidaActualEntidad();
            Jugador jugador = juegoServidor.obtenerJugador(nombreJugador);

          
            juegoServidor.validarTurno(jugador);

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

            partida.jugarCarta(carta, jugador);

        String nombreGanador = juegoServidor.getFachadaJuego().verificarGanador();

            if (nombreGanador != null) {
                MensajeDTO mensajeVictoria = new MensajeDTO();
                mensajeVictoria.setTipo("PARTIDA_FINALIZADA");
                mensajeVictoria.setRemitente("SERVIDOR");
                mensajeVictoria.getDatos().put("ganador", nombreGanador);
                
                manejadorNodos.notificarATodos(mensajeVictoria); 

            } else {
                dtos.PartidaDTO partidaActualizada = Mappers.PartidaMapper.toDTO(partida); 
                
                MensajeDTO mensajeActualizacion = new MensajeDTO();
                mensajeActualizacion.setTipo("ACTUALIZACION_MESA");
                mensajeActualizacion.setRemitente("SERVIDOR");
                mensajeActualizacion.getDatos().put("partida", partidaActualizada);
                
                manejadorNodos.notificarATodos(mensajeActualizacion);
            }

        } catch (Exception e) {
            enviarError(nodo, proxy, "ERROR_GENERAL", e.getMessage());
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
