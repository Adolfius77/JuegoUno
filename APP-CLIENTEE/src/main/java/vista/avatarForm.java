/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Dimension;

/**
 *
 * @author USER
 */
public class avatarForm extends javax.swing.JPanel {

    /**
     * Creates new form avatarForm
     */
    private String nombreUsuario;
    private String avatarUsuario;
    private boolean estaListoUsuario;
    private JLabel lblEstoyListo;

    public avatarForm() {
        initComponents();
        vista.tema.Tema.aplicar(this);
        configurarIndicadorListo();
    }

    public avatarForm(String nombreUsuario, String avatarUsuario) {
        this(nombreUsuario, avatarUsuario, false);
    }

    public avatarForm(String nombreUsuario, String avatarUsuario, boolean estaListoUsuario) {
        initComponents();
        vista.tema.Tema.aplicar(this);
        this.nombreUsuario = nombreUsuario;
        this.avatarUsuario = avatarUsuario;
        this.estaListoUsuario = estaListoUsuario;
        configurarIndicadorListo();
        mostrarDatosJugador();
    }

    private static final int LADO_AVATAR = 76;

    private ImageIcon cargarImagen(String avatarId) {
        if (avatarId != null && !avatarId.isBlank()
                && !avatarId.equals("no hay") && !avatarId.equals("pfp")) {
            try (java.io.InputStream is = getClass().getResourceAsStream("/img/" + avatarId + ".png")) {
                if (is != null) {
                    java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(is);
                    if (img != null) {
                        return new ImageIcon(recortarEnCirculo(img));
                    }
                }
            } catch (Exception ex) {
                System.out.println("[Avatar] No se pudo cargar '" + avatarId + "': " + ex.getMessage());
            }
        }
        // Antes se caia a /img/pfp.png, que es una imagen COMPLETAMENTE EN BLANCO:
        // por eso los jugadores sin avatar salian como cuadros vacios. Se dibuja
        // en su lugar la inicial sobre un color derivado del nombre.
        return new ImageIcon(avatarDeInicial(nombreUsuario));
    }

    /** Recorta la imagen en un circulo, para que todas se vean iguales. */
    private java.awt.image.BufferedImage recortarEnCirculo(java.awt.image.BufferedImage origen) {
        java.awt.image.BufferedImage salida = new java.awt.image.BufferedImage(
                LADO_AVATAR, LADO_AVATAR, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = salida.createGraphics();
        try {
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                    java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setColor(java.awt.Color.WHITE);
            g2.fillOval(0, 0, LADO_AVATAR, LADO_AVATAR);
            g2.setClip(new java.awt.geom.Ellipse2D.Float(2, 2, LADO_AVATAR - 4, LADO_AVATAR - 4));
            g2.drawImage(origen, 2, 2, LADO_AVATAR - 4, LADO_AVATAR - 4, null);
        } finally {
            g2.dispose();
        }
        return salida;
    }

    /** Circulo de color con la inicial: respaldo para quien no eligio avatar. */
    private java.awt.image.BufferedImage avatarDeInicial(String nombre) {
        String texto = (nombre == null || nombre.isBlank())
                ? "?" : nombre.trim().substring(0, 1).toUpperCase();

        java.awt.Color[] paleta = {
            vista.tema.Tema.ROJO, vista.tema.Tema.AZUL,
            vista.tema.Tema.VERDE, vista.tema.Tema.AMARILLO_OSCURO};
        java.awt.Color fondo = paleta[Math.abs((nombre == null ? 0 : nombre.hashCode())) % paleta.length];

        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                LADO_AVATAR, LADO_AVATAR, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        try {
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(java.awt.Color.WHITE);
            g2.fillOval(0, 0, LADO_AVATAR, LADO_AVATAR);
            g2.setColor(fondo);
            g2.fillOval(2, 2, LADO_AVATAR - 4, LADO_AVATAR - 4);

            g2.setColor(java.awt.Color.WHITE);
            g2.setFont(vista.tema.Tema.titulo(34));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto,
                    (LADO_AVATAR - fm.stringWidth(texto)) / 2,
                    (LADO_AVATAR - fm.getHeight()) / 2 + fm.getAscent());
        } finally {
            g2.dispose();
        }
        return img;
    }

    private ImageIcon cargarIconoEstado(boolean estaListo) {
        String ruta = estaListo ? "/img/palomita.png" : "/img/equis.png";
        java.net.URL recurso = getClass().getResource(ruta);
        return recurso != null ? new ImageIcon(recurso) : new ImageIcon();
    }

    private void configurarIndicadorListo() {

        jPanel2.removeAll();
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(lblNombre, BorderLayout.WEST);
        jPanel2.add(lblListo,BorderLayout.EAST);

        actualizarEstadoListo(estaListoUsuario);
        jPanel2.revalidate();
        jPanel2.repaint();
    }

    public void actualizarEstadoListo(boolean estaListo) {
        this.estaListoUsuario = estaListo;
        if (lblListo != null) {
            lblListo.setIcon(cargarIconoEstado(estaListo));
            lblListo.setText("");
        }
    }

    private void mostrarDatosJugador() {
        if (nombreUsuario != null && !nombreUsuario.isBlank()) {
            lblNombre.setText("  " + nombreUsuario);
            lblNombre.setFont(vista.tema.Tema.titulo(14));
            lblNombre.setForeground(vista.tema.Tema.TEXTO_CLARO);

            // La foto ya viene recortada en circulo; se agranda para que llene la
            // tarjeta, que antes era casi todo espacio blanco.
            ImageIcon iconoOriginal = cargarImagen(avatarUsuario);
            java.awt.Image imgEscalada = iconoOriginal.getImage()
                    .getScaledInstance(LADO_AVATAR, LADO_AVATAR, java.awt.Image.SCALE_SMOOTH);

            avatar.setIcon(new ImageIcon(imgEscalada));
            avatar.setText("");
            avatar.setHorizontalAlignment(SwingConstants.CENTER);
            avatar.setVerticalAlignment(SwingConstants.CENTER);
            avatar.setPreferredSize(new Dimension(LADO_AVATAR, LADO_AVATAR));
            avatar.setOpaque(false);
        }
        actualizarEstadoListo(estaListoUsuario);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        lblListo = new javax.swing.JLabel();
        avatar = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(51, 51, 255));
        jPanel2.setForeground(new java.awt.Color(51, 51, 255));

        lblNombre.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(255, 255, 255));
        lblNombre.setText("Nombre");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblListo)
                .addGap(16, 16, 16))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblNombre)
                        .addGap(0, 8, Short.MAX_VALUE))
                    .addComponent(lblListo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        avatar.setText("jLabel2");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(avatar, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(avatar, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel avatar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblListo;
    private javax.swing.JLabel lblNombre;
    // End of variables declaration//GEN-END:variables
}
