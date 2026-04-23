package dtos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public  class MensajeDTO implements Serializable {
    private static final long getSerialVersionUID =  1L;
    private String tipo;
    private String remitente;
    private Map<String,Object> datos;
    private long timestamp;

    public MensajeDTO() {
    }

    public MensajeDTO(String remitente) {
        this(null, remitente);
    }

    public MensajeDTO(String tipo,String remitente) {
        this.tipo = tipo;
        this.remitente = remitente;
        this.datos = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public Map<String, Object> getDatos() {
        return datos;
    }

    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MensajeDTO{" +
                "tipo='" + tipo + '\'' +
                ", remitente='" + remitente + '\'' +
                ", datos=" + datos +
                ", timestamp=" + timestamp +
                '}';
    }
}
