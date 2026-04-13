package dtos;
import java.io.Serializable;

public class paqueteRedDTO implements Serializable {
    private String destino;
    private String remitente;
    private String accion;
    private Object payload;

    public paqueteRedDTO(String destino, String remitente, String accion, Object payload) {
        this.destino = destino;
        this.remitente = remitente;
        this.accion = accion;
        this.payload = payload;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}