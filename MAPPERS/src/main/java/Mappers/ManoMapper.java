package Mappers;

import Entidades.Mano;
import dtos.CartaDTO;
import dtos.ManoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ManoMapper {
    private static final CartaMapper cMapper = new CartaMapper();

    public static ManoDTO toDTO(Mano mano) {
        if (mano == null) return null;

        List<CartaDTO> cartasDTO = (mano.getCartas() != null)
                ? mano.getCartas().stream()
                .map(cMapper::toDTO)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return new ManoDTO(cartasDTO);
    }

    public static Mano toEntity(ManoDTO manoDTO) {
        if (manoDTO == null) return new Mano();
        return new Mano();
    }
}


