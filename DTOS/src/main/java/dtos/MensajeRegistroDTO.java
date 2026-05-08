package dtos;

import java.io.Serializable;

public class MensajeRegistroDTO extends MensajeDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private String nombreAvatar;

    public MensajeRegistroDTO() {
        super("SOLICITUD_REGISTRO", "CLIENTE");
    }

    public MensajeRegistroDTO(String nombre, String nombreAvatar) {
        super("SOLICITUD_REGISTRO", "CLIENTE");
        this.nombre = nombre;
        this.nombreAvatar = nombreAvatar;
        getDatos().put("nombreJugador", nombre);
        getDatos().put("nombreAvatar", nombreAvatar);
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreAvatar() {
        return nombreAvatar;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        getDatos().put("nombreJugador", nombre);
    }

    public void setNombreAvatar(String nombreAvatar) {
        this.nombreAvatar = nombreAvatar;
        getDatos().put("nombreAvatar", nombreAvatar);
    }

}
