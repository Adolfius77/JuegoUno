package dtos;

import java.io.Serializable;
import java.util.List;

public class MensajeListaJugadoresDTO extends MensajeDTO implements Serializable {
    private List<String> nombres;

    public MensajeListaJugadoresDTO() {
    }

    public MensajeListaJugadoresDTO(List<String> jugadores) {
        super("listaJugadores");
        this.nombres = jugadores;
    }

    public List<String> getJugadores() {
        return nombres;
    }
}
