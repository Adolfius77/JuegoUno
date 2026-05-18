package Mappers;


import Entidades.Logica.Partida;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.PartidaDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartidaMapper {
    private static final jugadorMapper jMapper = new jugadorMapper();
    private static final CartaMapper cMapper = new CartaMapper();

    public static PartidaDTO toDTO(Partida partida) {
        if (partida == null) return null;

        List<JugadorDTO> jugadoresDTO = (partida.getJugadores() != null)
                ? partida.getJugadores().stream()
                .map(jMapper::toDTO)
                .collect(Collectors.toList())
                : new ArrayList<>();

        String turnoId = "";
        String mensajeTurno = "Sin turno asignado";
        if (partida.getJugadorActual() != null) {
            turnoId = partida.getJugadorActual().getNombre() != null
                    ? partida.getJugadorActual().getNombre()
                    : "";
            mensajeTurno = "Es el turno de " + partida.getJugadorActual().getNombre();
        }

        CartaDTO cartaCentro = null;
        String colorActual = "SIN_COLOR";
        if (partida.getPilaCartas() != null && !partida.getPilaCartas().getListaCartas().isEmpty()) {
            cartaCentro = cMapper.toDTO(partida.getPilaCartas().obtenerUltimaCarta());
            if (partida.getPilaCartas().getColorActivo() != null) {
                colorActual = partida.getPilaCartas().getColorActivo().name();
            }
        }

        int mazoTamano = 0;
        if (partida.getMazo() != null) {
            mazoTamano = partida.getMazo().getCantidadCartas();
        }

        boolean enJuego = partida.getEstado() != null && 
                         partida.getEstado().getClass().getSimpleName().equals("EstadoJugando");

        return new PartidaDTO(
                partida.getId(),
                jugadoresDTO,
                cartaCentro,
                turnoId,
                enJuego,
                colorActual,
                partida.getSentido() == Entidades.enums.Sentido.HORARIO,
                mazoTamano,
                mensajeTurno,
                mazoTamano
        );
    }

}
