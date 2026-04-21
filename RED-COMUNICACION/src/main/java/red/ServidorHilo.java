package red;

import Entidades.Lobby;
import dtos.MensajeListaJugadoresDTO;
import dtos.MensajeNotificacionDTO;
import dtos.MensajeRegistroDTO;
import interfaces.IReceptorMensajes;

import java.io.*;

public class ServidorHilo extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Lobby lobby;

    public ServidorHilo(ObjectInputStream in, ObjectOutputStream out, Lobby lobby ) {
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
        boolean nombreExistente = lobby.getNombreJugadores().stream().anyMatch(j -> j.equalsIgnoreCase(dto.getNombre()));
        if (nombreExistente) {
            out.writeObject(new MensajeNotificacionDTO("SERVIDOR",true , "error: el nombre" + dto.getNombre() + "ya esta en uso"));
        } else {
            lobby.agregarJugador(dto.getNombre());
            out.writeObject(new MensajeNotificacionDTO("SERVIDOR",false , "Registro exitoso"));
            difundirLista();
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
