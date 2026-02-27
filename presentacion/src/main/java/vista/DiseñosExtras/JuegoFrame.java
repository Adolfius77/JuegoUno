/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.DiseñosExtras;

import javax.swing.JFrame;
import vista.DiseñosExtras.PanelFondo;

/**
 *
 * @author USER
 */
public class JuegoFrame extends JFrame{
    public JuegoFrame(){
        PanelFondo panelPrincipal = new PanelFondo();
        
        panelPrincipal.setImagen("/img/juegoUno (2).jpg");
        panelPrincipal.setLayout(null);
        this.setContentPane(panelPrincipal);
    }
}
