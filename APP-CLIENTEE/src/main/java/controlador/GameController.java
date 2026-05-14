package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import com.google.gson.Gson;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import vista.GameView;

public class GameController {

    private ClienteProxy proxy;
    private final IVista vista;
    private final String miNombre;
    private PartidaDTO estadoPartida;

    public GameController(ClienteProxy proxy, IVista vista, String miNombre) {
        if (proxy == null || vista == null) {
            throw new IllegalArgumentException("proxy y vista son obligatorios");
        }

        this.proxy = proxy;
        this.vista = vista;
        this.miNombre = miNombre != null ? miNombre : "";
        configurarReceptorRed();
    }

    private void configurarReceptorRed() {
        this.proxy.setReceptor(mensaje -> {
            procesarEventoRed(mensaje);
        });
    }
    //getters 

    public String getMiNombre() {
        return this.miNombre;
    }

    public PartidaDTO getEstadoPartida() {
        return this.estadoPartida;
    }

    public List<CartaDTO> getMiMano() {
        if (estadoPartida == null || estadoPartida.getJugadores() == null) {
            return null;
        }
        for (JugadorDTO j : estadoPartida.getJugadores()) {
            if (j.getNombre().equals(miNombre)) {
                return j.getMano().getCartas();
            }
        }
        return null;
    }

    public CartaDTO getCartaCentro() {
        return estadoPartida != null ? estadoPartida.getCartaCentro() : null;
    }

    public int getCartasRestantesMazo() {
        return estadoPartida != null ? estadoPartida.getCartasRestantesMazo() : 0;
    }

    public void procesarEventoRed(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }

        String tipoMensaje = mensaje.getTipo();

        if ("PARTIDA_INICIADA".equals(tipoMensaje) || "ACTUALIZACION_MESA".equals(tipoMensaje)) {

            Object objetoCrudo = mensaje.getDatos().get("partida");

            Gson gson = new com.google.gson.Gson();
            String jsonTemporal = gson.toJson(objetoCrudo);
            PartidaDTO nuevaPartida = gson.fromJson(jsonTemporal, PartidaDTO.class);

            if (nuevaPartida != null) {
                this.estadoPartida = nuevaPartida;
                SwingUtilities.invokeLater(() -> {
                    if (vista != null) {
                        vista.actualizar("ACTUALIZAR_TABLERO");
                    }
                });
            }
        }
    }

    public void jugarCarta(CartaDTO carta) {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_JUGAR_CARTA");
        peticion.setRemitente(miNombre);
        peticion.getDatos().put("carta", carta);

        System.out.println("Enviando carta al servidor...");
        proxy.enviarMensaje(peticion);
    }

    public void tomarCarta() {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_TOMAR_CARTA");
        peticion.setRemitente(miNombre);

        System.out.println("Pidiendo carta al servidor...");
        proxy.enviarMensaje(peticion);
    }

    public void decirUno() {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_GRITAR_UNO");
        peticion.setRemitente(miNombre);
        proxy.enviarMensaje(peticion);
    }

    public void pasarTurno() {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_PASAR_TURNO");
        peticion.setRemitente(miNombre);
        proxy.enviarMensaje(peticion);
    }
}
