package red;

import Entidades.Lobby;
import dtos.MensajeNotificacionDTO;
import dtos.MensajeRegistroDTO;
import java.io.*;

public class ServidorHilo extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Lobby lobby;

    public ServidorHilo(DataInputStream in, DataOutputStream out, String lobby) {
        this.in = in;
        this.out = out;
        this.lobby = lobby;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object objeto = in.readObject();

                if (objeto instanceof MensajeRegistroDTO) {
                    MensajeRegistroDTO dto = (MensajeRegistroDTO) objeto;
                    validarNombre(dto);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cliente desconectado.");
        }
    }

    private void validarNombre(MensajeRegistroDTO dto) throws IOException {
        boolean nombreExistente = lobby.getNombreJugadores().stream()
                .anyMatch(j -> j.equalsIgnoreCase(dto.getNombre()));
        if (nombreExistente) {
            out.writeObject(new MensajeNotificacionDTO("Error: El nombre '" + dto.getNombre() + "' ya está en uso."));
        } else {
            lobby.agregarJugador(dto.getNombre());
            out.writeObject(new MensajeNotificacionDTO("Registro exitoso"));
        }
        out.flush();
    }
}