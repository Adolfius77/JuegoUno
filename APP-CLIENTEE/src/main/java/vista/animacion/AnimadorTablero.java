package vista.animacion;

import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.PartidaDTO;
import vista.TableroView;
import vista.tema.Tema;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Decide que animar comparando el estado anterior de la partida con el nuevo.
 *
 * El servidor manda una foto completa (PartidaDTO), no un evento del estilo
 * "fulano jugo el 5 rojo". Asi que la unica forma de saber que paso es mirar que
 * cambio entre una foto y la siguiente:
 *
 *   - cambio la carta del centro  -> alguien jugo una carta
 *   - a alguien le crecio la mano -> robo cartas
 *   - cambio el turno             -> le toca a otro
 */
public class AnimadorTablero {

    private static final int MS_VUELO = 520;
    private static final int MS_AVISO = 1300;

    private final TableroView tablero;
    private PartidaDTO anterior;

    public AnimadorTablero(TableroView tablero) {
        this.tablero = tablero;
    }

    /** Se llama en cada actualizacion, ANTES de guardar el nuevo estado. */
    public void alActualizar(PartidaDTO nuevo, String miNombre) {
        CapaAnimacion capa = tablero.getCapaAnimacion();
        if (capa == null || nuevo == null) {
            this.anterior = nuevo;
            return;
        }
        if (anterior == null) {
            // Primera foto de la partida: no hay con que comparar.
            this.anterior = nuevo;
            return;
        }

        animarCartaJugada(capa, nuevo, miNombre);
        animarRobos(capa, nuevo, miNombre);

        this.anterior = nuevo;
    }

    /** Al cerrar la partida, para no arrastrar el estado a la siguiente. */
    public void reiniciar() {
        this.anterior = null;
    }

    // --- Carta jugada ------------------------------------------------------

    private void animarCartaJugada(CapaAnimacion capa, PartidaDTO nuevo, String miNombre) {
        CartaDTO centroAntes = anterior.getCartaCentro();
        CartaDTO centroAhora = nuevo.getCartaCentro();

        if (centroAhora == null || mismaCarta(centroAntes, centroAhora)) {
            return;
        }

        // Quien jugo: el que tenia el turno en la foto ANTERIOR.
        String autor = anterior.getTurnoJugadorId();
        Point desde = puntoDe(autor, miNombre);
        Point hasta = tablero.puntoCentro();

        if (desde != null && hasta != null) {
            capa.volarCarta(centroAhora, desde, hasta, MS_VUELO, null);
        }

        avisoPorEfecto(capa, centroAhora, nuevo);
    }

    /** Mensaje grande para las cartas que cambian el curso del juego. */
    private void avisoPorEfecto(CapaAnimacion capa, CartaDTO carta, PartidaDTO nuevo) {
        String valor = carta.getValor();
        if (valor == null) {
            return;
        }
        switch (valor) {
            case "MAS_2" -> capa.mostrarAviso("+2", Tema.ROJO, MS_AVISO);
            case "MAS_4" -> capa.mostrarAviso("+4", Tema.ROJO, MS_AVISO);
            case "SALTAR" -> capa.mostrarAviso("¡Turno saltado!", Tema.AMARILLO, MS_AVISO);
            case "REVERSA" -> capa.mostrarAviso("¡Cambio de sentido!", Tema.AZUL, MS_AVISO);
            case "CAMBIO_COLOR" -> capa.mostrarAviso("Color: " + nombreColor(carta.getColor()),
                    Tema.AMARILLO, MS_AVISO);
            default -> { }
        }
    }

    private String nombreColor(String color) {
        if (color == null) {
            return "?";
        }
        return switch (color) {
            case "ROJO" -> "rojo";
            case "AZUL" -> "azul";
            case "VERDE" -> "verde";
            case "AMARILLO" -> "amarillo";
            default -> color.toLowerCase();
        };
    }

    // --- Robos -------------------------------------------------------------

    private void animarRobos(CapaAnimacion capa, PartidaDTO nuevo, String miNombre) {
        Map<String, Integer> antes = cartasPorJugador(anterior);
        Point mazo = tablero.puntoMazo();
        if (mazo == null) {
            return;
        }

        for (JugadorDTO j : seguro(nuevo.getJugadores())) {
            if (j == null || j.getNombre() == null) {
                continue;
            }
            int ahora = cuantasCartas(j);
            int previo = antes.getOrDefault(j.getNombre(), ahora);
            int robadas = ahora - previo;
            if (robadas <= 0) {
                continue;
            }

            Point destino = puntoDe(j.getNombre(), miNombre);
            if (destino == null) {
                continue;
            }

            // Reverso generico: no se revela lo que robo un rival.
            CartaDTO reverso = new CartaDTO("ROJO", "UNO");
            int aAnimar = Math.min(robadas, 4);   // con +4 ya se entiende
            for (int i = 0; i < aAnimar; i++) {
                final int retraso = i * 110;
                javax.swing.Timer t = new javax.swing.Timer(retraso + 1,
                        e -> capa.volarCarta(reverso, mazo, destino, MS_VUELO, null));
                t.setRepeats(false);
                t.start();
            }
        }
    }

    // --- Utilidades --------------------------------------------------------

    /** De donde sale o a donde llega una carta segun de quien sea. */
    private Point puntoDe(String nombreJugador, String miNombre) {
        if (nombreJugador == null) {
            return null;
        }
        if (nombreJugador.equals(miNombre)) {
            return tablero.puntoManoPropia();
        }
        return tablero.puntoDeJugador(nombreJugador);
    }

    private Map<String, Integer> cartasPorJugador(PartidaDTO partida) {
        Map<String, Integer> conteo = new HashMap<>();
        for (JugadorDTO j : seguro(partida.getJugadores())) {
            if (j != null && j.getNombre() != null) {
                conteo.put(j.getNombre(), cuantasCartas(j));
            }
        }
        return conteo;
    }

    private int cuantasCartas(JugadorDTO j) {
        if (j.getMano() == null || j.getMano().getCartas() == null) {
            return 0;
        }
        return j.getMano().getCartas().size();
    }

    private List<JugadorDTO> seguro(List<JugadorDTO> lista) {
        return lista == null ? List.of() : lista;
    }

    private boolean mismaCarta(CartaDTO a, CartaDTO b) {
        if (a == null || b == null) {
            return a == b;
        }
        return Objects.equals(a.getColor(), b.getColor())
                && Objects.equals(a.getValor(), b.getValor());
    }
}
