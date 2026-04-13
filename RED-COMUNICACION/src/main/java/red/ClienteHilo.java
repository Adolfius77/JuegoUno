package red;


import dtos.MensajeNotificacionDTO;
import java.io.*;


public class ClienteHilo extends Thread {
    private final ObjectInputStream in;

    public ClienteHilo(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object object = in.readObject();
                if (object instanceof MensajeNotificacionDTO) {
                    MensajeNotificacionDTO msg = (MensajeNotificacionDTO) object;
                    procesarNotificacion(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("Conexión perdida con el servidor.");
            e.printStackTrace();
        }
    }

    public void procesarNotificacion(MensajeNotificacionDTO msg) {
        if (msg.getTextoMensaje().equals("Registro exitoso")) {
            System.out.println("[Cliente] Registro exitoso");
        } else {
            System.err.println("[Cliente] Error de registro: " + msg.getTextoMensaje());
        }
    }
}
