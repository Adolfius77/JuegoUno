package vista.DiseñosExtras;

import dtos.CartaDTO;

import javax.swing.*;
import java.awt.*;

public class PanelCarta extends JPanel {

    private CartaDTO carta;
    private boolean seleccionada = false;
    private int yOriginal = -1; // ← guardar posición original

    public PanelCarta(CartaDTO carta) {
        this.carta = carta;
        setPreferredSize(new Dimension(75, 110));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                boolean estabaSeleccionada = seleccionada;

                // Deseleccionar todas primero
                java.awt.Container parent = (java.awt.Container) getParent();
                if (parent != null) {
                    for (java.awt.Component c : parent.getComponents()) {
                        if (c instanceof PanelCarta) {
                            ((PanelCarta) c).deseleccionar();
                        }
                    }
                }

                // Si no estaba seleccionada, seleccionarla
                if (!estabaSeleccionada) {
                    seleccionar();
                }
            }
        });
    }

    public PanelCarta(int cantidad) {
        this.carta = null;
        this.esCarta = false; // no clickeable, no seleccionable
        setPreferredSize(new Dimension(75, 110));
        setOpaque(false);
    }

    private boolean esCarta = true;

    private void guardarYOriginal() {
        if (yOriginal == -1) {
            yOriginal = getBounds().y;
        }
    }

    public void seleccionar() {
        guardarYOriginal();
        seleccionada = true;
        setBounds(getBounds().x, yOriginal - 15, getWidth(), getHeight());
        repaint();
    }

    public void deseleccionar() {
        seleccionada = false;
        setBounds(getBounds().x, yOriginal, getWidth(), getHeight());
        repaint();
    }

    public void setSeleccionada(boolean sel) {
        if (sel) {
            seleccionar();
        } else {
            deseleccionar();
        }
    }

    public boolean isSeleccionada() {
        return seleccionada;
    }

    public CartaDTO getCarta() {
        return carta;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        if (!esCarta) {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, ancho, alto, 15, 15);

            g2.setColor(new Color(180, 0, 0)); // rojo oscuro
            g2.fillRoundRect(5, 5, ancho - 10, alto - 10, 10, 10);

            g2.setColor(new Color(140, 0, 0));
            g2.setStroke(new BasicStroke(1.5f));
            for (int y = -alto; y < alto * 2; y += 12) {
                for (int x = -ancho; x < ancho * 2; x += 12) {
                    g2.drawLine(x, y, x + 10, y + 10);
                    g2.drawLine(x + 10, y, x, y + 10);
                }
            }

            g2.setColor(Color.BLACK);
            g2.fillOval(ancho / 2 - 18, alto / 2 - 28, 36, 56);
            g2.setColor(new Color(180, 0, 0));
            g2.fillOval(ancho / 2 - 14, alto / 2 - 24, 28, 48);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("UNO", (ancho - fm.stringWidth("UNO")) / 2, alto / 2 + 4);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, ancho - 3, alto - 3, 15, 15);
            return;
        }

        if (carta == null) {
            return;
        }

        java.awt.Color colorFondo = obtenerColorSwing(carta.getColor());

        if (seleccionada) {
            g2.setColor(new java.awt.Color(255, 215, 0));
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(1, 1, ancho - 3, alto - 3, 15, 15);
            g2.setStroke(new BasicStroke(1));
        }

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, ancho, alto, 15, 15);

        g2.setColor(colorFondo);
        g2.fillRoundRect(5, 5, ancho - 10, alto - 10, 10, 10);

        Graphics2D g2Oval = (Graphics2D) g2.create();
        g2Oval.translate(ancho / 2.0, alto / 2.0);
        g2Oval.rotate(Math.toRadians(-25));
        g2Oval.setColor(Color.WHITE);
        g2Oval.fillOval(-ancho / 2 + 8, -alto / 4 - 5, ancho - 16, alto / 2 + 10);
        g2Oval.dispose();

        String texto = obtenerTextoCarta();

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(texto, 9, 22);

        Graphics2D g2Esquina = (Graphics2D) g2.create();
        g2Esquina.translate(ancho, alto);
        g2Esquina.rotate(Math.toRadians(180));
        g2Esquina.drawString(texto, 9, 22);
        g2Esquina.dispose();

        int tamanoFuente = texto.length() > 1 ? 32 : 44;
        g2.setFont(new Font("Arial", Font.BOLD, tamanoFuente));

        FontMetrics fm = g2.getFontMetrics();
        int xCentro = (ancho - fm.stringWidth(texto)) / 2;
        int yCentro = ((alto - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(Color.BLACK);
        g2.drawString(texto, xCentro + 2, yCentro + 2);

        if (colorFondo.equals(Color.BLACK)) {
            g2.setColor(new Color(40, 40, 40));
        } else {
            g2.setColor(colorFondo);
        }
        g2.drawString(texto, xCentro, yCentro);
    }

    /**
     * Evalúa el valor en texto que trae el DTO y lo convierte en el símbolo
     * visual.
     */
    private String obtenerTextoCarta() {
        String valor = carta.getValor();
        if (valor == null) {
            return "";
        }

        // Ya no necesitamos instanceof, evaluamos directamente el String
        switch (valor) {
            case "SALTAR":
                return "Ø";
            case "REVERSA":
                return "⇄";
            case "MAS_2":
                return "+2";
            case "MAS_4":
                return "+4";
            case "CAMBIO_COLOR":
                return "W";
            default:
                return valor; // Si es un número del "0" al "9", lo devuelve tal cual
        }
    }

    /**
     * Evalúa el color en texto que trae el DTO y lo convierte a un Color de
     * Java AWT.
     */
    private java.awt.Color obtenerColorSwing(String colorStr) {
        // Protegemos contra nulos o comodines sin color
        if (colorStr == null || colorStr.equals("SIN_COLOR")) {
            return java.awt.Color.BLACK;
        }

        switch (colorStr) {
            case "ROJO":
                return new java.awt.Color(237, 28, 36);
            case "AZUL":
                return new java.awt.Color(0, 114, 188);
            case "VERDE":
                return new java.awt.Color(80, 170, 68);
            case "AMARILLO":
                return new java.awt.Color(255, 204, 0);
            case "NEGRO":
                return new java.awt.Color(0, 0, 0);
            default:
                return java.awt.Color.BLACK;
        }
    }
}
