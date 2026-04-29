package dtos;

import java.io.Serializable;
import java.util.List;

public class ManoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CartaDTO> cartas;

    public ManoDTO() {
    }

    public ManoDTO(List<CartaDTO> cartas) {
        this.cartas = cartas;
    }

    public List<CartaDTO> getCartas() {
        return cartas;
    }

    public void setCartas(List<CartaDTO> cartas) {
        this.cartas = cartas;
    }

    public int getTamano() {
        return cartas != null ? cartas.size() : 0;
    }
}

