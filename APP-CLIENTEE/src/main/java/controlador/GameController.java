package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import com.google.gson.Gson;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import vista.podioView;

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
            return Collections.emptyList();
        }
        for (JugadorDTO j : estadoPartida.getJugadores()) {
            if (j != null && miNombre.equals(j.getNombre())) {
                return j.getMano() != null ? j.getMano().getCartas() : Collections.emptyList();
            }
        }
        return Collections.emptyList();
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

        if (tipoMensaje != null && tipoMensaje.startsWith("ERROR_")) {
            String motivo = "No se pudo completar la accion.";
            if (mensaje.getDatos() != null && mensaje.getDatos().get("motivo") != null) {
                motivo = String.valueOf(mensaje.getDatos().get("motivo"));
            }
            if (vista != null) {
                vista.mostrarMensaje(motivo);
            }
            return;
        }

        if ("PARTIDA_FINALIZADA".equals(tipoMensaje)) {
            String ganador = "";
            if (mensaje.getDatos() != null && mensaje.getDatos().get("ganador") != null) {
                ganador = String.valueOf(mensaje.getDatos().get("ganador"));
            }

            final String ganadorFinal = ganador;
            SwingUtilities.invokeLater(() -> {
                if (vista != null) {
                    vista.cerrarVista();
                }
                podioView podio = new podioView();
                if (!ganadorFinal.isBlank()) {
                    podio.mostrarMensaje("Ganador: " + ganadorFinal);
                }
                podio.mostrarVista();
            });
            return;
        }

        if ("PARTIDA_INICIADA".equals(tipoMensaje) || "ACTUALIZACION_MESA".equals(tipoMensaje) || "ACTUALIZACION_TABLERO".equals(tipoMensaje)) {
            if (mensaje.getDatos() == null || !mensaje.getDatos().containsKey("partida")) {
                return;
            }

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
        jugarCarta(carta, null);
    }

    public void jugarCarta(CartaDTO carta, String colorElegido) {
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_JUGAR_CARTA");
        peticion.setRemitente(miNombre);
        peticion.getDatos().put("carta", carta);
        if (colorElegido != null && !colorElegido.isBlank()) {
            peticion.getDatos().put("colorElegido", colorElegido);
        }

        System.out.println("Enviando carta al servidor...");
        proxy.enviarMensaje(peticion);
    }

    public void jugarCartaSeleccionada(CartaDTO cartaSeleccionada) {
        if (cartaSeleccionada == null) {
            if (vista != null) {
                vista.mostrarMensaje("Selecciona una carta antes de jugar.");
            }
            return;
        }

        String valor = normalizar(cartaSeleccionada.getValor());
        if ("MAS_4".equals(valor) || "CAMBIO_COLOR".equals(valor)) {
            String color = pedirColorComodin();
            if (color == null) {
                return;
            }
            jugarCarta(cartaSeleccionada, color);
            return;
        }

        jugarCarta(cartaSeleccionada);
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

    public void jugarPrimeraCartaJugable() {
        List<CartaDTO> mano = getMiMano();
        if (mano.isEmpty()) {
            tomarCarta();
            return;
        }

        for (CartaDTO carta : mano) {
            if (esJugable(carta)) {
                String valor = normalizar(carta.getValor());
                if ("MAS_4".equals(valor) || "CAMBIO_COLOR".equals(valor)) {
                    String color = pedirColorComodin();
                    if (color == null) {
                        return;
                    }
                    jugarCarta(carta, color);
                    return;
                }

                jugarCarta(carta);
                return;
            }
        }

        tomarCarta();
    }

    private boolean esJugable(CartaDTO carta) {
        if (carta == null) {
            return false;
        }

        String valor = normalizar(carta.getValor());
        if ("MAS_4".equals(valor) || "CAMBIO_COLOR".equals(valor)) {
            return true;
        }

        CartaDTO cartaCentro = getCartaCentro();
        if (cartaCentro == null) {
            return true;
        }

        String colorCentro = normalizar(cartaCentro.getColor());
        String valorCentro = normalizar(cartaCentro.getValor());
        if (Objects.equals(normalizar(carta.getColor()), colorCentro)) {
            return true;
        }
        return Objects.equals(valor, valorCentro);
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase();
    }

    private String pedirColorComodin() {
        String[] opciones = {"ROJO", "AZUL", "VERDE", "AMARILLO"};
        Object seleccion = JOptionPane.showInputDialog(
                null,
                "Elige un color para el comodín",
                "UNO",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        return seleccion != null ? seleccion.toString() : null;
    }
}
