/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import Interfaces.IVista;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import vista.DiseñosExtras.PanelCarta;

/**
 *
 * @author USER
 */
public class GameView extends javax.swing.JFrame implements IVista {

    private JugadorDTO jugadorLocal;
    private dtos.PartidaDTO partidaDTO;

    /**
     * Creates new form GameView
     */
    public GameView() {
        initComponents();
        panelFondo.setImagen("/img/juegoUno (2).jpg");
        panelJugadorPrincipal.setLayout(new FlowLayout(FlowLayout.CENTER, -20, 10));
        setLocationRelativeTo(null);
    }

    public GameView(JugadorDTO jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
        initComponents();
        panelFondo.setImagen("/img/juegoUno (2).jpg");

        panelJugadorPrincipal.setLayout(null);
        jScrollPane1.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane1.setBorder(null);

        panelPilaCartas.setLayout(new BorderLayout());
        panelPilaCartas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelPilaCartas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                robarCarta();
            }
        });

        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);
        panelJugadorPrincipal.setOpaque(false);
        panelJugador2.setOpaque(false);
        panelJugador3.setOpaque(false);
        panelJugador4.setOpaque(false);
        panelNumeroCartas1.setOpaque(false);
        panelNumeroCartas2.setOpaque(false);
        panelNumeroCartas3.setOpaque(false);
        panelNumeroCartas4.setOpaque(false);
        panelCartaMedio.setOpaque(false);
        panelPilaCartas.setOpaque(false);
        panelAvatar1.setOpaque(false);
        panelAvatar2.setOpaque(false);
        panelAvatar3.setOpaque(false);
        panelAvatar4.setOpaque(false);
        panelNumeroCartas1.setOpaque(false);
        panelNumeroCartas2.setOpaque(false);
        panelNumeroCartas3.setOpaque(false);
        panelNumeroCartas4.setOpaque(false);
        panelJugadorPrincipal.setOpaque(false);
        setLocationRelativeTo(null);
    }

    private void robarCarta() {
        try {
            dtos.MensajeDTO msg = new dtos.MensajeDTO("ROBAR_CARTA", "CLIENTE");
            msg.getDatos().put("codigoSala", red.ClienteControlador.getInstance().getCodigoSala()); // ← agregar
            red.ClienteControlador.getInstance().enviarMensaje(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ImageIcon cargarAvatar(String avatarId) {
        if (avatarId == null || avatarId.isBlank()) {
            java.net.URL recurso = getClass().getResource("/img/pfp.png");
            return recurso != null ? new ImageIcon(recurso) : new ImageIcon();
        }
        String ruta = "/img/" + avatarId + ".png";
        java.net.URL recurso = getClass().getResource(ruta);
        return recurso != null ? new ImageIcon(recurso) : new ImageIcon();
    }

    private void mostrarAvatar(javax.swing.JPanel panel, String avatarId) {
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        javax.swing.JLabel lbl = new javax.swing.JLabel(cargarAvatar(avatarId));
        lbl.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        panel.add(lbl, java.awt.BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    public void inicializarPartida(dtos.PartidaDTO partida) {
        this.partidaDTO = partida;

        System.out.println("[GameView] Jugadores en partida: "
                + (partida.getJugadores() != null ? partida.getJugadores().size() : "null"));

        if (partida.getJugadores() != null) {
            for (dtos.JugadorDTO j : partida.getJugadores()) {
                System.out.println("[GameView] Jugador: " + j.getNombre()
                        + " | Mano: " + (j.getMano() != null ? j.getMano().getCartas().size() + " cartas" : "null"));

                if (j.getNombre().equals(jugadorLocal.getNombre())) {
                    if (j.getMano() != null && j.getMano().getCartas() != null) {
                        System.out.println("[GameView] Mostrando " + j.getMano().getCartas().size() + " cartas");
                        mostrarCartas(j.getMano().getCartas());
                    }
                    break;
                }

            }
        }

        mostrarAvatar(panelAvatar1, jugadorLocal.getAvatar());

        javax.swing.JPanel[] panelesAvatar = {panelAvatar2, panelAvatar3, panelAvatar4};
        int avatarIdx = 0;
        for (dtos.JugadorDTO j : partida.getJugadores()) {
            if (!j.getNombre().equals(jugadorLocal.getNombre()) && avatarIdx < panelesAvatar.length) {
                mostrarAvatar(panelesAvatar[avatarIdx], null); // pfp genérico
                avatarIdx++;
            }
        }

        if (partida.getJugadores() != null) {
            for (dtos.JugadorDTO j : partida.getJugadores()) {
                if (j.getNombre().equals(jugadorLocal.getNombre())) {
                    int cantidad = (j.getMano() != null && j.getMano().getCartas() != null)
                            ? j.getMano().getCartas().size() : 0;
                    NumeroDeCartasForm numLocal = new NumeroDeCartasForm();
                    numLocal.setNumero(cantidad);
                    panelNumeroCartas1.removeAll();
                    panelNumeroCartas1.setLayout(new BorderLayout());
                    panelNumeroCartas1.add(numLocal, BorderLayout.CENTER);
                    panelNumeroCartas1.revalidate();
                    panelNumeroCartas1.repaint();
                    break;
                }
            }

            javax.swing.JPanel[] panelesNum = {panelNumeroCartas2, panelNumeroCartas3, panelNumeroCartas4};
            int numIdx = 0;
            for (dtos.JugadorDTO j : partida.getJugadores()) {
                if (!j.getNombre().equals(jugadorLocal.getNombre()) && numIdx < panelesNum.length) {
                    int cantidad = (j.getMano() != null && j.getMano().getCartas() != null)
                            ? j.getMano().getCartas().size() : 0;
                    NumeroDeCartasForm numForm = new NumeroDeCartasForm();
                    numForm.setNumero(cantidad);
                    panelesNum[numIdx].removeAll();
                    panelesNum[numIdx].setLayout(new BorderLayout());
                    panelesNum[numIdx].add(numForm, BorderLayout.CENTER);
                    panelesNum[numIdx].revalidate();
                    panelesNum[numIdx].repaint();
                    numIdx++;
                }
            }
        }

        if (partida.getCartaCentro() != null) {
            System.out.println("[GameView] Carta centro: "
                    + partida.getCartaCentro().getColor() + " " + partida.getCartaCentro().getValor());
            actualizar(partida.getCartaCentro());
        }

        actualizarMazo(partida.getCartasRestantesMazo());
    }

    public void mostrarCartas(List<CartaDTO> manoDelServidor) {
        panelJugadorPrincipal.removeAll();
        int cantidadCartas = manoDelServidor.size();
        if (cantidadCartas == 0) {
            return;
        }

        int anchoPanel = jScrollPane1.getWidth() - 10;
        int altoPanel = jScrollPane1.getHeight() - 10;
        int anchoCarta = 75;
        int altoCarta = 110;

        int separacion;
        if (cantidadCartas == 1) {
            separacion = 0;
        } else {
            separacion = Math.min(55, (anchoPanel - anchoCarta) / (cantidadCartas - 1));
        }

        int anchoTotal = anchoCarta + (separacion * (cantidadCartas - 1));
        int xInicio = Math.max(0, (anchoPanel - anchoTotal) / 2);
        int yInicio = (altoPanel - altoCarta) / 2;

        panelJugadorPrincipal.setPreferredSize(new Dimension(anchoPanel, altoPanel));

        for (int i = 0; i < cantidadCartas; i++) {
            PanelCarta cartaVisual = new PanelCarta(manoDelServidor.get(i));
            int posX = xInicio + (i * separacion);
            cartaVisual.setBounds(posX, yInicio, anchoCarta, altoCarta);
            panelJugadorPrincipal.add(cartaVisual);
        }

        javax.swing.JPanel[] panelesOtros = {panelJugador2, panelJugador3, panelJugador4};
        int panelIdx = 0;

        for (dtos.JugadorDTO j : partidaDTO.getJugadores()) {
            if (!j.getNombre().equals(jugadorLocal.getNombre()) && panelIdx < panelesOtros.length) {
                int cantidad = (j.getMano() != null && j.getMano().getCartas() != null)
                        ? j.getMano().getCartas().size() : 0;
                mostrarCartasReverso(panelesOtros[panelIdx], cantidad);
                panelIdx++;
            }
        }

        panelJugadorPrincipal.revalidate();
        panelJugadorPrincipal.repaint();
    }

    public void actualizar(CartaDTO cartaActual) {
        panelCartaMedio.removeAll();

        if (cartaActual != null) {
            PanelCarta cartaVisual = new PanelCarta(cartaActual);
            panelCartaMedio.add(cartaVisual, BorderLayout.CENTER);
        }
        panelCartaMedio.revalidate();
        panelCartaMedio.repaint();

    }

    public void actualizarMazo(int cantidadCartas) {
        panelPilaCartas.removeAll();
        PanelCarta reverso = new PanelCarta(0);
        reverso.setBounds(0, 0, 80, 110);
        panelPilaCartas.setLayout(null);
        panelPilaCartas.add(reverso);
        panelPilaCartas.revalidate();
        panelPilaCartas.repaint();
    }

    public CartaDTO obtenerCartaSeleccionada() {
        for (Component c : panelJugadorPrincipal.getComponents()) {
            if (c instanceof PanelCarta) {
                PanelCarta panel = (PanelCarta) c;
                if (panel.isSeleccionada()) {
                    return panel.getCarta();
                }
            }
        }
        return null;
    }

    private void enviarCartaAlServidor(CartaDTO carta, String colorElegido) {
        try {
            dtos.MensajeDTO msg = new dtos.MensajeDTO("JUGAR_CARTA", "CLIENTE");
            msg.getDatos().put("color", carta.getColor());
            msg.getDatos().put("valor", carta.getValor());
            msg.getDatos().put("codigoSala", red.ClienteControlador.getInstance().getCodigoSala()); // ← agregar
            if (colorElegido != null) {
                msg.getDatos().put("colorElegido", colorElegido);
            }
            red.ClienteControlador.getInstance().enviarMensaje(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarSelectorColor(CartaDTO carta) {
        String[] colores = {"ROJO", "AZUL", "VERDE", "AMARILLO"};
        String[] etiquetas = {"Rojo", "Azul", "Verde", "Amarillo"};

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Elige un color:",
                "Color del comodín",
                JOptionPane.PLAIN_MESSAGE,
                null,
                etiquetas,
                etiquetas[0]
        );

        if (seleccion == null) {
            return;
        }

        String colorElegido = colores[java.util.Arrays.asList(etiquetas).indexOf(seleccion)];
        enviarCartaAlServidor(carta, colorElegido);
    }

    public void mostrarCartasReverso(javax.swing.JPanel panel, int cantidad) {
        panel.removeAll();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, -15, 5));
        panel.setOpaque(false);

        for (int i = 0; i < cantidad; i++) {
            PanelCarta reverso = new PanelCarta(0);
            reverso.setPreferredSize(new Dimension(75, 110));
            panel.add(reverso);
        }
        panel.revalidate();
        panel.repaint();
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
        panelFondo = new vista.DiseñosExtras.PanelFondo();
        jScrollPane1 = new javax.swing.JScrollPane();
        panelJugadorPrincipal = new javax.swing.JPanel();
        btnJugarCarta = new vista.DiseñosExtras.botonCircular();
        panelAvatar1 = new javax.swing.JPanel();
        panelNumeroCartas1 = new javax.swing.JPanel();
        panelJugador2 = new javax.swing.JPanel();
        panelAvatar2 = new javax.swing.JPanel();
        panelNumeroCartas2 = new javax.swing.JPanel();
        panelJugador4 = new javax.swing.JPanel();
        panelAvatar4 = new javax.swing.JPanel();
        panelNumeroCartas4 = new javax.swing.JPanel();
        panelJugador3 = new javax.swing.JPanel();
        panelNumeroCartas3 = new javax.swing.JPanel();
        panelAvatar3 = new javax.swing.JPanel();
        panelCartaMedio = new javax.swing.JPanel();
        panelPilaCartas = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelFondo.setBackground(new java.awt.Color(255, 255, 255));
        panelFondo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelJugadorPrincipal.setBackground(new java.awt.Color(255, 255, 255));
        panelJugadorPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelJugadorPrincipal.setOpaque(false);
        panelJugadorPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jScrollPane1.setViewportView(panelJugadorPrincipal);

        panelFondo.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 590, 570, 100));

        btnJugarCarta.setForeground(new java.awt.Color(255, 255, 255));
        btnJugarCarta.setText("Jugar Carta");
        btnJugarCarta.setBorderColor(new java.awt.Color(255, 255, 255));
        btnJugarCarta.setColor(new java.awt.Color(30, 136, 56));
        btnJugarCarta.setColorClick(new java.awt.Color(30, 136, 56));
        btnJugarCarta.setColorOver(new java.awt.Color(30, 136, 56));
        btnJugarCarta.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        btnJugarCarta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJugarCartaActionPerformed(evt);
            }
        });
        panelFondo.add(btnJugarCarta, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 710, 140, -1));

        panelAvatar1.setBackground(new java.awt.Color(255, 255, 255));
        panelAvatar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAvatar1.setOpaque(false);

        javax.swing.GroupLayout panelAvatar1Layout = new javax.swing.GroupLayout(panelAvatar1);
        panelAvatar1.setLayout(panelAvatar1Layout);
        panelAvatar1Layout.setHorizontalGroup(
            panelAvatar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );
        panelAvatar1Layout.setVerticalGroup(
            panelAvatar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 570, 110, 120));

        panelNumeroCartas1.setBackground(new java.awt.Color(255, 255, 255));
        panelNumeroCartas1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelNumeroCartas1.setOpaque(false);

        javax.swing.GroupLayout panelNumeroCartas1Layout = new javax.swing.GroupLayout(panelNumeroCartas1);
        panelNumeroCartas1.setLayout(panelNumeroCartas1Layout);
        panelNumeroCartas1Layout.setHorizontalGroup(
            panelNumeroCartas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );
        panelNumeroCartas1Layout.setVerticalGroup(
            panelNumeroCartas1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );

        panelFondo.add(panelNumeroCartas1, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 670, 50, 80));

        panelJugador2.setBackground(new java.awt.Color(255, 255, 255));
        panelJugador2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelJugador2.setOpaque(false);

        javax.swing.GroupLayout panelJugador2Layout = new javax.swing.GroupLayout(panelJugador2);
        panelJugador2.setLayout(panelJugador2Layout);
        panelJugador2Layout.setHorizontalGroup(
            panelJugador2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );
        panelJugador2Layout.setVerticalGroup(
            panelJugador2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        panelFondo.add(panelJugador2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, 410, 90));

        panelAvatar2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAvatar2.setOpaque(false);

        javax.swing.GroupLayout panelAvatar2Layout = new javax.swing.GroupLayout(panelAvatar2);
        panelAvatar2.setLayout(panelAvatar2Layout);
        panelAvatar2Layout.setHorizontalGroup(
            panelAvatar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panelAvatar2Layout.setVerticalGroup(
            panelAvatar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));

        panelNumeroCartas2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelNumeroCartas2Layout = new javax.swing.GroupLayout(panelNumeroCartas2);
        panelNumeroCartas2.setLayout(panelNumeroCartas2Layout);
        panelNumeroCartas2Layout.setHorizontalGroup(
            panelNumeroCartas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );
        panelNumeroCartas2Layout.setVerticalGroup(
            panelNumeroCartas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 68, Short.MAX_VALUE)
        );

        panelFondo.add(panelNumeroCartas2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 50, 70));

        panelJugador4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelJugador4Layout = new javax.swing.GroupLayout(panelJugador4);
        panelJugador4.setLayout(panelJugador4Layout);
        panelJugador4Layout.setHorizontalGroup(
            panelJugador4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
        panelJugador4Layout.setVerticalGroup(
            panelJugador4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );

        panelFondo.add(panelJugador4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, 500, 110));

        panelAvatar4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelAvatar4Layout = new javax.swing.GroupLayout(panelAvatar4);
        panelAvatar4.setLayout(panelAvatar4Layout);
        panelAvatar4Layout.setHorizontalGroup(
            panelAvatar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );
        panelAvatar4Layout.setVerticalGroup(
            panelAvatar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, 110, 120));

        panelNumeroCartas4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelNumeroCartas4Layout = new javax.swing.GroupLayout(panelNumeroCartas4);
        panelNumeroCartas4.setLayout(panelNumeroCartas4Layout);
        panelNumeroCartas4Layout.setHorizontalGroup(
            panelNumeroCartas4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );
        panelNumeroCartas4Layout.setVerticalGroup(
            panelNumeroCartas4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        panelFondo.add(panelNumeroCartas4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 130, 60, 90));

        panelJugador3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelJugador3Layout = new javax.swing.GroupLayout(panelJugador3);
        panelJugador3.setLayout(panelJugador3Layout);
        panelJugador3Layout.setHorizontalGroup(
            panelJugador3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        panelJugador3Layout.setVerticalGroup(
            panelJugador3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 98, Short.MAX_VALUE)
        );

        panelFondo.add(panelJugador3, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 290, 400, 100));

        panelNumeroCartas3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelNumeroCartas3Layout = new javax.swing.GroupLayout(panelNumeroCartas3);
        panelNumeroCartas3.setLayout(panelNumeroCartas3Layout);
        panelNumeroCartas3Layout.setHorizontalGroup(
            panelNumeroCartas3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );
        panelNumeroCartas3Layout.setVerticalGroup(
            panelNumeroCartas3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );

        panelFondo.add(panelNumeroCartas3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 400, 60, 80));

        panelAvatar3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelAvatar3Layout = new javax.swing.GroupLayout(panelAvatar3);
        panelAvatar3.setLayout(panelAvatar3Layout);
        panelAvatar3Layout.setHorizontalGroup(
            panelAvatar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        panelAvatar3Layout.setVerticalGroup(
            panelAvatar3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 150, 140, 130));

        panelCartaMedio.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelCartaMedio.setLayout(new java.awt.BorderLayout());
        panelFondo.add(panelCartaMedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 70, 110));

        panelPilaCartas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelPilaCartas.setLayout(new java.awt.BorderLayout());
        panelFondo.add(panelPilaCartas, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 300, 80, 110));

        getContentPane().add(panelFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 790));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnJugarCartaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJugarCartaActionPerformed
        CartaDTO cartaSeleccionada = obtenerCartaSeleccionada();
        if (cartaSeleccionada == null) {
            return;
        }

        if ("MAS_4".equals(cartaSeleccionada.getValor())
                || "CAMBIO_COLOR".equals(cartaSeleccionada.getValor())) {
            mostrarSelectorColor(cartaSeleccionada);
            return;
        }

        enviarCartaAlServidor(cartaSeleccionada, null);
    }//GEN-LAST:event_btnJugarCartaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vista.DiseñosExtras.botonCircular btnJugarCarta;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelAvatar1;
    private javax.swing.JPanel panelAvatar2;
    private javax.swing.JPanel panelAvatar3;
    private javax.swing.JPanel panelAvatar4;
    private javax.swing.JPanel panelCartaMedio;
    private vista.DiseñosExtras.PanelFondo panelFondo;
    private javax.swing.JPanel panelJugador2;
    private javax.swing.JPanel panelJugador3;
    private javax.swing.JPanel panelJugador4;
    private javax.swing.JPanel panelJugadorPrincipal;
    private javax.swing.JPanel panelNumeroCartas1;
    private javax.swing.JPanel panelNumeroCartas2;
    private javax.swing.JPanel panelNumeroCartas3;
    private javax.swing.JPanel panelNumeroCartas4;
    private javax.swing.JPanel panelPilaCartas;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mostrarVista() {
        this.setVisible(true);
    }

    @Override
    public void cerrarVista() {
        this.dispose();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    @Override
    public void actualizar(String evento) {
        System.out.println("evento recibido en gameView: " + evento);
        if ("ACTUALIZACION_PARTIDA".equals(evento)) {

        } else if (evento != null && evento.startsWith("PARTIDA_TERMINADA:")) {
            String ganador = evento.split(":")[1];

            java.util.List<dtos.JugadorDTO> ordenados = new java.util.ArrayList<>();
            if (partidaDTO != null && partidaDTO.getJugadores() != null) {
                ordenados = new java.util.ArrayList<>(partidaDTO.getJugadores());
                ordenados.sort((a, b) -> {
                    int cartasA = (a.getMano() != null && a.getMano().getCartas() != null)
                            ? a.getMano().getCartas().size() : 0;
                    int cartasB = (b.getMano() != null && b.getMano().getCartas() != null)
                            ? b.getMano().getCartas().size() : 0;
                    return Integer.compare(cartasA, cartasB);
                });
            }

            java.util.List<String> nombresOrdenados = new java.util.ArrayList<>();
            for (dtos.JugadorDTO j : ordenados) {
                nombresOrdenados.add(j.getNombre());
            }

            red.ClienteControlador.getInstance().setVistaActual(null);
            this.dispose();
            new podioView(ganador, nombresOrdenados).setVisible(true);
        }
    }
}
