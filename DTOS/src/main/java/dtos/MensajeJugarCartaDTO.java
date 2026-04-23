package dtos;

public class MensajeJugarCartaDTO extends MensajeDTO{
    private static final long serialVersionUID = 1L;
    private String idRemitente;
    private CartaDTO cartaJugada;
    private String colorElegido;

    public MensajeJugarCartaDTO(String idRemitente, CartaDTO cartaJugada, String colorElegido) {
        super("JUGAR_CARTA");
        this.idRemitente = idRemitente;
        this.cartaJugada = cartaJugada;
        this.colorElegido = colorElegido;
    }

    public String getIdRemitente() {
        return idRemitente;
    }

    public void setIdRemitente(String idRemitente) {
        this.idRemitente = idRemitente;
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
