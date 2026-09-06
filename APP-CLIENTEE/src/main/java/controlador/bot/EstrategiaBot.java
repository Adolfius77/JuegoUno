package controlador.bot;

import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.PartidaDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decide que hace el bot cuando le toca.
 *
 * La implementacion recibe el estado ya digerido y devuelve una Decision; no
 * conoce la red. Aqui viven ademas las reglas comunes a cualquier nivel, sobre
 * todo la de que carta se puede jugar: es una copia exacta de lo que valida el
 * dominio, y tiene que serlo porque EstadoJugando.jugarCarta descarta en
 * silencio una jugada ilegal. Si el bot se equivoca, no recibe un error: se
 * queda esperando un turno que nunca avanza.
 */
public interface EstrategiaBot {

    String COMODIN_COLOR = "CAMBIO_COLOR";
    String COMODIN_MAS_4 = "MAS_4";
    List<String> COLORES = List.of("ROJO", "AZUL", "VERDE", "AMARILLO");

    /** Cartas que obligan al siguiente jugador a robar o le quitan el turno. */
    List<String> CARTAS_DE_ATAQUE = List.of("MAS_4", "MAS_2", "SALTAR", "REVERSA");

    /**
     * @param mano       cartas del bot
     * @param centro     carta visible en el descarte, con el color ya resuelto
     *                   si fue comodin (el servidor se lo pinta al jugarla)
     * @param estado     la partida completa, para mirar a los rivales
     * @param miNombre   nombre del bot dentro de la partida
     * @param puedePasar false mientras no haya robado: sin robar no se pasa
     */
    Decision decidir(List<CartaDTO> mano, CartaDTO centro, PartidaDTO estado,
                     String miNombre, boolean puedePasar);

    /** Nombre corto que se imprime en la consola del bot. */
    String nombre();

    // --- Reglas comunes ----------------------------------------------------

    static String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase();
    }

    static boolean esComodin(CartaDTO carta) {
        String valor = normalizar(carta == null ? null : carta.getValor());
        return COMODIN_MAS_4.equals(valor) || COMODIN_COLOR.equals(valor);
    }

    static boolean esNumero(CartaDTO carta) {
        return carta != null && normalizar(carta.getValor()).matches("\\d+");
    }

    /**
     * Misma regla que CartaNumerica.esJugable, CartaAccion.esJugable y
     * cartaComodin.esJugable en DOMINIO: comodin siempre; numero por color o
     * por numero; accion por color o por accion. Un numero NO empareja con una
     * accion aunque las cadenas se comparen sueltas, porque "5" nunca es
     * "SALTAR".
     */
    static boolean esJugable(CartaDTO carta, CartaDTO centro) {
        if (carta == null) {
            return false;
        }
        if (esComodin(carta)) {
            return true;
        }
        if (centro == null) {
            return true;
        }
        if (normalizar(carta.getColor()).equals(normalizar(centro.getColor()))) {
            return true;
        }
        return normalizar(carta.getValor()).equals(normalizar(centro.getValor()));
    }

    static List<CartaDTO> jugables(List<CartaDTO> mano, CartaDTO centro) {
        List<CartaDTO> resultado = new ArrayList<>();
        for (CartaDTO carta : mano) {
            if (esJugable(carta, centro)) {
                resultado.add(carta);
            }
        }
        return resultado;
    }

    /**
     * El color que mas se repite en la mano, para pedirlo con un comodin. Si
     * solo quedan comodines no hay color que contar y se usa el de la mesa.
     */
    static String colorMasFrecuente(List<CartaDTO> mano, CartaDTO centro) {
        Map<String, Integer> cuenta = new HashMap<>();
        for (CartaDTO carta : mano) {
            if (esComodin(carta)) {
                continue;
            }
            String color = normalizar(carta.getColor());
            if (COLORES.contains(color)) {
                cuenta.merge(color, 1, Integer::sum);
            }
        }

        String mejor = null;
        int mejorCuenta = 0;
        // Se recorre COLORES y no el mapa para que un empate siempre se rompa
        // igual: un bot que decide distinto con la misma mano es imposible de
        // probar.
        for (String color : COLORES) {
            int veces = cuenta.getOrDefault(color, 0);
            if (veces > mejorCuenta) {
                mejor = color;
                mejorCuenta = veces;
            }
        }

        if (mejor != null) {
            return mejor;
        }
        String colorMesa = normalizar(centro == null ? null : centro.getColor());
        return COLORES.contains(colorMesa) ? colorMesa : COLORES.get(0);
    }

    /** El color a pedir si la carta es comodin, o null si no lo es. */
    static String colorParaJugar(CartaDTO carta, List<CartaDTO> mano, CartaDTO centro) {
        if (!esComodin(carta)) {
            return null;
        }
        List<CartaDTO> resto = new ArrayList<>(mano);
        resto.remove(carta);
        return colorMasFrecuente(resto, centro);
    }

    /**
     * A quien le toca despues del bot. Se calcula igual que
     * Partida.calcularSiguienteIndice: por el orden de la lista y el sentido.
     * Devuelve null si el estado todavia no trae jugadores.
     */
    static JugadorDTO siguienteJugador(PartidaDTO estado, String miNombre) {
        if (estado == null || estado.getJugadores() == null || estado.getJugadores().size() < 2) {
            return null;
        }
        List<JugadorDTO> jugadores = estado.getJugadores();
        int mio = -1;
        for (int i = 0; i < jugadores.size(); i++) {
            JugadorDTO j = jugadores.get(i);
            if (j != null && miNombre != null && miNombre.equals(j.getNombre())) {
                mio = i;
                break;
            }
        }
        if (mio < 0) {
            return null;
        }
        int paso = estado.isSentidoHorario() ? 1 : -1;
        int siguiente = Math.floorMod(mio + paso, jugadores.size());
        return jugadores.get(siguiente);
    }

    static int cartasEnMano(JugadorDTO jugador) {
        if (jugador == null || jugador.getMano() == null || jugador.getMano().getCartas() == null) {
            return 0;
        }
        return jugador.getMano().getCartas().size();
    }
}
