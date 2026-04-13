package controlador;

import Entidades.Carta;
import Entidades.Mano;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManoController {

    private final Mano modelo;

    public ManoController(Mano modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("la mano es obligatoria");
        }
        this.modelo = modelo;
    }

    public List<Carta> obtenerCartas() {
        return Collections.unmodifiableList(new ArrayList<>(modelo.getCartas()));
    }

    public List<Carta> obtenerCartasJugables(Carta cartaActiva) {
        if (cartaActiva == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(modelo.ObtenerCartasJugables(cartaActiva));
    }

    public boolean tieneCarta(Carta carta) {
        if (carta == null) {
            return false;
        }
        return modelo.getCartas().contains(carta);
    }
}
