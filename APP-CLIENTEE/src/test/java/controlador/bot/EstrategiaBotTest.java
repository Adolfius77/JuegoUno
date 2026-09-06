package controlador.bot;

import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.ManoDTO;
import dtos.PartidaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * La decision del bot es una funcion pura sobre DTOs, asi que se prueba sin
 * servidor ni sockets.
 *
 * Lo que mas importa aqui es que nunca elija una carta ilegal: el dominio
 * descarta una jugada invalida en silencio, sin devolver error, asi que un bot
 * equivocado no falla de forma ruidosa, simplemente congela la partida.
 */
class EstrategiaBotTest {

    private static final List<String> COLORES = List.of("ROJO", "AZUL", "VERDE", "AMARILLO");

    private static CartaDTO carta(String color, String valor) {
        return new CartaDTO(color, valor);
    }

    private static PartidaDTO partidaCon(String miNombre, List<CartaDTO> miMano,
                                         String rival, int cartasDelRival) {
        JugadorDTO yo = new JugadorDTO();
        yo.setNombre(miNombre);
        yo.setMano(new ManoDTO(new ArrayList<>(miMano)));

        List<CartaDTO> manoRival = new ArrayList<>();
        for (int i = 0; i < cartasDelRival; i++) {
            manoRival.add(carta("ROJO", "0"));
        }
        JugadorDTO otro = new JugadorDTO();
        otro.setNombre(rival);
        otro.setMano(new ManoDTO(manoRival));

        PartidaDTO partida = new PartidaDTO();
        partida.setJugadores(new ArrayList<>(Arrays.asList(yo, otro)));
        partida.setTurnoJugadorId(miNombre);
        partida.setSentidoHorario(true);
        partida.setEnCurso(true);
        return partida;
    }

    // --- La regla de jugabilidad -------------------------------------------

    @Test
    @DisplayName("un numero entra por color o por numero, nunca por otra cosa")
    void numeroEntraPorColorOPorNumero() {
        CartaDTO centro = carta("ROJO", "5");

        assertTrue(EstrategiaBot.esJugable(carta("ROJO", "9"), centro), "mismo color");
        assertTrue(EstrategiaBot.esJugable(carta("AZUL", "5"), centro), "mismo numero");
        assertFalse(EstrategiaBot.esJugable(carta("AZUL", "9"), centro), "ni color ni numero");
    }

    @Test
    @DisplayName("una accion entra por color o por la misma accion")
    void accionEntraPorColorOPorAccion() {
        CartaDTO centro = carta("VERDE", "SALTAR");

        assertTrue(EstrategiaBot.esJugable(carta("VERDE", "3"), centro));
        assertTrue(EstrategiaBot.esJugable(carta("AZUL", "SALTAR"), centro));
        assertFalse(EstrategiaBot.esJugable(carta("AZUL", "MAS_2"), centro));
    }

    @Test
    @DisplayName("un comodin siempre entra")
    void comodinSiempreEntra() {
        CartaDTO centro = carta("VERDE", "3");
        assertTrue(EstrategiaBot.esJugable(carta("NEGRO", "MAS_4"), centro));
        assertTrue(EstrategiaBot.esJugable(carta("NEGRO", "CAMBIO_COLOR"), centro));
    }

    // --- Nivel facil --------------------------------------------------------

