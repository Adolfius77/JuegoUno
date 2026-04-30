package red;

import Entidades.Lobby;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;
import dtos.MensajeRegistroDTO;
import java.io.*;

public class ServidorHilo extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Lobby lobby;
    private String nombreJugador;

    public ServidorHilo(ObjectInputStream in, ObjectOutputStream out, Lobby lobby) {
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
            if (this.nombreJugador != null && !this.nombreJugador.isBlank()) {
                lobby.getNombreJugadores().removeIf(j -> j.equalsIgnoreCase(this.nombreJugador));
                lobby.notificarObservador("LISTA_ACTUALIZADA");
                System.out.println("Jugador removido del lobby: " + this.nombreJugador);
            }
            Servidor.hilosConectados.remove(this);
        }
    }

    private void validarNombre(MensajeRegistroDTO dto) throws IOException {
        try {
            lobby.agregarJugador(dto.getNombre());
            this.nombreJugador = dto.getNombre();
            out.writeObject(new MensajeNotificacionDTO("SERVIDOR", false, "Registro exitoso"));
            difundirLista();
        } catch (IllegalArgumentException e) {
            out.writeObject(new MensajeNotificacionDTO("SERVIDOR", true, "error: " + e.getMessage()));
        }
        out.flush();
    }

    private void difundirLista() {
        MensajeListaJugadoresDTO msgLista = new MensajeListaJugadoresDTO(lobby.getNombreJugadores());

        for (ServidorHilo cliente : Servidor.hilosConectados){
            try{
                cliente.out.writeObject(msgLista);
                cliente.out.flush();
            } catch (IOException e) {
                System.out.println("Error al escribir lista de jugadores.");
            }
        }
    }
}