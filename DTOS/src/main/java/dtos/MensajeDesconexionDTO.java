package dtos;

import java.io.Serializable;

public class MensajeDesconexionDTO extends MensajeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombreUsuario;

    public MensajeDesconexionDTO() {
    }
    
    public MensajeDesconexionDTO(String nombreUsuario) {
        super("DESCONEXION");
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
