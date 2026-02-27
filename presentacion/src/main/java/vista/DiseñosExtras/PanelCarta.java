package vista.DiseñosExtras;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import modelo.Carta;
import modelo.CartaAccion;
import modelo.CartaNumerica;
import modelo.cartaComodin;
import modelo.enums.TipoAccion;

public class PanelCarta extends JPanel {

    private Carta carta;

    public PanelCarta(Carta carta) {
        this.carta = carta;
        setPreferredSize(new Dimension(75, 110)); 
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (carta == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();
        Color colorFondo = obtenerColorSwing(carta.getColor());

        //Borde exterior blanco de la carta
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, ancho, alto, 15, 15);

        //Rectangulo interior de color
        g2.setColor(colorFondo);
        g2.fillRoundRect(5, 5, ancho - 10, alto - 10, 10, 10);

        //ovalo blanco inclinado en el centro
        Graphics2D g2Oval = (Graphics2D) g2.create();
        g2Oval.translate(ancho / 2.0, alto / 2.0); 
        g2Oval.rotate(Math.toRadians(-25));
        g2Oval.setColor(Color.WHITE);
        
        g2Oval.fillOval(-ancho / 2 + 8, -alto / 4 - 5, ancho - 16, alto / 2 + 10);
        g2Oval.dispose();

        //Obtener el texto de la carta 
        String texto = obtenerTextoCarta();

        //Dibujar números pequeños en las esquinas
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        // Esquina superior izquierda
        g2.drawString(texto, 9, 22);

       
        Graphics2D g2Esquina = (Graphics2D) g2.create();
        g2Esquina.translate(ancho, alto);
        g2Esquina.rotate(Math.toRadians(180));
        g2Esquina.drawString(texto, 9, 22);
        g2Esquina.dispose();

        //Dibujar texto GIGANTE en el centro
        int tamanoFuente = texto.length() > 1 ? 32 : 44; // Si es "+4" o "+2" hacemos la letra mas chica
        g2.setFont(new Font("Arial", Font.BOLD, tamanoFuente));
        
        FontMetrics fm = g2.getFontMetrics();
        int xCentro = (ancho - fm.stringWidth(texto)) / 2;
        int yCentro = ((alto - fm.getHeight()) / 2) + fm.getAscent();

        // Sombra del texto central 
        g2.setColor(Color.BLACK);
        g2.drawString(texto, xCentro + 2, yCentro + 2);

        // Texto central 
        if (colorFondo.equals(Color.BLACK)) {
            g2.setColor(new Color(40, 40, 40)); 
        } else {
            g2.setColor(colorFondo);
        }
        g2.drawString(texto, xCentro, yCentro);
    }

    private String obtenerTextoCarta() {
        if (carta instanceof CartaNumerica) {
            return String.valueOf(((CartaNumerica) carta).getNumero());
        }
        if (carta instanceof CartaAccion) {
            TipoAccion accion = ((CartaAccion) carta).getAccion();
            switch (accion) {
                case SALTAR: return "Ø"; 
                case REVERSA: return "⇄";
                case MAS_2: return "+2";
            }
        }
        if (carta instanceof cartaComodin) {
            if (((cartaComodin) carta).isTomarCuatro()) {
                return "+4";
            }
            return "W";
        }
        return "";
    }

    private java.awt.Color obtenerColorSwing(modelo.enums.Color color) {
        if (color == null) return java.awt.Color.BLACK; // Para los comodines

        switch (color) {
            case ROJO: return new java.awt.Color(237, 28, 36);   // Rojo
            case AZUL: return new java.awt.Color(0, 114, 188);   // Azul
            case VERDE: return new java.awt.Color(80, 170, 68);  // Verde
            case AMARILLO: return new java.awt.Color(255, 204, 0); // Amarillo
            case NEGRO: return new java.awt.Color(0,0,0); //negro
        }
        return java.awt.Color.BLACK;
    }
}