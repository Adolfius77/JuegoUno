package dtos;

public class MensajeJugarCartaDTO extends MensajeDTO{
private static final long serialVersionUID = 1L;
private CartaDTO cartaJugada;
private String colorElegido;

    public MensajeJugarCartaDTO(String idRemitente, CartaDTO cartaJugada, String colorElegido) {
        super(idRemitente);
        this.cartaJugada = cartaJugada;
        this.colorElegido = colorElegido;
    }

    public CartaDTO getCartaJugada() {
        return cartaJugada;
    }

    public void setCartaJugada(CartaDTO cartaJugada) {
        this.cartaJugada = cartaJugada;
    }

    public String getColorElegido() {
        return colorElegido;
    }

    public void setColorElegido(String colorElegido) {
        this.colorElegido = colorElegido;
    }
}
