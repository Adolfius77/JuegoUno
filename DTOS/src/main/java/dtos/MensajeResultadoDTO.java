package dtos;

import java.io.Serializable;
import java.util.List;

public class MensajeResultadoDTO extends MensajeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreGanador;
    private List<JugadorDTO> podio;

    public MensajeResultadoDTO(String nombreGanador, List<JugadorDTO> podio) {
        super("RESULTADO_PARTIDA");
        this.nombreGanador = nombreGanador;
        this.podio = podio;
    }

    public String getNombreGanador() {
        return nombreGanador;
    }

    public List<JugadorDTO> getPodio() {
        return podio;
    }
}