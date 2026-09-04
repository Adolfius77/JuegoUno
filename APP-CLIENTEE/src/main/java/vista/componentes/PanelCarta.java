package vista.componentes;

import dtos.CartaDTO;
import vista.tema.Tema;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Dibuja una carta de UNO.
 *
 * Antes las cartas de accion se resolvian con texto improvisado: "x" para
 * SALTAR, "&lt;-" para REVERSA y "CC" para CAMBIO_COLOR. Ahora esos tres y los
 * comodines se dibujan como simbolos vectoriales, que es como se ven en el juego
 * real.
 *
 * La carta tambien conoce su estado (seleccionada / jugable) y lo dibuja ella
 * misma; antes TableroView le ponia un borde dorado desde fuera.
 */
public class PanelCarta extends JPanel {

    // El contenedor de la mano tiene altura fija desde el editor de formularios:
    // con cartas mas altas se cortaban por abajo.
    private static final int ANCHO = 70;
    private static final int ALTO = 98;

    private CartaDTO carta;
    private boolean seleccionada;
    private boolean jugable = true;

    public PanelCarta(CartaDTO carta) {
        this.carta = carta;
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setOpaque(false);
    }

    public void setCarta(CartaDTO carta) {
        this.carta = carta;
        repaint();
    }

    public CartaDTO getCarta() {
        return carta;
    }

    public void setSeleccionada(boolean seleccionada) {
        if (this.seleccionada != seleccionada) {
            this.seleccionada = seleccionada;
            repaint();
        }
    }

    public boolean isSeleccionada() {
        return seleccionada;
    }

