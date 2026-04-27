package Mappers;

import Entidades.Lobby;
import dtos.MensajeListaJugadoresDTO;

import java.util.ArrayList;

public class LobbyMapper {
    public static MensajeListaJugadoresDTO toDTO(Lobby lobby) {
        if (lobby == null) {
            return null;
        }
        return new MensajeListaJugadoresDTO(new ArrayList<>(lobby.getNombreJugadores()));
    }
}
