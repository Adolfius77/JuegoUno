package controlador.bot;

import dtos.CartaDTO;

/**
 * Lo que el bot decidio hacer en su turno.
 *
 * Se separa de quien la ejecuta para que la estrategia sea una funcion pura
 * sobre DTOs: sin red y sin estado, se puede probar con JUnit sin levantar el
 * servidor.
 */
public record Decision(Tipo tipo, CartaDTO carta, String colorElegido) {

    public enum Tipo {
        JUGAR,
        ROBAR,
        PASAR
    }

    public static Decision jugar(CartaDTO carta, String colorElegido) {
        return new Decision(Tipo.JUGAR, carta, colorElegido);
    }

    public static Decision robar() {
        return new Decision(Tipo.ROBAR, null, null);
    }

    public static Decision pasar() {
        return new Decision(Tipo.PASAR, null, null);
    }

    @Override
    public String toString() {
        return switch (tipo) {
            case JUGAR -> "jugar " + carta.getColor() + " " + carta.getValor()
                    + (colorElegido != null ? " (pide " + colorElegido + ")" : "");
            case ROBAR -> "robar";
            case PASAR -> "pasar";
        };
    }
}
