package vista.componentes;

import vista.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Campo de texto con esquinas redondeadas.
 *
 * Antes el borde era negro de 1px, siempre igual: no se veia cual campo tenia el
 * foco. Ahora el borde es gris suave y se tine con el color de acento al
 * enfocar, y admite un texto de sugerencia mientras esta vacio.
 */
public class TextFieldRedondo extends JTextField {

    private int radius = 999;
    private String sugerencia = "";

    public TextFieldRedondo() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        setBackground(Color.WHITE);
        setForeground(Tema.TEXTO);
        setCaretColor(Tema.TEXTO);
        setFont(Tema.cuerpo(15));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    /** Texto gris que se muestra mientras el campo esta vacio. */
    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia == null ? "" : sugerencia;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arco = Math.min(radius, getHeight());
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arco, arco));
        g2.dispose();

        super.paintComponent(g);

        if (getText().isEmpty() && !sugerencia.isEmpty()) {
            Graphics2D gs = (Graphics2D) g.create();
            gs.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gs.setColor(Tema.TEXTO_SUAVE);
            gs.setFont(getFont());
            FontMetrics fm = gs.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            gs.drawString(sugerencia, getInsets().left, y);
            gs.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arco = Math.min(radius, getHeight());
        boolean enfocado = isFocusOwner();
        g2.setStroke(new java.awt.BasicStroke(enfocado ? 2f : 1f));
        g2.setColor(enfocado ? Tema.AMARILLO_OSCURO : Tema.BORDE);
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2f, getHeight() - 2f, arco, arco));

        g2.dispose();
    }
}
