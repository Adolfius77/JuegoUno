package dtos;


import java.io.Serializable;
import java.util.List;

public class JugadorDTO implements Serializable {
    private static final long getSerialVersionUID =  1L;
    private String id;
    private String nombre;
    private byte[] avatar;
    private List<CartaDTO> cartas;
    private boolean dijoUno;
    private int puntuaje;


        public JugadorDTO() {
    }
    public JugadorDTO(String id, String nombre, byte[] avatar, List<CartaDTO> cartas, boolean dijoUno, int puntuaje) {
        this.id = id;
        this.nombre = nombre;
        this.avatar = avatar;
        this.cartas = cartas;
        this.dijoUno = dijoUno;
        this.puntuaje = puntuaje;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public List<CartaDTO> getCartas() {
        return cartas;
    }

    public void setCartas(List<CartaDTO> cartas) {
        this.cartas = cartas;
    }

    public boolean isDijoUno() {
        return dijoUno;
    }

    public void setDijoUno(boolean dijoUno) {
        this.dijoUno = dijoUno;
    }

    public int getPuntuaje() {
        return puntuaje;
    }

    public void setPuntuaje(int puntuaje) {
        this.puntuaje = puntuaje;
    }
}
