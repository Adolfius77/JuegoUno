package Mappers;

import Entidades.Logica.Partida;
import dtos.JugadorDTO;
import dtos.PartidaDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PartidaMapper {
    private final jugadorMapper jMapper = new jugadorMapper();
    private final CartaMapper cMapper = new CartaMapper();

    public PartidaDTO toDTO(Partida partida) {
        if (partida == null) return null;

        List<JugadorDTO> jugadoresDTO = partida.getJugadores().stream()
                .map(jMapper::toDTO)
                .collect(Collectors.toList());

        String turnoId = (partida.getJugadorActual() != null) ? partida.getJugadorActual().getId() : "";

        return new PartidaDTO(
                partida.getId(),
                jugadoresDTO,
                cMapper.toDTO(partida.getPilaCartas().obtenerUltimaCarta()),
                turnoId,
                partida.getEstado().toString().contains("Jugando"),
                partida.getPilaCartas().getColorActivo() != null ? partida.getPilaCartas().getColorActivo().name() : "SIN_COLOR",
                partida.getSentido() == Entidades.enums.Sentido.HORARIO,
                partida.getMazo().estaVacio() ? 0 : 1,
                "Es el turno de " + partida.getJugadorActual().getNombre()
        );
    }

}
