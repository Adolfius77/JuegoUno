package dtos;

import java.io.Serializable;
import java.util.List;

public class PartidaDTO implements Serializable {
    private static final long getSerialVersionUID =  1L;
    private String id;
    private List<JugadorDTO> jugadores;
    private CartaDTO cartaCentro;
    private String turnoJugadorId;
    private boolean enCurso;
    private String colorActual;
    private boolean sentidoHorario;
    private int mazoTamano;
    private String mensajeEstado;

    public PartidaDTO() {
    }

    public PartidaDTO(String id, List<JugadorDTO> jugadores, CartaDTO cartaCentro,
                      String turnoJugadorId, boolean enCurso, String colorActual,
                      boolean sentidoHorario, int mazoTamano, String mensajeEstado) {
        this.id = id;
        this.jugadores = jugadores;
        this.cartaCentro = cartaCentro;
        this.turnoJugadorId = turnoJugadorId;
        this.enCurso = enCurso;
        this.colorActual = colorActual;
        this.sentidoHorario = sentidoHorario;
        this.mazoTamano = mazoTamano;
        this.mensajeEstado = mensajeEstado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<JugadorDTO> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<JugadorDTO> jugadores) {
        this.jugadores = jugadores;
    }

    public CartaDTO getCartaCentro() {
        return cartaCentro;
    }

    public void setCartaCentro(CartaDTO cartaCentro) {
        this.cartaCentro = cartaCentro;
    }

    public String getTurnoJugadorId() {
        return turnoJugadorId;
    }

    public void setTurnoJugadorId(String turnoJugadorId) {
        this.turnoJugadorId = turnoJugadorId;
    }

    public boolean isEnCurso() {
        return enCurso;
    }

    public void setEnCurso(boolean enCurso) {
        this.enCurso = enCurso;
    }

    public String getColorActual() {
        return colorActual;
    }

    public void setColorActual(String colorActual) {
        this.colorActual = colorActual;
    }

    public boolean isSentidoHorario() {
        return sentidoHorario;
    }

    public void setSentidoHorario(boolean sentidoHorario) {
        this.sentidoHorario = sentidoHorario;
    }

    public int getMazoTamano() {
        return mazoTamano;
    }

    public void setMazoTamano(int mazoTamano) {
        this.mazoTamano = mazoTamano;
    }

    public String getMensajeEstado() {
        return mensajeEstado;
    }

    public void setMensajeEstado(String mensajeEstado) {
        this.mensajeEstado = mensajeEstado;
    }
}
