package Mappers;

import Entidades.Jugador;
import Entidades.Mano;
import dtos.CartaDTO;
import dtos.JugadorDTO;

import java.util.List;

public class jugadorMapper {
public JugadorDTO toDTO(Jugador jugador){
    if(jugador == null){
        return null;
    }
    JugadorDTO jugadorDTO = new JugadorDTO();
    jugadorDTO.setId(jugador.getId());
    jugadorDTO.setNombre(jugador.getNombre());
    jugadorDTO.setDijoUno(jugador.getDijoUno());
    jugadorDTO.setPuntuaje(jugador.getPuntaje());

    if(jugador.getAvatar() !=null){
        jugadorDTO.setAvatar(jugador.getAvatar());
    }
    if(jugador.getMano() !=null){
        jugadorDTO.setCartas((List<CartaDTO>) jugador.getMano());

    }
    return jugadorDTO;
}
public Jugador toEntity(JugadorDTO jugadorDTO){
    if(jugadorDTO == null){
        return null;
    }
    Jugador j = new Jugador();
    j.setId(jugadorDTO.getId());
    j.setNombre(jugadorDTO.getNombre());
    j.setAvatar(jugadorDTO.getAvatar());
    j.setMano((Mano) jugadorDTO.getCartas());
    j.setDijoUno(jugadorDTO.isDijoUno());
    j.setPuntaje(jugadorDTO.getPuntuaje());

    return j;


}
}
