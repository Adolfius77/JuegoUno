package vista.DiseñosExtras;

import dtos.CartaDTO;

import javax.swing.*;
import java.awt.*;

public class PanelCarta extends JPanel {

    private CartaDTO carta;

    public PanelCarta(CartaDTO carta) {
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
        
        java.awt.Color colorFondo = obtenerColorSwing(carta.getColor());

        // Borde exterior blanco de la carta
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, ancho, alto, 15, 15);

        // Rectangulo interior de color
        g2.setColor(colorFondo);
        g2.fillRoundRect(5, 5, ancho - 10, alto - 10, 10, 10);

        // Ovalo blanco inclinado en el centro
        Graphics2D g2Oval = (Graphics2D) g2.create();
        g2Oval.translate(ancho / 2.0, alto / 2.0); 
        g2Oval.rotate(Math.toRadians(-25));
        g2Oval.setColor(Color.WHITE);
        
        g2Oval.fillOval(-ancho / 2 + 8, -alto / 4 - 5, ancho - 16, alto / 2 + 10);
        g2Oval.dispose();

        // Obtener el texto de la carta basado en el DTO
        String texto = obtenerTextoCarta();

        // Dibujar números pequeños en las esquinas
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Esquina superior izquierda
        g2.drawString(texto, 9, 22);

        Graphics2D g2Esquina = (Graphics2D) g2.create();
        g2Esquina.translate(ancho, alto);
        g2Esquina.rotate(Math.toRadians(180));
        g2Esquina.drawString(texto, 9, 22);
        g2Esquina.dispose();

        // Dibujar texto GIGANTE en el centro
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

    /**
     * Evalúa el valor en texto que trae el DTO y lo convierte en el símbolo visual.
     */
    private String obtenerTextoCarta() {
        String valor = carta.getValor();
        if (valor == null) return "";

        // Ya no necesitamos instanceof, evaluamos directamente el String
        switch (valor) {
            case "SALTAR": return "Ø"; 
            case "REVERSA": return "⇄";
            case "MAS_2": return "+2";
            case "MAS_4": return "+4";
            case "CAMBIO_COLOR": return "W";
            default: return valor; // Si es un número del "0" al "9", lo devuelve tal cual
        }
    }

    /**
     * Evalúa el color en texto que trae el DTO y lo convierte a un Color de Java AWT.
     */
    private java.awt.Color obtenerColorSwing(String colorStr) {
        // Protegemos contra nulos o comodines sin color
        if (colorStr == null || colorStr.equals("SIN_COLOR")) {
            return java.awt.Color.BLACK; 
        }

        switch (colorStr) {
            case "ROJO": return new java.awt.Color(237, 28, 36);   
            case "AZUL": return new java.awt.Color(0, 114, 188);   
            case "VERDE": return new java.awt.Color(80, 170, 68);  
            case "AMARILLO": return new java.awt.Color(255, 204, 0); 
            case "NEGRO": return new java.awt.Color(0,0,0); 
            default: return java.awt.Color.BLACK;
        }
    }
}