package controlador;

public class ConfiguracionController {

    private int limiteJugadores = 4;
    private String nombreSala = "Sala UNO";

    public int getLimiteJugadores() {
        return limiteJugadores;
    }

    public void setLimiteJugadores(int limiteJugadores) {
        if (limiteJugadores < 2 || limiteJugadores > 4) {
            throw new IllegalArgumentException("el limite de jugadores debe estar entre 2 y 4");
        }
        this.limiteJugadores = limiteJugadores;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        if (nombreSala == null || nombreSala.trim().isEmpty()) {
            throw new IllegalArgumentException("el nombre de la sala es obligatorio");
        }
        this.nombreSala = nombreSala.trim();
    }
}
