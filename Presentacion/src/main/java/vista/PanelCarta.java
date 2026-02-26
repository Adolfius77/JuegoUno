/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import java.awt.*;
import modelo.Carta;
import modelo.CartaAccion;
import modelo.CartaNumerica;
import modelo.cartaComodin;
import static modelo.enums.Color.AMARILLO;
import static modelo.enums.Color.AZUL;
import static modelo.enums.Color.ROJO;
import static modelo.enums.Color.VERDE;
import modelo.enums.TipoAccion;
/**
 *
 * @author santi
 */

public class PanelCarta extends JPanel {

    private Carta carta;

    public PanelCarta(Carta carta) {
        this.carta = carta;
        setPreferredSize(new Dimension(70, 100));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (carta == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int margen = 10;
        int ancho = getWidth() - 2 * margen;
        int alto = getHeight() - 2 * margen;

        g2.setColor(obtenerColorSwing(carta.getColor()));
        g2.fillRoundRect(margen, margen, ancho, alto, 40, 40);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(margen, margen, ancho, alto, 40, 40);

        String texto = obtenerTextoCarta();

        g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(texto)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 10;

        g2.drawString(texto, x, y);
    }

    private String obtenerTextoCarta() {

        if (carta instanceof CartaNumerica) {
            return String.valueOf(((CartaNumerica) carta).getNumero());
        }

        if (carta instanceof CartaAccion) {
            TipoAccion accion = ((CartaAccion) carta).getAccion();
            switch (accion) {
                case SALTAR: return "⛔";
                case REVERSA: return "↺";
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

    if (color == null) return java.awt.Color.BLACK;

    switch (color) {
        case ROJO -> {
            return java.awt.Color.RED;
        }
        case AZUL -> {
            return java.awt.Color.BLUE;
        }
        case VERDE -> {
            return java.awt.Color.GREEN;
        }
        case AMARILLO -> {
            return java.awt.Color.YELLOW;
        }
    }

    return java.awt.Color.BLACK;
}

}

