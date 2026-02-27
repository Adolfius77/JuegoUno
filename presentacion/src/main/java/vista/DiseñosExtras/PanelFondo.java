/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.DiseñosExtras;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author USER
 */
public class PanelFondo extends JPanel{
    private Image imagenFondo;
    
    public PanelFondo(){
        this.setOpaque(false);
    }
    public void setImagen(String rutaImagen){
      if(rutaImagen != null){
          this.imagenFondo = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
          this.repaint();
      }  
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
}
