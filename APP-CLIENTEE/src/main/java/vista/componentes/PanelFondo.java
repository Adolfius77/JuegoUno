package vista.componentes;

import vista.tema.Tema;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * Panel de fondo del juego.
 *
 * Antes hacia drawImage con el ancho y alto del panel, lo que deformaba la
 * imagen. Ahora la escala cubriendo el panel sin cambiar su proporcion (recorta
 * lo que sobra) y le pone un velo, porque encima va texto blanco que sobre un
 * fondo claro no se leia.
 *
 * Si no hay imagen, dibuja un degradado rojo en vez de un panel plano.
 */
public class PanelFondo extends JPanel {

    private Image imagenFondo;
    private float velo = 0.28f;

    public PanelFondo() {
        setOpaque(false);
    }

    public void setImagen(String rutaImagen) {
        if (rutaImagen == null) {
            this.imagenFondo = null;
        } else {
            java.net.URL url = getClass().getResource(rutaImagen);
            this.imagenFondo = url == null ? null : new ImageIcon(url).getImage();
        }
        repaint();
    }

    /** 0 = sin velo, 1 = negro total. */
    public void setVelo(float velo) {
        this.velo = Math.max(0f, Math.min(1f, velo));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();

        if (imagenFondo == null) {
            g2.setPaint(new GradientPaint(0, 0, Tema.ROJO, 0, h, Tema.ROJO_PROFUNDO));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
            return;
        }

        int iw = imagenFondo.getWidth(this);
        int ih = imagenFondo.getHeight(this);
        if (iw <= 0 || ih <= 0) {
            g2.dispose();
            return;
        }

        // Escala "cubrir": la imagen llena el panel sin deformarse.
        double escala = Math.max(w / (double) iw, h / (double) ih);
        int nw = (int) Math.ceil(iw * escala);
        int nh = (int) Math.ceil(ih * escala);
        g2.drawImage(imagenFondo, (w - nw) / 2, (h - nh) / 2, nw, nh, this);

        if (velo > 0f) {
            g2.setColor(new Color(0, 0, 0, (int) (velo * 255)));
            g2.fillRect(0, 0, w, h);
        }

        g2.dispose();
    }
}
