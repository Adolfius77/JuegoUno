/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

import controlador.GameController;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import vista.DiseñosExtras.CartaReversoUNO;
import vista.DiseñosExtras.PanelCarta;

/**
 *
 * @author USER
 */
public class TableroView extends javax.swing.JPanel {

    private GameController controlador;
    private List<CartaDTO> cartasMiMano = new ArrayList<>();
    private int indiceSeleccionado = -1;
    private boolean esMiTurnoActual = false;
    private int segundosRestantes = 30;
    private final Timer temporizadorTurno;
    private final JLabel lblTemporizador = new JLabel("Tiempo: 30s");
    private final Border bordeNormal = BorderFactory.createEmptyBorder();
    private final Border bordeSeleccionado = BorderFactory.createLineBorder(new Color(255, 215, 0), 3);
    private final JLabel lblIndicadorUno = new JLabel();

    /**
     * Creates new form TableroView
     */
    public TableroView() {
        initComponents();
        panelFondo.setImagen("/img/juegoUno (2).jpg");
        panelJugadorPrincipal.setLayout(new FlowLayout(FlowLayout.CENTER, -20, 10));
        btnJugarCarta.setEnabled(false);
        btnJugarCarta.addActionListener(evt -> jugarCartaSeleccionada());
        lblTemporizador.setForeground(Color.WHITE);
        lblTemporizador.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTemporizador.setHorizontalAlignment(SwingConstants.CENTER);
        panelFondo.add(lblTemporizador, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 250, 220, 30));

