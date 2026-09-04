package dtos;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public  class MensajeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo;
    private String remitente;
    private Map<String,Object> datos = new HashMap<>();
    private long timestamp;

    /**
     * Identifica la conexion que envio el mensaje. Lo asigna el proxy del
     * servidor al recibirlo; el cliente nunca lo manda.
     *
     * Antes se metia el propio IProxy (un socket vivo) dentro de 'datos', lo que
     * rompia la serializacion del DTO y ataba los comandos al transporte.
     */
    private transient String idSesion;

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

    public String getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
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
