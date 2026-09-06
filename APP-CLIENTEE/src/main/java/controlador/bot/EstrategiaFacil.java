package controlador.bot;

import dtos.CartaDTO;
import dtos.PartidaDTO;

import java.util.List;

/**
 * Juega la primera carta que le sirve, sin pensar mas.
 *
 * Es el mismo criterio que GameController.jugarPrimeraCartaJugable, que ya
 * estaba en el cliente para las pruebas.
 */
public class EstrategiaFacil implements EstrategiaBot {

    @Override
    public String nombre() {
        return "facil";
    }

    @Override
    public Decision decidir(List<CartaDTO> mano, CartaDTO centro, PartidaDTO estado,
                            String miNombre, boolean puedePasar) {
        for (CartaDTO carta : mano) {
            if (EstrategiaBot.esJugable(carta, centro)) {
                return Decision.jugar(carta, EstrategiaBot.colorParaJugar(carta, mano, centro));
            }
        }
        return puedePasar ? Decision.pasar() : Decision.robar();
    }
}
