package Mappers;

import Entidades.Jugador;
import dtos.JugadorDTO;

public class jugadorMapper {
    private static final ManoMapper manoMapper = new ManoMapper();

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
            jugadorDTO.setMano(manoMapper.toDTO(jugador.getMano()));
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
        if(jugadorDTO.getMano() != null) {
            j.setMano(manoMapper.toEntity(jugadorDTO.getMano()));
        }
        j.setDijoUno(jugadorDTO.isDijoUno());
        j.setPuntaje(jugadorDTO.getPuntuaje());

        return j;


}
}
