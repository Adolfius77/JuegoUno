package vista.componentes;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.List;

/**
 * Fila de cartas solapadas de la mano del jugador.
 *
 * Usa posiciones absolutas a proposito. Con un FlowLayout, traer una carta al
 * frente con setComponentZOrder tambien le cambia el indice, y FlowLayout coloca
 * por indice: al pasar el raton, la carta saltaba al primer lugar y toda la mano
 * se reordenaba a la vista. Fijando las posiciones a mano, el indice solo decide
 * quien se pinta encima, que es lo unico que queremos.
 */
public class ManoDeCartas extends JPanel {

    // El alto cabe en el visor de la mano (100 px) para que las cartas no
    // salgan cortadas por abajo ni aparezca una barra de desplazamiento.
    private static final int ANCHO_CARTA = 70;
    private static final int ALTO_CARTA = 98;
    private static final int SOLAPE = 16;

    public ManoDeCartas() {
        setLayout(null);
        setOpaque(false);
    }

    /** Coloca las cartas de izquierda a derecha, solapadas. */
    public void colocar(List<PanelCarta> cartas) {
        removeAll();
        int paso = ANCHO_CARTA - SOLAPE;

        for (int i = 0; i < cartas.size(); i++) {
            PanelCarta carta = cartas.get(i);
            carta.setBounds(i * paso, 0, ANCHO_CARTA, ALTO_CARTA);
            add(carta);
        }

        int ancho = cartas.isEmpty() ? 0 : (cartas.size() - 1) * paso + ANCHO_CARTA;
        setPreferredSize(new Dimension(ancho, ALTO_CARTA));
        revalidate();
        repaint();
    }

    /**
     * Pone la carta encima de sus vecinas sin moverla de sitio. Sin esto, la
     * carta que se levanta quedaria medio tapada por la de su izquierda.
     */
    public void traerAlFrente(PanelCarta carta) {
        if (carta != null && carta.getParent() == this) {
            setComponentZOrder(carta, 0);
            repaint();
        }
    }
}
