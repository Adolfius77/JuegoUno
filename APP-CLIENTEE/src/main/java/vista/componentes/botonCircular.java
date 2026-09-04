package vista.componentes;

import vista.tema.Tema;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Boton redondeado del juego.
 *
 * Antes era un relleno plano con un borde de 2px y no distinguia el estado
 * deshabilitado, que importa: btnJugarCarta arranca deshabilitado y no se
 * notaba. Ahora tiene sombra, degradado sutil y estados de hover, pulsado y
 * deshabilitado.
 */
public class botonCircular extends JButton {

    private boolean over;
    private boolean presionado;
    private Color color;
    private Color colorOver;
    private Color colorClick;
    private Color borderColor;
    private int radius;

    public botonCircular() {
        setColor(Tema.AMARILLO);
        colorOver = aclarar(Tema.AMARILLO, 0.10f);
        colorClick = oscurecer(Tema.AMARILLO, 0.12f);
        borderColor = new Color(0, 0, 0, 0);
        setRadius(999);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Tema.TEXTO);
        setFont(Tema.boton(15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                if (isEnabled()) {
                    over = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent me) {
                over = false;
                presionado = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if (isEnabled()) {
                    presionado = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                presionado = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arco = Math.min(radius, h);

        Color relleno = color;
        if (!isEnabled()) {
            relleno = new Color(0xD8, 0xDA, 0xDE);
        } else if (presionado) {
            relleno = colorClick;
        } else if (over) {
            relleno = colorOver;
        }

        // Sombra: se hunde al pulsar, para que el clic se sienta
        int desplaz = presionado ? 1 : 3;
        if (isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fill(new RoundRectangle2D.Float(1, desplaz, w - 2, h - desplaz, arco, arco));
        }

        int alturaCuerpo = h - desplaz;
        g2.setPaint(new GradientPaint(0, 0, aclarar(relleno, 0.07f), 0, alturaCuerpo, relleno));
        g2.fill(new RoundRectangle2D.Float(1, 0, w - 2, alturaCuerpo, arco, arco));

        if (borderColor.getAlpha() > 0) {
            g2.setColor(borderColor);
            g2.draw(new RoundRectangle2D.Float(1, 0, w - 3, alturaCuerpo - 1, arco, arco));
        }

        g2.dispose();

        Color textoOriginal = getForeground();
        if (!isEnabled()) {
            setForeground(new Color(0x8A, 0x8F, 0x98));
        }
        super.paintComponent(g);
        setForeground(textoOriginal);
    }

    // --- Getters y setters (los usa el editor de formularios) ---------------

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.colorOver = aclarar(color, 0.10f);
        this.colorClick = oscurecer(color, 0.12f);
        setBackground(color);
        repaint();
    }

    public Color getColorOver() {
        return colorOver;
    }

    public void setColorOver(Color colorOver) {
        this.colorOver = colorOver;
    }

    public Color getColorClick() {
        return colorClick;
    }

    public void setColorClick(Color colorClick) {
        this.colorClick = colorClick;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    private static Color aclarar(Color c, float f) {
        return new Color(
                Math.min(255, (int) (c.getRed() + 255 * f)),
                Math.min(255, (int) (c.getGreen() + 255 * f)),
                Math.min(255, (int) (c.getBlue() + 255 * f)));
    }

    private static Color oscurecer(Color c, float f) {
        return new Color(
                Math.max(0, (int) (c.getRed() * (1 - f))),
                Math.max(0, (int) (c.getGreen() * (1 - f))),
                Math.max(0, (int) (c.getBlue() * (1 - f))));
    }
}
