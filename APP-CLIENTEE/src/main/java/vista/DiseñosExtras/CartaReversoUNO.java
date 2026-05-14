package vista.DiseñosExtras;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;


public class CartaReversoUNO extends JPanel {

    // Tamaño base de diseño (se escala automáticamente)
    private static final int BASE_W = 200;
    private static final int BASE_H = 300;

    public CartaReversoUNO() {
        setPreferredSize(new Dimension(BASE_W, BASE_H));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Factor de escala para que todo se adapte al tamaño del componente
        double sx = (double) w / BASE_W;
        double sy = (double) h / BASE_H;
        g2.scale(sx, sy);

        dibujarCarta(g2);
        g2.dispose();
    }

    private void dibujarCarta(Graphics2D g2) {
        // ── 1. Fondo negro con esquinas redondeadas ──────────────────────────
        RoundRectangle2D fondo = new RoundRectangle2D.Float(0, 0, BASE_W, BASE_H, 24, 24);
        g2.setColor(Color.BLACK);
        g2.fill(fondo);

        // Borde blanco fino
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(fondo);

        // ── 2. Óvalo rojo diagonal ───────────────────────────────────────────
        // Guardamos el clip original para restaurarlo después
        Shape clipOriginal = g2.getClip();

        // Recortamos al borde de la carta antes de dibujar el óvalo
        g2.clip(fondo);

        // Óvalo base centrado, luego rotado ~-35°
        Ellipse2D ovalo = new Ellipse2D.Float(
            BASE_W / 2f - 72, BASE_H / 2f - 118,  // x, y
            144, 236                                 // ancho, alto
        );

        AffineTransform rotacion = AffineTransform.getRotateInstance(
            Math.toRadians(-35),
            BASE_W / 2.0, BASE_H / 2.0
        );

        Shape ovaloRotado = rotacion.createTransformedShape(ovalo);

        g2.setColor(new Color(204, 0, 0)); // Rojo UNO
        g2.fill(ovaloRotado);

        // Restauramos el clip
        g2.setClip(clipOriginal);
        g2.clip(fondo);  // mantenemos el clip de la carta

        // ── 3. Logo "UNO" ────────────────────────────────────────────────────
        dibujarLogoUNO(g2);

        // ── 4. Símbolo ® ─────────────────────────────────────────────────────
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("®", 147, 112);

        // Restaurar clip completo
        g2.setClip(clipOriginal);
    }

    private void dibujarLogoUNO(Graphics2D g2) {
        // Centro de la carta
        float cx = BASE_W / 2f;      // 100
        float cy = BASE_H / 2f + 5f; // 155

        // ── Letra U ──────────────────────────────────────────────────────────
        dibujarLetra(g2, "U", cx - 54, cy);

        // ── Letra N ──────────────────────────────────────────────────────────
        dibujarLetra(g2, "N", cx, cy);

        // ── Letra O ──────────────────────────────────────────────────────────
        dibujarLetra(g2, "O", cx + 54, cy);
    }

    /**
     * Dibuja una letra del logo UNO con los tres capas de color:
     *  1. Borde negro grueso (sombra/contorno exterior)
     *  2. Borde blanco (halo)
     *  3. Relleno amarillo
     */
    private void dibujarLetra(Graphics2D g2, String letra, float cx, float cy) {
        Font font = new Font("Arial Black", Font.BOLD, 52);

        // Obtenemos el contorno de la letra como Shape
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, letra);
        Shape shape = gv.getOutline();

        // Centrar la letra en (cx, cy)
        Rectangle2D bounds = shape.getBounds2D();
        double tx = cx - bounds.getCenterX();
        double ty = cy - bounds.getCenterY();

        AffineTransform traslado = AffineTransform.getTranslateInstance(tx, ty);
        Shape letraTransladada = traslado.createTransformedShape(shape);

        // Capa 1: contorno negro grueso (borde exterior)
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(letraTransladada);

        // Capa 2: borde blanco (halo interior)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(letraTransladada);

        // Capa 3: relleno amarillo
        g2.setColor(new Color(230, 175, 0)); // Amarillo UNO
        g2.fill(letraTransladada);
    }
}