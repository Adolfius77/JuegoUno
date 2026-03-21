package dtos;

import java.io.Serializable;
import java.util.List;

public class MensajeListaJugadoresDTO implements Serializable {
    private List<String> nombres;

    public MensajeListaJugadoresDTO(List<String> jugadores) {
        this.nombres = jugadores;
    }

    public List<String> getJugadores() {
        return nombres;
    }
}