    public void setJugable(boolean jugable) {
        if (this.jugable != jugable) {
            this.jugable = jugable;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (carta == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int w = getWidth();
        int h = getHeight();

        // La carta seleccionada se levanta unos pixeles en vez de recibir un
        // borde: se lee mejor cuando las cartas estan solapadas en la mano.
        int subida = seleccionada ? 10 : 0;
        int margen = 4;
        int cw = w - margen * 2;
        int ch = h - margen * 2 - subida;
        int cx = margen;
        int cy = margen + (seleccionada ? 0 : subida);

        dibujarSombra(g2, cx, cy, cw, ch, seleccionada ? 7 : 4);

        Color base = colorDe(carta.getColor());
        boolean comodin = esComodin();

        // Cuerpo blanco de la carta
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(cx, cy, cw, ch, 14, 14));

        // Relleno de color, con un degradado suave que le da volumen
        int p = 6;
        Color arriba = aclarar(base, 0.18f);
        g2.setPaint(new GradientPaint(cx, cy, arriba, cx, cy + ch, base));
        g2.fill(new RoundRectangle2D.Float(cx + p, cy + p, cw - p * 2, ch - p * 2, 10, 10));

        // Ovalo blanco inclinado, la marca de la casa de UNO
        Graphics2D go = (Graphics2D) g2.create();
        go.translate(cx + cw / 2.0, cy + ch / 2.0);
        go.rotate(Math.toRadians(-25));
        go.setColor(Color.WHITE);
        go.fill(new Ellipse2D.Float(-cw / 2f + 9, -ch / 4f - 4, cw - 18, ch / 2f + 8));
        go.dispose();

        if (comodin) {
            dibujarRuedaColores(g2, cx + cw / 2, cy + ch / 2, Math.min(cw, ch) / 4);
            if ("MAS_4".equals(carta.getValor())) {
                dibujarTextoCentral(g2, "+4", cx, cy, cw, ch, base, 26);
            }
        } else {
            dibujarCentro(g2, cx, cy, cw, ch, base);
        }

        dibujarEsquinas(g2, cx, cy, cw, ch);

        // Las cartas que no se pueden jugar se atenuan
        if (!jugable) {
            g2.setColor(new Color(255, 255, 255, 130));
            g2.fill(new RoundRectangle2D.Float(cx, cy, cw, ch, 14, 14));
        }

        g2.dispose();
    }

    // --- Piezas del dibujo -------------------------------------------------

    private void dibujarSombra(Graphics2D g2, int x, int y, int w, int h, int capas) {
        for (int i = capas; i > 0; i--) {
            g2.setColor(new Color(0, 0, 0, 14));
            g2.fill(new RoundRectangle2D.Float(x - i / 2f, y + i, w + i, h + i, 16, 16));
        }
    }

    private void dibujarCentro(Graphics2D g2, int x, int y, int w, int h, Color base) {
        String valor = carta.getValor() == null ? "" : carta.getValor();
        int cx = x + w / 2;
        int cy = y + h / 2;
        switch (valor) {
            case "SALTAR" -> dibujarProhibido(g2, cx, cy, Math.min(w, h) / 4, base);
            case "REVERSA" -> dibujarReversa(g2, cx, cy, Math.min(w, h) / 4, base);
            case "MAS_2" -> dibujarTextoCentral(g2, "+2", x, y, w, h, base, 30);
            default -> dibujarTextoCentral(g2, valor, x, y, w, h, base, 42);
        }
    }

    /** Circulo con barra diagonal: la carta de saltar turno. */
    private void dibujarProhibido(Graphics2D g2, int cx, int cy, int r, Color base) {
        g2.setColor(base);
        g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
        double d = r * 0.78;
        g2.drawLine((int) (cx - d), (int) (cy + d), (int) (cx + d), (int) (cy - d));
    }

    /**
     * Dos flechas paralelas apuntando en sentidos opuestos, como la carta real.
     * Se dibujan rectas en vez de curvas porque a 78x116 los arcos se leian
     * como dos letras C.
     */
    private void dibujarReversa(Graphics2D g2, int cx, int cy, int r, Color base) {
        g2.setColor(base);
        int largo = (int) (r * 1.7);
        int sep = (int) (r * 0.55);

        // Izquierda, hacia arriba
        flechaVertical(g2, cx - sep, cy, largo, true);
        // Derecha, hacia abajo
        flechaVertical(g2, cx + sep, cy, largo, false);
    }

    private void flechaVertical(Graphics2D g2, int x, int cy, int largo, boolean haciaArriba) {
        int mitad = largo / 2;
        int grosor = 6;
        int punta = 9;
        int yIni = haciaArriba ? cy + mitad : cy - mitad;
        int yFin = haciaArriba ? cy - mitad : cy + mitad;

        g2.setStroke(new BasicStroke(grosor, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        g2.drawLine(x, yIni, x, yFin + (haciaArriba ? punta : -punta));

        Path2D.Float p = new Path2D.Float();
        p.moveTo(x - punta, yFin + (haciaArriba ? punta : -punta));
        p.lineTo(x + punta, yFin + (haciaArriba ? punta : -punta));
        p.lineTo(x, yFin);
        p.closePath();
        g2.fill(p);
    }

    /** Rueda de cuatro cuadrantes: los comodines. */
    private void dibujarRuedaColores(Graphics2D g2, int cx, int cy, int r) {
        Color[] colores = {Tema.ROJO, Tema.AMARILLO, Tema.VERDE, Tema.AZUL};
        for (int i = 0; i < 4; i++) {
            g2.setColor(colores[i]);
            g2.fill(new Arc2D.Float(cx - r, cy - r, r * 2, r * 2, i * 90, 90, Arc2D.PIE));
        }
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
    }

    private void dibujarTextoCentral(Graphics2D g2, String texto, int x, int y, int w, int h,
                                     Color base, int tamano) {
        if (texto.isEmpty()) {
            return;
        }
        g2.setFont(new Font(Tema.familia(), Font.BOLD, tamano));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(texto)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(new Color(0, 0, 0, 45));
        g2.drawString(texto, tx + 2, ty + 2);
        g2.setColor(esComodin() ? Color.WHITE : base);
        g2.drawString(texto, tx, ty);
    }

    /** Valor pequeno en las dos esquinas opuestas, como en la carta fisica. */
    private void dibujarEsquinas(Graphics2D g2, int x, int y, int w, int h) {
        String texto = textoCorto();
        if (texto.isEmpty()) {
            return;
        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Tema.familia(), Font.BOLD, 14));
        g2.drawString(texto, x + 8, y + 20);

        Graphics2D ge = (Graphics2D) g2.create();
        ge.translate(x + w, y + h);
        ge.rotate(Math.PI);
        ge.drawString(texto, 8, 20);
        ge.dispose();
    }

    // --- Traduccion del DTO ------------------------------------------------

    private boolean esComodin() {
        String v = carta.getValor();
        return "MAS_4".equals(v) || "CAMBIO_COLOR".equals(v);
    }

    /** Version corta para las esquinas, donde no cabe un simbolo dibujado. */
    private String textoCorto() {
        String valor = carta.getValor();
        if (valor == null) {
            return "";
        }
        return switch (valor) {
            case "SALTAR" -> "Ø";
            case "REVERSA" -> "⇄";
            case "MAS_2" -> "+2";
            case "MAS_4" -> "+4";
            case "CAMBIO_COLOR" -> "";
            default -> valor;
        };
    }

    private Color colorDe(String colorStr) {
        if (colorStr == null || "SIN_COLOR".equals(colorStr)) {
            return new Color(0x20, 0x20, 0x24);
        }
        return switch (colorStr) {
            case "ROJO" -> Tema.ROJO;
            case "AZUL" -> Tema.AZUL;
            case "VERDE" -> Tema.VERDE;
            case "AMARILLO" -> Tema.AMARILLO;
            case "NEGRO" -> new Color(0x20, 0x20, 0x24);
            default -> new Color(0x20, 0x20, 0x24);
        };
    }

    private static Color aclarar(Color c, float f) {
        return new Color(
                Math.min(255, (int) (c.getRed() + 255 * f)),
                Math.min(255, (int) (c.getGreen() + 255 * f)),
                Math.min(255, (int) (c.getBlue() + 255 * f)));
    }
}
