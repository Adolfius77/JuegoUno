package dtos;

import java.io.Serializable;

public abstract class MensajeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idRemitentel;

    public MensajeDTO(String idRemitente) {
        this.idRemitentel = idRemitente;
    }

    public String getIdRemitentel() {
        return idRemitentel;
    }

    public void setIdRemitentel(String idRemitentel) {
        this.idRemitentel = idRemitentel;
    }
}
