package dtos;

import java.io.Serializable;

public class MensajePasarTurnoDTO extends MensajeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idJugadorActual;
    private String sentido;
    private String mensaje;

    public MensajePasarTurnoDTO(String idJugadorActual, String sentido, String mensaje) {
        super("PASAR_TURNO");
        this.idJugadorActual = idJugadorActual;
        this.sentido = sentido;
        this.mensaje = mensaje;
    }

    public String getIdJugadorActual() {
        return idJugadorActual;
    }

    public String getSentido() {
        return sentido;
    }

    public String getMensaje() {
        return mensaje;
    }
}