package dtos;

public class MensajeNotificacionDTO extends MensajeDTO {

    private static final long serialVersionUID = 1L;
    private String textoMensaje;
    private boolean esError;//para movimientos invalidos

    public MensajeNotificacionDTO() {
    }

    public MensajeNotificacionDTO(String idRemitente, boolean esError, String textoMensaje) {
        super("NOTIFICACION", idRemitente);
        this.esError = esError;
        this.textoMensaje = textoMensaje;
        getDatos().put("textoMensaje", textoMensaje);
        getDatos().put("esError", esError);
    }

    public String getTextoMensaje() {
        return textoMensaje;
    }

    public void setTextoMensaje(String textoMensaje) {
        this.textoMensaje = textoMensaje;
    }

    public boolean isEsError() {
        return esError;
    }

    public void setEsError(boolean esError) {
        this.esError = esError;
    }
}
