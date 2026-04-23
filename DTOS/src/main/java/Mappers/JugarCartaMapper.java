package Mappers;

import Entidades.Carta;
import Entidades.cartaComodin;
import dtos.MensajeJugarCartaDTO;
import dtos.CartaDTO;

public class JugarCartaMapper {
    private final CartaMapper cMapper = new CartaMapper();

    public MensajeJugarCartaDTO toDTO(String jugadorId, Carta carta, String colorElegido) {
        if (carta == null) return null;

        CartaDTO cartaDto = cMapper.toDTO(carta);
        String colorFinal = (carta instanceof cartaComodin) ? colorElegido : null;

        return new MensajeJugarCartaDTO(jugadorId, cartaDto, colorFinal);
    }
}