    @Test
    @DisplayName("el facil juega la primera que le sirve")
    void facilJuegaLaPrimeraQueSirve() {
        List<CartaDTO> mano = List.of(
                carta("AZUL", "7"),      // no sirve
                carta("ROJO", "2"),      // la primera que sirve
                carta("ROJO", "8"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 5);

        Decision d = new EstrategiaFacil()
                .decidir(mano, carta("ROJO", "5"), partida, "Bot", false);

        assertEquals(Decision.Tipo.JUGAR, d.tipo());
        assertEquals("2", d.carta().getValor());
    }

    @Test
    @DisplayName("sin nada jugable roba, y si ya robo entonces pasa")
    void sinNadaRobaYLuegoPasa() {
        List<CartaDTO> mano = List.of(carta("AZUL", "7"), carta("VERDE", "9"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 5);
        CartaDTO centro = carta("ROJO", "5");

        assertEquals(Decision.Tipo.ROBAR,
                new EstrategiaFacil().decidir(mano, centro, partida, "Bot", false).tipo());
        assertEquals(Decision.Tipo.PASAR,
                new EstrategiaFacil().decidir(mano, centro, partida, "Bot", true).tipo());
    }

    @Test
    @DisplayName("al jugar un comodin pide el color que mas tiene")
    void comodinPideElColorQueMasTiene() {
        List<CartaDTO> mano = List.of(
                carta("NEGRO", "CAMBIO_COLOR"),
                carta("VERDE", "1"),
                carta("VERDE", "8"),
                carta("AZUL", "3"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 5);

        Decision d = new EstrategiaFacil()
                .decidir(mano, carta("ROJO", "5"), partida, "Bot", false);

        assertEquals("CAMBIO_COLOR", d.carta().getValor());
        assertEquals("VERDE", d.colorElegido());
    }

    @Test
    @DisplayName("si solo le quedan comodines pide el color de la mesa")
    void soloComodinesPideElColorDeLaMesa() {
        List<CartaDTO> mano = List.of(carta("NEGRO", "MAS_4"), carta("NEGRO", "CAMBIO_COLOR"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 5);

        Decision d = new EstrategiaFacil()
                .decidir(mano, carta("AMARILLO", "5"), partida, "Bot", false);

        assertEquals("AMARILLO", d.colorElegido());
    }

    // --- Nivel dificil ------------------------------------------------------

    @Test
    @DisplayName("el dificil guarda los comodines mientras tenga otra cosa")
    void dificilGuardaLosComodines() {
        List<CartaDTO> mano = List.of(
                carta("NEGRO", "MAS_4"),
                carta("NEGRO", "CAMBIO_COLOR"),
                carta("ROJO", "8"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 5);

        Decision d = new EstrategiaDificil()
                .decidir(mano, carta("ROJO", "5"), partida, "Bot", false);

        assertEquals("8", d.carta().getValor(), "deberia soltar el rojo y guardar los comodines");
    }

    @Test
    @DisplayName("entre dos comodines suelta antes el cambio de color que el +4")
    void entreComodinesSueltaPrimeroElCambioDeColor() {
        List<CartaDTO> mano = List.of(carta("NEGRO", "MAS_4"), carta("NEGRO", "CAMBIO_COLOR"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 5);

        Decision d = new EstrategiaDificil()
                .decidir(mano, carta("ROJO", "5"), partida, "Bot", false);

        assertEquals("CAMBIO_COLOR", d.carta().getValor());
    }

    @Test
    @DisplayName("si al rival le queda poco, le tira lo que mas duele")
    void ataqueCuandoElRivalVaGanando() {
        List<CartaDTO> mano = List.of(
                carta("ROJO", "8"),
                carta("ROJO", "MAS_2"),
                carta("NEGRO", "MAS_4"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 1);

        Decision d = new EstrategiaDificil()
                .decidir(mano, carta("ROJO", "5"), partida, "Bot", false);

        assertEquals("MAS_4", d.carta().getValor(), "el +4 es el que mas duele");
    }

    @Test
    @DisplayName("con el rival tranquilo, tira del color del que mas tiene")
    void prefiereElColorQueMasDomina() {
        List<CartaDTO> mano = List.of(
                carta("AZUL", "5"),     // sirve por numero, pero solo tiene un azul
                carta("ROJO", "5"),     // sirve por color, y tiene tres rojos
                carta("ROJO", "1"),
                carta("ROJO", "2"));
        PartidaDTO partida = partidaCon("Bot", mano, "Ana", 6);

        Decision d = new EstrategiaDificil()
                .decidir(mano, carta("ROJO", "5"), partida, "Bot", false);

        assertEquals("ROJO", d.carta().getColor());
    }

    // --- Lo que no puede pasar nunca ---------------------------------------

    @Test
    @DisplayName("con manos al azar ninguna estrategia elige una carta ilegal")
    void nuncaEligeUnaCartaIlegal() {
        Random azar = new Random(20260905L);
        List<EstrategiaBot> estrategias = List.of(new EstrategiaFacil(), new EstrategiaDificil());

        for (int intento = 0; intento < 2000; intento++) {
            List<CartaDTO> mano = manoAlAzar(azar);
            CartaDTO centro = cartaAlAzar(azar, false);
            PartidaDTO partida = partidaCon("Bot", mano, "Ana", 1 + azar.nextInt(7));
            boolean puedePasar = azar.nextBoolean();

            for (EstrategiaBot estrategia : estrategias) {
                Decision d = estrategia.decidir(mano, centro, partida, "Bot", puedePasar);
                assertNotNull(d, "siempre tiene que decidir algo");

                if (d.tipo() == Decision.Tipo.JUGAR) {
                    assertTrue(mano.contains(d.carta()), "la carta tiene que estar en su mano");
                    assertTrue(EstrategiaBot.esJugable(d.carta(), centro),
                            "eligio " + d.carta().getColor() + " " + d.carta().getValor()
                                    + " sobre " + centro.getColor() + " " + centro.getValor());
                    if (EstrategiaBot.esComodin(d.carta())) {
                        assertTrue(COLORES.contains(d.colorElegido()),
                                "un comodin siempre pide un color valido");
                    }
                } else {
                    assertTrue(EstrategiaBot.jugables(mano, centro).isEmpty(),
                            "no puede robar ni pasar teniendo una carta jugable");
                }
            }
        }
    }

    private static List<CartaDTO> manoAlAzar(Random azar) {
        List<CartaDTO> mano = new ArrayList<>();
        int cuantas = 1 + azar.nextInt(9);
        for (int i = 0; i < cuantas; i++) {
            mano.add(cartaAlAzar(azar, true));
        }
        return mano;
    }

    private static CartaDTO cartaAlAzar(Random azar, boolean puedeSerComodin) {
        if (puedeSerComodin && azar.nextInt(10) == 0) {
            return carta("NEGRO", azar.nextBoolean() ? "MAS_4" : "CAMBIO_COLOR");
        }
        String color = COLORES.get(azar.nextInt(COLORES.size()));
        int tipo = azar.nextInt(4);
        String valor = switch (tipo) {
            case 0 -> "SALTAR";
            case 1 -> "REVERSA";
            case 2 -> "MAS_2";
            default -> String.valueOf(azar.nextInt(10));
        };
        return carta(color, valor);
    }
}
