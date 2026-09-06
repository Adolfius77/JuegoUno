package controlador.bot;

import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.PartidaDTO;

import java.util.Comparator;
import java.util.List;

/**
 * Juega con algo de cabeza en vez de soltar lo primero que encaja.
 *
 * Tres ideas, en este orden:
 *
 *  1. Si al siguiente jugador le queda poco, le cae encima lo que mas duela.
 *  2. Los comodines son la ultima bala: sirven siempre, asi que se guardan
 *     para cuando no haya otra cosa.
 *  3. Mientras tanto, tira del color del que mas cartas tiene, para no
 *     quedarse sin salida en ese color, y suelta antes los numeros altos.
 */
public class EstrategiaDificil implements EstrategiaBot {

    /** Con estas cartas o menos, el rival esta a punto de ganar. */
    private static final int RIVAL_EN_PELIGRO = 2;

    // Grupos de preferencia; se juega siempre el grupo mas alto disponible.
    private static final int GRUPO_ATAQUE = 4;
    private static final int GRUPO_NORMAL = 3;
    private static final int GRUPO_CAMBIO_COLOR = 2;
    private static final int GRUPO_MAS_4 = 1;

    @Override
    public String nombre() {
        return "dificil";
    }

    @Override
    public Decision decidir(List<CartaDTO> mano, CartaDTO centro, PartidaDTO estado,
                            String miNombre, boolean puedePasar) {
        List<CartaDTO> jugables = EstrategiaBot.jugables(mano, centro);
        if (jugables.isEmpty()) {
            return puedePasar ? Decision.pasar() : Decision.robar();
        }

        JugadorDTO siguiente = EstrategiaBot.siguienteJugador(estado, miNombre);
        boolean rivalEnPeligro = siguiente != null
                && EstrategiaBot.cartasEnMano(siguiente) <= RIVAL_EN_PELIGRO;

        // max() con un comparador estable: ante dos cartas igual de buenas gana
        // la primera de la mano, para que el bot sea reproducible en pruebas.
        CartaDTO elegida = jugables.stream()
                .max(Comparator
                        .comparingInt((CartaDTO c) -> grupo(c, rivalEnPeligro))
                        .thenComparingInt(c -> desempate(c, mano, rivalEnPeligro)))
                .orElse(jugables.get(0));

        return Decision.jugar(elegida, EstrategiaBot.colorParaJugar(elegida, mano, centro));
    }

    private int grupo(CartaDTO carta, boolean rivalEnPeligro) {
        String valor = EstrategiaBot.normalizar(carta.getValor());
        if (rivalEnPeligro && EstrategiaBot.CARTAS_DE_ATAQUE.contains(valor)) {
            return GRUPO_ATAQUE;
        }
        if (EstrategiaBot.COMODIN_MAS_4.equals(valor)) {
            return GRUPO_MAS_4;
        }
        if (EstrategiaBot.COMODIN_COLOR.equals(valor)) {
            return GRUPO_CAMBIO_COLOR;
        }
        return GRUPO_NORMAL;
    }

    private int desempate(CartaDTO carta, List<CartaDTO> mano, boolean rivalEnPeligro) {
        String valor = EstrategiaBot.normalizar(carta.getValor());

        if (rivalEnPeligro && EstrategiaBot.CARTAS_DE_ATAQUE.contains(valor)) {
            // El orden de la lista va de la mas dura a la mas suave.
            return EstrategiaBot.CARTAS_DE_ATAQUE.size()
                    - EstrategiaBot.CARTAS_DE_ATAQUE.indexOf(valor);
        }

        if (EstrategiaBot.esComodin(carta)) {
            return 0;
        }

        // Quedarse con el color del que mas tiene, y soltar antes lo que mas
        // estorba: las acciones y los numeros grandes.
        int mismoColor = 0;
        String color = EstrategiaBot.normalizar(carta.getColor());
        for (CartaDTO otra : mano) {
            if (!EstrategiaBot.esComodin(otra)
                    && EstrategiaBot.normalizar(otra.getColor()).equals(color)) {
                mismoColor++;
            }
        }

        int peso = EstrategiaBot.esNumero(carta) ? Integer.parseInt(valor) : 15;
        return mismoColor * 100 + peso;
    }
}
