package dtos;


import java.util.List;
import java.util.Map;

public class MensajeEstadoPartidaDTO extends MensajeDTO{
    private static final long serialVersionUID = 1L;
    private CartaDTO cartaEnTope;
    private String idJugadorTurnoActual;
    private String sentidoJuego;
    private boolean alguienGritoUno;
    private Map<String, Integer> cantidadCartasPorJugador;
    private List<CartaDTO>miMano;


    public MensajeEstadoPartidaDTO(CartaDTO cartaEnTope, String idJugadorTurnoActual, String sentidoJuego, boolean alguienGritoUno, Map<String, Integer> cantidadCartasPorJugador, List<CartaDTO> miMano) {
        super("SERVIDOR");
        this.cartaEnTope = cartaEnTope;
        this.idJugadorTurnoActual = idJugadorTurnoActual;
        this.sentidoJuego = sentidoJuego;
        this.alguienGritoUno = alguienGritoUno;
        this.cantidadCartasPorJugador = cantidadCartasPorJugador;
        this.miMano = miMano;
    }

    public CartaDTO getCartaEnTope() {
        return cartaEnTope;
    }

    public void setCartaEnTope(CartaDTO cartaEnTope) {
        this.cartaEnTope = cartaEnTope;
    }

    public String getIdJugadorTurnoActual() {
        return idJugadorTurnoActual;
    }

    public void setIdJugadorTurnoActual(String idJugadorTurnoActual) {
        this.idJugadorTurnoActual = idJugadorTurnoActual;
    }

    public String getSentidoJuego() {
        return sentidoJuego;
    }

    public void setSentidoJuego(String sentidoJuego) {
        this.sentidoJuego = sentidoJuego;
    }

    public boolean isAlguienGritoUno() {
        return alguienGritoUno;
    }

    public void setAlguienGritoUno(boolean alguienGritoUno) {
        this.alguienGritoUno = alguienGritoUno;
    }

    public Map<String, Integer> getCantidadCartasPorJugador() {
        return cantidadCartasPorJugador;
    }

    public void setCantidadCartasPorJugador(Map<String, Integer> cantidadCartasPorJugador) {
        this.cantidadCartasPorJugador = cantidadCartasPorJugador;
    }

    public List<CartaDTO> getMiMano() {
        return miMano;
    }

    public void setMiMano(List<CartaDTO> miMano) {
        this.miMano = miMano;
    }

}
