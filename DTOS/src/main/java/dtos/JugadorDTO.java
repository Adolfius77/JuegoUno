    package dtos;

import java.io.Serializable;

    public class JugadorDTO implements Serializable {
    private static final long serialVersionUID =  1L;
    private String id;
    private String nombre;
    private byte[] avatar;
    private ManoDTO mano;
    private boolean dijoUno;
    private int puntuaje;


    public JugadorDTO() {
    }
    public JugadorDTO(String id, String nombre, byte[] avatar, ManoDTO mano, boolean dijoUno, int puntuaje) {
        this.id = id;
        this.nombre = nombre;
        this.avatar = avatar;
        this.mano = mano;
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

    public ManoDTO getMano() {
        return mano;
    }

    public void setMano(ManoDTO mano) {
        this.mano = mano;
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
