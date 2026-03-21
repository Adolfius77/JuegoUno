package dtos;

import java.io.Serializable;

public class MensajeRegistroDTO extends MensajeDTO implements Serializable {
    private String nombre;
    private String nombreAvatar;

    public MensajeRegistroDTO(String nombre, String nombreAvatar) {
        super();
        this.nombre = nombre;
        this.nombreAvatar = nombreAvatar;
    }

    public String getNombre() {
        return nombre;
    }
    public String getNombreAvatar() {
        return nombreAvatar;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setNombreAvatar(String nombreAvatar) {
        this.nombreAvatar = nombreAvatar;
    }

}