        lblIndicadorUno.setText("GRITAR UNO!");
        lblIndicadorUno.setForeground(new Color(255, 0, 0));
        lblIndicadorUno.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblIndicadorUno.setHorizontalAlignment(SwingConstants.CENTER);
        lblIndicadorUno.setVisible(false);
        panelFondo.add(lblIndicadorUno, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 290, 220, 30));
        
        temporizadorTurno = new Timer(1000, e -> actualizarCuentaRegresiva());
        btnDecirUno.setVisible(false);
    }

    public void setController(GameController controlador) {
        this.controlador = controlador;
    }

    public void actualizarTurno(String turnoJugadorId, String miNombre) {
        boolean turnoLocal = turnoJugadorId != null && miNombre != null && turnoJugadorId.equals(miNombre);
        this.esMiTurnoActual = turnoLocal;

        if (!turnoLocal) {
            temporizadorTurno.stop();
            segundosRestantes = 30;
            lblTemporizador.setText("Turno de: " + (turnoJugadorId != null && !turnoJugadorId.isBlank() ? turnoJugadorId : "?"));
            btnJugarCarta.setEnabled(false);
            return;
        }

        reiniciarTemporizador();
        actualizarEstadoBotonJugar();
    }

    public void mostrarCartas(List<CartaDTO> manoDelServidor) {
        this.cartasMiMano = manoDelServidor != null ? new ArrayList<>(manoDelServidor) : new ArrayList<>();
        this.indiceSeleccionado = -1;

        
        if (cartasMiMano.size() == 1) {
            lblIndicadorUno.setVisible(true);
            lblIndicadorUno.setText("¡GRITA UNO!");
            lblIndicadorUno.setForeground(Color.RED);
            btnDecirUno.setVisible(true);
        } else {
            lblIndicadorUno.setVisible(false);
            btnDecirUno.setVisible(false);
        }

        actualizarEstadoBotonJugar();
        panelJugadorPrincipal.removeAll();
        renderizarMano();

    }

    public CartaDTO getCartaSeleccionada() {
        if (indiceSeleccionado < 0 || indiceSeleccionado >= cartasMiMano.size()) {
            return null;
        }
        return cartasMiMano.get(indiceSeleccionado);
    }

    private void seleccionarCarta(int indice) {
        this.indiceSeleccionado = indice;
        actualizarEstadoBotonJugar();
        renderizarMano();
    }

    private void renderizarMano() {
        panelJugadorPrincipal.removeAll();
        if (cartasMiMano.isEmpty()) {
            panelJugadorPrincipal.revalidate();
            panelJugadorPrincipal.repaint();
            return;
        }

        for (int i = 0; i < cartasMiMano.size(); i++) {
            CartaDTO carta = cartasMiMano.get(i);
            boolean seleccionada = i == indiceSeleccionado;
            JPanel cartaVisual = crearCartaSeleccionable(carta, seleccionada, i);
            panelJugadorPrincipal.add(cartaVisual);
        }

        panelJugadorPrincipal.revalidate();
        panelJugadorPrincipal.repaint();
    }

    private JPanel crearCartaSeleccionable(CartaDTO carta, boolean seleccionada, int indice) {
        PanelCarta cartaVisual = new PanelCarta(carta);
        cartaVisual.setPreferredSize(new java.awt.Dimension(75, 110));
        cartaVisual.setBorder(seleccionada ? bordeSeleccionado : bordeNormal);
        cartaVisual.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarCarta(indice);
            }
        });
        return cartaVisual;
    }

    private void actualizarEstadoBotonJugar() {
        btnJugarCarta.setEnabled(esMiTurnoActual && indiceSeleccionado >= 0 && indiceSeleccionado < cartasMiMano.size());
    }

    private void reiniciarTemporizador() {
        segundosRestantes = 30;
        lblTemporizador.setText("Tiempo: 30s");
        actualizarColorTemporizador();
        if (!temporizadorTurno.isRunning()) {
            temporizadorTurno.start();
        }
    }

    private void actualizarCuentaRegresiva() {
        if (!esMiTurnoActual) {
            temporizadorTurno.stop();
            lblTemporizador.setForeground(Color.WHITE);
            return;
        }

        segundosRestantes--;
        if (segundosRestantes <= 0) {
            temporizadorTurno.stop();
            segundosRestantes = 30;
            lblTemporizador.setText("Tiempo: 0s");
            lblTemporizador.setForeground(Color.RED);
            if (controlador != null) {
                controlador.tomarCarta();
                controlador.pasarTurno();
            }
            return;
        }

        lblTemporizador.setText("Tiempo: " + segundosRestantes + "s");
        actualizarColorTemporizador();
    }

    private void actualizarColorTemporizador() {
        if (segundosRestantes <= 3) {
            lblTemporizador.setForeground(new Color(220, 20, 60));
        } else if (segundosRestantes <= 10) {
            lblTemporizador.setForeground(new Color(255, 140, 0));
        } else if (segundosRestantes <= 20) {
            lblTemporizador.setForeground(new Color(255, 215, 0));
        } else {
            lblTemporizador.setForeground(new Color(144, 238, 144));
        }
    }

    private void jugarCartaSeleccionada() {
        if (controlador == null) {
            return;
        }
        CartaDTO seleccionada = getCartaSeleccionada();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una carta primero.", "UNO", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        controlador.jugarCartaSeleccionada(seleccionada);
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
        if (cantidadCartas > 0) {
            CartaDTO reversoMazo = new CartaDTO("ROJO", "UNO");
            PanelCarta cartaVisual = new PanelCarta(reversoMazo);
            cartaVisual.setToolTipText("cartas restantes:" + cantidadCartas);

            cartaVisual.setCursor(new Cursor(Cursor.HAND_CURSOR));

            
            cartaVisual.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (controlador != null) {
                            if (esMiTurnoActual) {
 
                                controlador.tomarCarta();
                            } else {
                                JOptionPane.showMessageDialog(TableroView.this, "¡Espera tu turno para robar!", "UNO", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            });

            panelPilaCartas.add(cartaVisual, BorderLayout.CENTER);
        }
        panelPilaCartas.revalidate();
        panelPilaCartas.repaint();
    }

    public void actulizarRivales(List<JugadorDTO> jugadores, String miNombre) {
        JPanel[] panelesAvatares = {panelAvatar1, panelAvatar2, panelAvatar3, panelAvatar4};

        for (JPanel panel : panelesAvatares) {
            panel.removeAll();
            panel.setLayout(new BorderLayout());
            panel.setVisible(false);
        }

        JugadorDTO jugadorLocal = buscarJugador(jugadores, miNombre);
        if (jugadorLocal != null) {
            panelAvatar1.setVisible(true);
            panelAvatar1.add(new avatarForm(jugadorLocal.getNombre(), obtenerAvatarSeguro(jugadorLocal), jugadorLocal.isEstaListo()), BorderLayout.CENTER);
        }

        int indexPanel = 1;
        for (JugadorDTO j : obtenerRivales(jugadores, miNombre)) {
            if (indexPanel < panelesAvatares.length) {
                panelesAvatares[indexPanel].setVisible(true);
                panelesAvatares[indexPanel].add(new avatarForm(j.getNombre(), obtenerAvatarSeguro(j), j.isEstaListo()), BorderLayout.CENTER);
                indexPanel++;
            }
        }

        for (JPanel panel : panelesAvatares) {
            panel.revalidate();
            panel.repaint();
        }
    }

    public void actualizarPanelesNumeroCartas(List<JugadorDTO> jugadores, String miNombre) {
        JPanel[] panelNumero = {panelNumeroCartas1, panelNumeroCartas2, panelNumeroCartas3, panelNumeroCartas4};
        for (JPanel panel : panelNumero) {
            panel.removeAll();
            panel.setLayout(new BorderLayout());
            panel.setVisible(false);
        }
        JugadorDTO jugadorLocal = buscarJugador(jugadores, miNombre);
        if (jugadorLocal != null) {
            int cantidadLocal = (jugadorLocal.getMano() != null) ? jugadorLocal.getMano().getCartas().size() : 0;
            NumeroDeCartasForm numeroLocal = new NumeroDeCartasForm();
            numeroLocal.MostrarNumeroCartas(cantidadLocal);
            panelNumero[0].setVisible(true);
            panelNumero[0].add(numeroLocal, BorderLayout.CENTER);
        }

        int indexPanel = 1;
        for (JugadorDTO j : obtenerRivales(jugadores, miNombre)) {
            if (indexPanel < panelNumero.length) {
                int cantidad = (j.getMano() != null) ? j.getMano().getCartas().size() : 0;
                NumeroDeCartasForm numero = new NumeroDeCartasForm();
                numero.MostrarNumeroCartas(cantidad);
                panelNumero[indexPanel].setVisible(true);
                panelNumero[indexPanel].add(numero, BorderLayout.CENTER);
                indexPanel++;
            }
        }
        for (JPanel panel : panelNumero) {
            panel.revalidate();
            panel.repaint();
        }
    }

    public void cargarCartasVolteadas(List<JugadorDTO> jugadores, String nombre) {
        JPanel[] mazosVolteados = {panelJugador2, panelJugador3, panelJugador4};

        for (JPanel panel : mazosVolteados) {
            panel.removeAll();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, -20, 10));
            panel.setVisible(false);
        }

        int indexPanel = 0;
        for (JugadorDTO j : obtenerRivales(jugadores, nombre)) {
            if (indexPanel < mazosVolteados.length) {
                int cantidadCartas = (j.getMano() != null) ? j.getMano().getCartas().size() : 0;
                mazosVolteados[indexPanel].setVisible(true);
                for (int i = 0; i < cantidadCartas; i++) {
                    CartaReversoUNO reverso = new CartaReversoUNO();
                    reverso.setPreferredSize(new Dimension(50, 75));
                    mazosVolteados[indexPanel].add(reverso);
                }
                mazosVolteados[indexPanel].revalidate();
                mazosVolteados[indexPanel].repaint();

                indexPanel++;
            }
        }
    }

    private JugadorDTO buscarJugador(List<JugadorDTO> jugadores, String nombre) {
        if (jugadores == null || nombre == null) {
            return null;
        }
        for (JugadorDTO jugador : jugadores) {
            if (jugador != null && nombre.equals(jugador.getNombre())) {
                return jugador;
            }
        }
        return null;
    }

    private List<JugadorDTO> obtenerRivales(List<JugadorDTO> jugadores, String nombre) {
        List<JugadorDTO> rivales = new ArrayList<>();
        if (jugadores == null) {
            return rivales;
        }
        for (JugadorDTO jugador : jugadores) {
            if (jugador != null && (nombre == null || !nombre.equals(jugador.getNombre()))) {
                rivales.add(jugador);
            }
        }
        return rivales;
    }

    private String obtenerAvatarSeguro(JugadorDTO jugador) {
        if (jugador == null) {
            return "pfp";
        }
        String a = jugador.getAvatar();
        if (a == null || a.isBlank()) {
            return "pfp";
        }
        try {
            int lastSlash = Math.max(a.lastIndexOf('/'), a.lastIndexOf('\\'));
            String base = lastSlash >= 0 ? a.substring(lastSlash + 1) : a;
            if (base.toLowerCase().endsWith(".png")) {
                base = base.substring(0, base.length() - 4);
            }
            if (base.isBlank()) {
                return "pfp";
            }
            return base;
        } catch (Exception ex) {
            return "pfp";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        btnDecirUno = new javax.swing.JButton();

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
        panelFondo.add(btnJugarCarta, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 710, 140, -1));

        panelAvatar1.setBackground(new java.awt.Color(255, 255, 255));
        panelAvatar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelAvatar1.setOpaque(false);

        javax.swing.GroupLayout panelAvatar1Layout = new javax.swing.GroupLayout(panelAvatar1);
        panelAvatar1.setLayout(panelAvatar1Layout);
        panelAvatar1Layout.setHorizontalGroup(
            panelAvatar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        panelAvatar1Layout.setVerticalGroup(
            panelAvatar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 550, 140, 140));

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
            .addGap(0, 138, Short.MAX_VALUE)
        );
        panelAvatar2Layout.setVerticalGroup(
            panelAvatar2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 128, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 162, 140, 130));

        panelNumeroCartas2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelNumeroCartas2Layout = new javax.swing.GroupLayout(panelNumeroCartas2);
        panelNumeroCartas2.setLayout(panelNumeroCartas2Layout);
        panelNumeroCartas2Layout.setHorizontalGroup(
            panelNumeroCartas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 48, Short.MAX_VALUE)
        );
        panelNumeroCartas2Layout.setVerticalGroup(
            panelNumeroCartas2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );

        panelFondo.add(panelNumeroCartas2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 400, 50, 80));

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
            .addGap(0, 128, Short.MAX_VALUE)
        );
        panelAvatar4Layout.setVerticalGroup(
            panelAvatar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, 130, 140));

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
            .addGap(0, 138, Short.MAX_VALUE)
        );

        panelFondo.add(panelAvatar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 140, 140, 140));

        panelCartaMedio.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelCartaMedio.setLayout(new java.awt.BorderLayout());
        panelFondo.add(panelCartaMedio, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 300, 70, 110));

        panelPilaCartas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelPilaCartas.setLayout(new java.awt.BorderLayout());
        panelFondo.add(panelPilaCartas, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 300, 80, 110));

        btnDecirUno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/unoBoton.png"))); // NOI18N
        btnDecirUno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDecirUnoActionPerformed(evt);
            }
        });
        panelFondo.add(btnDecirUno, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 600, 90, 90));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelFondo, javax.swing.GroupLayout.PREFERRED_SIZE, 1360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 790, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(panelFondo, javax.swing.GroupLayout.PREFERRED_SIZE, 790, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDecirUnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDecirUnoActionPerformed
        if (controlador != null) {
           
            controlador.decirUno(); 
            btnDecirUno.setVisible(false);
            lblIndicadorUno.setText("¡UNO GRITADO!");
            lblIndicadorUno.setForeground(new Color(0, 200, 0));
        }
    }//GEN-LAST:event_btnDecirUnoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDecirUno;
    private vista.DiseñosExtras.botonCircular btnJugarCarta;
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
}
