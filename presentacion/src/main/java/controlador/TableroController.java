package controlador;

import Entidades.Carta;
import Entidades.PilaCartas;
import Entidades.enums.Color;

public class TableroController {

    private final PilaCartas modelo;

    public TableroController(PilaCartas modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("la pila del tablero es obligatoria");
        }
        this.modelo = modelo;
    }

    public Carta jugarCarta(Carta carta) {
        if (carta == null) {
            throw new IllegalArgumentException("la carta es obligatoria");
        }
        return modelo.agregarCarta(carta);
    }

    public Carta obtenerCartaActiva() {
        if (modelo.getListaCartas().isEmpty()) {
            return null;
        }
        return modelo.obtenerUltimaCarta();
    }

    public Color obtenerColorActivo() {
        return modelo.getColorActivo();
    }
}
