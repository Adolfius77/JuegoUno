package controlador;

import Entidades.Carta;
import Entidades.Mazo;
import Entidades.PilaCartas;
import java.util.List;

public class MazoController {

    private final Mazo modelo;

    public MazoController(Mazo modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("el mazo es obligatorio");
        }
        this.modelo = modelo;
    }

    public Carta tomarCarta(PilaCartas pilaCartas) {
        if (modelo.estaVacio()) {
            if (pilaCartas == null) {
                throw new IllegalArgumentException("la pila es obligatoria para recargar el mazo");
            }
            modelo.recargar(pilaCartas);
        }
        return modelo.tomarCarta();
    }

    public List<Carta> repartirCartasIniciales() {
        return modelo.entregarCartas();
    }

    public boolean estaVacio() {
        return modelo.estaVacio();
    }
}
