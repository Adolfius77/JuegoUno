package red;

import dtos.MensajeNotificacionDTO;
import vista.MenuPrincipal;
import vista.SeleccionPartida;

import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteRed extends Thread {
    private static ClienteRed instance;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean escuchando = false;
    private String nombreJugador;
    private String nombreAvatar;
    private MenuPrincipal vista;

    private ClienteRed() {
    }
    
    LectorConfiguracion config = new LectorConfiguracion();
    String ip = config.getIpServidor();
    int puerto = config.getPuertoServidor();
    
    public void conectar() throws Exception {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(ip, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            escuchando = true;

            if (!this.isAlive()) {
                this.start();
            }
        }
    }

    public void setVistaMenu(MenuPrincipal vista, String nombreJugador, String nombreAvatar) {
        this.vista = vista;
        this.nombreJugador = nombreJugador;
        this.nombreAvatar = nombreAvatar;
    }

    public synchronized void enviarMensaje(Object mensaje) throws Exception {
        if (out != null) {
            out.writeObject(mensaje);
            out.reset();
            out.flush();
        }
    }

    @Override
    public void run() {
        try {
            while (escuchando) {
                Object objeto = in.readObject();

                if (objeto instanceof MensajeNotificacionDTO) {
                    MensajeNotificacionDTO notif = (MensajeNotificacionDTO) objeto;
                    if (!notif.isEsError()) {
                        SwingUtilities.invokeLater(() -> {
                            SeleccionPartida sp = new SeleccionPartida();
                            // PASAMOS LOS DATOS A LA NUEVA VENTANA
                            sp.setJugadorInfo(nombreJugador, nombreAvatar);
                            sp.setVisible(true);
                            if (vista != null) vista.dispose();
                        });
                    } else {
                        SwingUtilities.invokeLater(() ->
                                vista.mostrarMensaje(notif.getTextoMensaje()));
                    }
                }
            }
        } catch (Exception e) {
            escuchando = false;
        }
    }

    public static ClienteRed getInstance() {
        if (instance == null) {
            instance = new ClienteRed();
        }
        return instance;
    }

}
