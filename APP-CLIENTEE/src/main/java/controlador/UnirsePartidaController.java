/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import vista.LobbyView;
import vista.unirsePartidaView;

/**
 *
 * @author USER
 */
public class UnirsePartidaController {

    private final IVista vista;
    private final unirsePartidaView vistaUnirse;
    private final ClienteProxy proxy;
    private String nombreInvitadoTemporal;

    public UnirsePartidaController(unirsePartidaView vista, ClienteProxy proxy) {
        this.vista = vista;
        this.vistaUnirse = vista;
        this.proxy = proxy;
        if (this.proxy != null) {
            this.proxy.setReceptor(this::escucharEventoRed);
        }
    }

    public void solicitarListaPartidas() {
        if (proxy == null) {
            return;
        }
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_LISTA_PARTIDAS");
        peticion.setRemitente("CLIENTE");
        proxy.enviarMensaje(peticion);
    }

    public void solicitarUnirse(String nombreInvitado, String codigoSala) {
        if (proxy == null) {
            return;
        }
        this.nombreInvitadoTemporal = nombreInvitado;
        System.out.println("[UNISER PARTIDA CONTROLLER] Solicitando unirse ala partida");
        MensajeDTO peticion = new MensajeDTO();
        peticion.setTipo("PETICION_UNIRSE_PARTIDA");
        peticion.setRemitente("CLIENTE");
        peticion.getDatos().put("nombre", nombreInvitado);
        peticion.getDatos().put("codigoSala", codigoSala != null ? codigoSala.trim().toUpperCase() : "");
        proxy.enviarMensaje(peticion);
    }

    private void escucharEventoRed(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getTipo() == null) {
            return;
        }

        switch (mensaje.getTipo()) {
            case "UNIDO_EXITO" -> {
                String codigoSala = (String) mensaje.getDatos().get("codigoSala");
                String nombre = (String) mensaje.getDatos().getOrDefault("nombre", nombreInvitadoTemporal);
                List<?> jugadores = null;
                Object jugadoresRaw = mensaje.getDatos().get("jugadores");
                if (jugadoresRaw instanceof List<?>) {
                    jugadores = (List<?>) jugadoresRaw;
                }

                LobbyView lobby = new LobbyView();
                LobbyController control = new LobbyController(proxy, codigoSala, nombre, false, lobby);
                control.cargarDatosIniciales(jugadores);

                SwingUtilities.invokeLater(() -> {
                    vista.cerrarVista();
                    lobby.setVisible(true);
                });
            }
            case "ERROR_UNIRSE" -> SwingUtilities.invokeLater(() -> {
                String motivo = mensaje.getDatos() != null
                        ? String.valueOf(mensaje.getDatos().getOrDefault("motivo", "Error desconocido"))
                        : "Error desconocido";
                if (vista != null) {
                    vista.mostrarMensaje("Error: " + motivo + " - No se pudo unirse a la partida.");
                }
            });
            case "LISTA_PARTIDAS_DISPONIBLES" -> {
                Object partidasRaw = mensaje.getDatos() != null ? mensaje.getDatos().get("partidas") : null;
                List<Map<String, Object>> partidas = normalizarPartidas(partidasRaw);
                SwingUtilities.invokeLater(() -> vistaUnirse.mostrarPartidasDisponibles(partidas));
            }
            default -> {
            }
        }
    }

    private List<Map<String, Object>> normalizarPartidas(Object partidasRaw) {
        List<Map<String, Object>> normalizadas = new ArrayList<>();
        if (!(partidasRaw instanceof List<?> listaRaw)) {
            return normalizadas;
        }

        for (Object partidaRaw : listaRaw) {
            if (!(partidaRaw instanceof Map<?, ?> mapaRaw)) {
                continue;
            }
            Map<String, Object> partida = new HashMap<>();
            partida.put("codigoSala", String.valueOf(valorOMinimo(mapaRaw, "codigoSala", "")));
            partida.put("nombreSala", String.valueOf(valorOMinimo(mapaRaw, "nombreSala", "Sala sin nombre")));
            partida.put("host", String.valueOf(valorOMinimo(mapaRaw, "host", "")));
            partida.put("jugadoresActuales", valorOMinimo(mapaRaw, "jugadoresActuales", 0));
            partida.put("limiteJugadores", valorOMinimo(mapaRaw, "limiteJugadores", 4));
            normalizadas.add(partida);
        }
        return normalizadas;
    }

    private Object valorOMinimo(Map<?, ?> mapaRaw, String llave, Object porDefecto) {
        Object valor = mapaRaw.get(llave);
        return valor != null ? valor : porDefecto;
    }
}
