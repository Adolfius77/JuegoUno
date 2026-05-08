/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

/**
 *
 * @author Usuario
 */
public class MensajeCrearPartidaDTO extends MensajeDTO {

    private static final long serialVersionUID = 1L;
    private String nombreSala;
    private int limiteJugadores;

    public MensajeCrearPartidaDTO(String nombreSala, int limiteJugadores) {
        super("CREAR_PARTIDA", "CLIENTE");
        this.nombreSala = nombreSala;
        this.limiteJugadores = limiteJugadores;
        getDatos().put("nombreSala", nombreSala);
        getDatos().put("limiteJugadores", limiteJugadores);
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public int getLimiteJugadores() {
        return limiteJugadores;
    }
}
