package Mappers;

import Entidades.Logica.Partida;
import dtos.JugadorDTO;
import dtos.MensajeResultadoDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ResultadoMapper {
    private static final jugadorMapper jMapper = new jugadorMapper();

    public static MensajeResultadoDTO toDTO(Partida partida, String nombreGanador) {
        if (partida == null) return null;

        List<JugadorDTO> jugadoresFinales = partida.getJugadores().stream()
                .map(j -> jMapper.toDTO(j))
                .collect(Collectors.toList());

        return new MensajeResultadoDTO(nombreGanador, jugadoresFinales);
    }
}