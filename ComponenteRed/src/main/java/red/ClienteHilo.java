package red;

import dtos.MensajeNotificacionDTO;
import vista.MenuPrincipal;

import javax.swing.*;
import java.io.*;


public class ClienteHilo extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MenuPrincipal vistaRegistro;

    public ClienteHilo(ObjectInputStream in, ObjectOutputStream out, MenuPrincipal vistaRegistro) {
        this.in = in;
        this.out = out;
        this.vistaRegistro = vistaRegistro;
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
           if(vistaRegistro != null) {
               vistaRegistro.dispose();
           }
            java.awt.EventQueue.invokeLater(() -> {
                new vista.LobbyView().setVisible(true);
            });

        } else {
            JOptionPane.showMessageDialog(vistaRegistro, msg.getTextoMensaje(), "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}