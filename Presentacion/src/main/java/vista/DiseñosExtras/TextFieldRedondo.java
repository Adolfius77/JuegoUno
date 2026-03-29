/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Un JTextField personalizado con esquinas redondeadas y un borde.
 * @author adoil (¡y Asistente de programación!)
 */
public class TextFieldRedondo extends JTextField {

    private int radius;

    public TextFieldRedondo() {
        super();
        this.radius = 20; // Radio por defecto

        // 1. Hacer el fondo transparente
        setOpaque(false);

        // 2. Establecer un borde vacío (padding)
        // (top, left, bottom, right)
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Damos colores por defecto
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }

    // --- Getter y Setter para el radio ---

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint(); // Volver a dibujar si cambia el radio
    }

    /**
     * Dibuja el fondo del componente.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        // Activar Antialiasing para bordes suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Dibujar el fondo redondeado (el relleno)
        g2.setColor(getBackground());
        g2.fillRoundRect(
                0, 0, // Posición X, Y
                getWidth(), // Ancho
                getHeight(), // Alto
                radius, radius // Radio de las esquinas
        );

        // 2. Llamar al método original para dibujar el texto, cursor, etc.
        super.paintComponent(g);
    }

    /**
     * Dibuja el borde del componente.
     * Este método se llama *después* de paintComponent.
     */
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // --- ¡AQUÍ ESTÁ EL CAMBIO! ---
        
        // 1. Establece el color del borde a negro
        g2.setColor(Color.BLACK); 
        
        // 2. Dibuja el contorno redondeado
        // Se resta 1 al ancho y alto para que el borde de 1px 
        // quede completamente dentro de los límites del componente.
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
    }
}