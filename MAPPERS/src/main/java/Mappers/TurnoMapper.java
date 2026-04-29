package Mappers;

import Entidades.Logica.turno;
import dtos.MensajePasarTurnoDTO;

public class TurnoMapper {

    public static MensajePasarTurnoDTO toDTO(turno logicaTurno) {
        if (logicaTurno == null) return null;

        String jugadorActualId = (logicaTurno.getJugadorActual() != null)
                ? logicaTurno.getJugadorActual().getId()
                : "";

        String sentidoActual = logicaTurno.getSentido().name();

        String mensajeStatus = "Es el turno de: " +
                (logicaTurno.getJugadorActual() != null
                        ? logicaTurno.getJugadorActual().getNombre()
                        : "Nadie");

        return new MensajePasarTurnoDTO(
                jugadorActualId,
                sentidoActual,
                mensajeStatus
        );
    }
}