package red;

import Interfacez.ISerializador;
import Interfaces.IVista;
import cliente.ClienteProxy;
import dtos.MensajeDTO;
import dtos.MensajeNotificacionDTO;
import dtos.MensajeEstadoPartidaDTO;
import dtos.MensajeListaJugadoresDTO;
import vista.GameView;
import vista.LobbyView;
import javax.swing.SwingUtilities;
import java.net.Socket;

public class ClienteControlador {

    private static ClienteControlador instance;
    private ClienteProxy proxy;
    private String nombreJugador;
    private String nombreAvatar;
    private volatile IVista vistaActual;
    private String codigoSala;

    private ClienteControlador() {
    }

    public static ClienteControlador getInstance() {
        if (instance == null) {
            instance = new ClienteControlador();
        }
        return instance;
    }

    public void conectar(String host, int puerto, ISerializador serializador) throws Exception {
        Socket socket = new Socket(host, puerto);
        proxy = new ClienteProxy(socket, serializador);

        // Todo mensaje recibido pasa por aquí
        proxy.setReceptor(this::procesarMensaje);

        Thread hilo = new Thread(proxy);
        hilo.setDaemon(true);
        hilo.setName("ClienteProxy-Escucha");
        hilo.start();

        System.out.println("[ClienteControlador] Conectado al servidor.");
    }

    public void enviarMensaje(MensajeDTO mensaje) {
        proxy.enviarMensaje(mensaje);
    }

    public void setVistaActual(IVista vista) {
        this.vistaActual = vista;
        System.out.println("[ClienteControlador] Vista actual: "
                + (vista != null ? vista.getClass().getSimpleName() : "null"));
    }

    public void setDatosJugador(String nombre, String avatar) {
        this.nombreJugador = nombre;
        this.nombreAvatar = avatar;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public String getNombreAvatar() {
        return nombreAvatar;
    }

    public void setCodigoSala(String sala) {
        this.codigoSala = sala;
    }

    public String getCodigoSala() {
        return codigoSala;
    }

    // ── Procesar mensajes entrantes ───────────────────────────────────────────
    private void procesarMensaje(MensajeDTO mensaje) {
        System.out.println("[ClienteControlador] Mensaje recibido: " + mensaje.getTipo());
        if (mensaje.getDatos() == null) {
            return;
        }

        Object texto = mensaje.getDatos().get("textoMensaje");

        if (texto != null) {
            boolean error = Boolean.TRUE.equals(mensaje.getDatos().get("esError"));
            String textoStr = texto.toString().trim();
            SwingUtilities.invokeLater(() -> {
                if (vistaActual == null) {
                    return;
                }
                if (error) {
                    vistaActual.mostrarMensaje(textoStr);
                } else {
                    vistaActual.actualizar(textoStr);
                }
            });

        } else if ("ACTUALIZACION_PARTIDA".equals(mensaje.getTipo())) {
            Object partidaRaw = mensaje.getDatos().get("partida");
            com.google.gson.Gson gson = new com.google.gson.Gson();
            dtos.PartidaDTO partida = partidaRaw != null
                    ? gson.fromJson(gson.toJson(partidaRaw), dtos.PartidaDTO.class)
                    : null;

            SwingUtilities.invokeLater(() -> {
                if (vistaActual != null) {
                    vistaActual.actualizar("ACTUALIZACION_PARTIDA");
                }
                SwingUtilities.invokeLater(() -> {
                    if (vistaActual instanceof GameView && partida != null) {
                        ((GameView) vistaActual).inicializarPartida(partida);
                    }
                });
            });
        } else if (mensaje.getDatos().get("jugadores") != null) {
            Object raw = mensaje.getDatos().get("jugadores");
            java.util.List<String> nombres = new java.util.ArrayList<>();
            if (raw instanceof java.util.List) {
                for (Object item : (java.util.List<?>) raw) {
                    nombres.add(item.toString());
                }
            }
            final java.util.List<String> listaFinal = nombres;
            SwingUtilities.invokeLater(() -> {
                if (vistaActual instanceof LobbyView) {
                    ((LobbyView) vistaActual).actualizarJugadores(listaFinal);
                }
            });
        }
    }

    private void procesarNotificacion(MensajeNotificacionDTO notif) {
        String texto = notif.getTextoMensaje();
        boolean esError = notif.isEsError();

        System.out.println("[ClienteControlador] Notificación: " + texto + " error=" + esError);

        SwingUtilities.invokeLater(() -> {
            if (vistaActual == null) {
                return;
            }
            if (esError) {
                vistaActual.mostrarMensaje(texto);
            } else {
                vistaActual.actualizar(texto.trim());
            }
        });
    }

    public void desconectar() {
        instance = null;
        System.out.println("[ClienteControlador] Desconectado.");
    }
}
