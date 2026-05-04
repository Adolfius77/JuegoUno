package red;

import Interfaces.IVista;
import dtos.MensajeEstadoPartidaDTO;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;
import vista.MenuPrincipal;
import javax.swing.SwingUtilities;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteRed { 

    private static ClienteRed instance;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String nombreJugador;
    private String nombreAvatar;
    private MenuPrincipal vista;

    private volatile IVista vistaActual;  // volatile: visibilidad entre hilos
    private Thread hiloEscucha;           // hilo separado, recreable

    private ClienteRed() {}

    public static ClienteRed getInstance() {
        if (instance == null) {
            instance = new ClienteRed();
        }
        return instance;
    }

    public void conectar() throws Exception {
        if (socket != null && !socket.isClosed()) return;

        socket = new Socket("127.0.0.1", 9091);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        // Hilo de escucha independiente, siempre nuevo
        hiloEscucha = new Thread(this::escuchar);
        hiloEscucha.setDaemon(true); // muere con la app, no la bloquea
        hiloEscucha.setName("ClienteRed-Escucha");
        hiloEscucha.start();

        System.out.println("[ClienteRed] Conectado al servidor.");
    }

    public void setVistaMenu(MenuPrincipal vista, String nombreJugador, String nombreAvatar) {
        this.vista = vista;
        this.nombreJugador = nombreJugador;
        this.nombreAvatar = nombreAvatar;
    }

    public void setVistaActual(IVista nuevaVista) {
        this.vistaActual = nuevaVista;
        System.out.println("[ClienteRed] vistaActual actualizada a: "
                + (nuevaVista != null ? nuevaVista.getClass().getSimpleName() : "null"));
    }

    public synchronized void enviarMensaje(Object mensaje) throws Exception {
        if (out != null) {
            out.writeObject(mensaje);
            out.reset();
            out.flush();
        }
    }

    private void escuchar() {
        try {
            while (true) {
                Object objeto = in.readObject();
                System.out.println("[ClienteRed] Objeto recibido: "
                        + objeto.getClass().getSimpleName());

                if (objeto instanceof MensajeNotificacionDTO) {
                    procesarNotificacion((MensajeNotificacionDTO) objeto);

                } else if (objeto instanceof MensajeListaJugadoresDTO) {
                    SwingUtilities.invokeLater(() -> {
                        if (vistaActual != null) vistaActual.actualizar("LISTA_ACTUALIZADA");
                    });

                } else if (objeto instanceof MensajeEstadoPartidaDTO) {
                    MensajeEstadoPartidaDTO estado = (MensajeEstadoPartidaDTO) objeto;
                    SwingUtilities.invokeLater(() -> {
                        if (vistaActual != null) vistaActual.actualizar(estado.getTipo());
                    });

                } else if (objeto instanceof String) {
                    String msj = ((String) objeto).trim();
                    SwingUtilities.invokeLater(() -> {
                        if (vistaActual != null) vistaActual.actualizar(msj);
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("[ClienteRed] Conexión cerrada: " + e.getMessage());
        }
    }

    private void procesarNotificacion(MensajeNotificacionDTO notif) {
        String texto = notif.getTextoMensaje(); // ← getter correcto confirmado
        boolean esError = notif.isEsError();

        System.out.println("[ClienteRed] Notificación: texto='"
                + texto + "' esError=" + esError
                + " vistaActual=" + (vistaActual != null
                        ? vistaActual.getClass().getSimpleName() : "NULL ⚠️"));

        if (esError) {
            SwingUtilities.invokeLater(() -> {
                if (vistaActual != null) vistaActual.mostrarMensaje(texto);
                if (vista != null) vista.setFormularioHabilitado(true);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                if (vistaActual != null) {
                    vistaActual.actualizar(texto.trim());
                } else {
                    System.out.println("[ClienteRed] ⚠️ vistaActual es null, evento perdido: " + texto);
                }
            });
        }
    }
}