package vista.tema;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Paleta y tipografia unicas de la aplicacion.
 *
 * Antes cada pantalla ponia sus colores a mano: habia catorce distintos sin
 * ningun sistema (tres azules diferentes, un salmon, un amarillo puro...) y la
 * fuente "Segoe UI" hardcodeada en cuarenta sitios, que solo existe en Windows.
 *
 * Los colores y fuentes de las vistas los genera NetBeans dentro de
 * initComponents(), que no se puede editar a mano. Por eso aplicar() recorre el
 * arbol de componentes DESPUES de initComponents() y traduce los colores viejos
 * a la paleta nueva.
 */
public final class Tema {

    private Tema() {
    }

    /**
     * Instala el Look and Feel y los valores por defecto. Debe llamarse UNA vez,
     * antes de crear cualquier ventana.
     *
     * Sin esto la aplicacion corria con Metal, el aspecto por defecto de Swing:
     * el bloque que activaba Nimbus vivia en MenuPrincipal.main, que no es el
     * punto de entrada real.
     */
    public static void instalar() {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("[Tema] No se pudo instalar FlatLaf: " + e.getMessage());
        }

        javax.swing.UIManager.put("Button.arc", 999);        // botones tipo pastilla
        javax.swing.UIManager.put("Component.arc", 14);
        javax.swing.UIManager.put("TextComponent.arc", 14);
        javax.swing.UIManager.put("ScrollBar.width", 10);
        javax.swing.UIManager.put("ScrollBar.thumbArc", 999);
        javax.swing.UIManager.put("ScrollBar.showButtons", false);
        javax.swing.UIManager.put("Component.focusWidth", 0);
        javax.swing.UIManager.put("Component.focusColor", AMARILLO);
        javax.swing.UIManager.put("Component.borderColor", BORDE);
        javax.swing.UIManager.put("OptionPane.background", SUPERFICIE);
        javax.swing.UIManager.put("Panel.background", SUPERFICIE);
        javax.swing.UIManager.put("defaultFont", cuerpo(14));
    }

    // --- Paleta UNO --------------------------------------------------------

    /** Rojo de fondo, arriba del degradado. */
    public static final Color ROJO = new Color(0xD3, 0x1F, 0x2B);
    /** Rojo profundo, abajo del degradado y para superficies oscuras. */
    public static final Color ROJO_PROFUNDO = new Color(0x8E, 0x0E, 0x18);
    public static final Color AMARILLO = new Color(0xF5, 0xB9, 0x24);
    public static final Color AMARILLO_OSCURO = new Color(0xD1, 0x99, 0x11);
    public static final Color AZUL = new Color(0x14, 0x6E, 0xB4);
    public static final Color VERDE = new Color(0x2E, 0x9E, 0x4F);
    public static final Color VERDE_OSCURO = new Color(0x22, 0x7A, 0x3C);

    public static final Color SUPERFICIE = new Color(0xFF, 0xFF, 0xFF);
    public static final Color SUPERFICIE_ALT = new Color(0xF4, 0xF5, 0xF7);
    public static final Color BORDE = new Color(0xDD, 0xDF, 0xE3);

    public static final Color TEXTO = new Color(0x1C, 0x1C, 0x1E);
    public static final Color TEXTO_SUAVE = new Color(0x6B, 0x72, 0x80);
    public static final Color TEXTO_CLARO = new Color(0xFF, 0xFF, 0xFF);

    /** Sombra reutilizable para los componentes que la dibujan. */
    public static final Color SOMBRA = new Color(0, 0, 0, 55);

    // --- Colores viejos que aplicar() traduce ------------------------------

    private static final Color VIEJO_SALMON = new Color(255, 109, 109);
    private static final Color VIEJO_AZUL_FUERTE = new Color(51, 51, 255);
    private static final Color VIEJO_AZUL_MEDIO = new Color(51, 102, 255);
    private static final Color VIEJO_ROJO = new Color(204, 0, 0);
    private static final Color VIEJO_AMARILLO = new Color(255, 255, 51);
    private static final Color VIEJO_AMARILLO2 = new Color(255, 255, 0);
    private static final Color VIEJO_VERDE = new Color(102, 204, 0);
    private static final Color VIEJO_VERDE2 = new Color(30, 136, 56);

    // --- Tipografia --------------------------------------------------------

    /**
     * Primera familia disponible en la maquina. "Segoe UI" solo existe en
     * Windows; sin esta cadena, en Linux y macOS caia a una generica.
     */
    private static final String FAMILIA = elegirFamilia(
            "Segoe UI", "SF Pro Text", "Helvetica Neue", "Ubuntu", "Cantarell",
            "DejaVu Sans", "Liberation Sans", "Arial");

    private static String elegirFamilia(String... candidatas) {
        Set<String> disponibles = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String c : candidatas) {
            if (disponibles.contains(c)) {
                return c;
            }
        }
        return Font.SANS_SERIF;
    }

    public static String familia() {
        return FAMILIA;
    }

    public static Font titulo(int tamano) {
        return new Font(FAMILIA, Font.BOLD, tamano);
    }

    public static Font cuerpo(int tamano) {
        return new Font(FAMILIA, Font.PLAIN, tamano);
    }

    public static Font boton(int tamano) {
        return new Font(FAMILIA, Font.BOLD, tamano);
    }

    // --- Aplicacion al arbol de componentes --------------------------------

    /**
     * Normaliza fuentes y traduce los colores viejos en todo el arbol.
     *
     * Conserva el TAMANO de fuente que puso NetBeans y cambia solo la familia:
     * las vistas usan AbsoluteLayout (posiciones fijas), asi que agrandar el
     * texto cortaria etiquetas.
     */
    public static void aplicar(Container raiz) {
        if (raiz == null) {
            return;
        }
        aplicarA(raiz);
        for (Component hijo : raiz.getComponents()) {
            if (hijo instanceof Container c) {
                aplicar(c);
            } else {
                aplicarA(hijo);
            }
        }
    }

    private static void aplicarA(Component c) {
        Font f = c.getFont();
        if (f != null && !FAMILIA.equals(f.getFamily())) {
            c.setFont(new Font(FAMILIA, f.getStyle(), f.getSize()));
        }

        if (c instanceof JPanel p) {
            Color nuevo = traducirFondo(p.getBackground());
            if (nuevo != null) {
                p.setBackground(nuevo);
            }
        } else if (c instanceof JLabel l) {
            Color nuevo = traducirTexto(l.getForeground());
            if (nuevo != null) {
                l.setForeground(nuevo);
            }
            Color fondo = traducirFondo(l.getBackground());
            if (fondo != null && l.isOpaque()) {
                l.setBackground(fondo);
            }
        } else if (c instanceof JScrollPane sp) {
            // Las barras de desplazamiento de Metal se veian como cajas grises
            // encima del fondo; sin borde y transparentes desaparecen.
            sp.setBorder(null);
            sp.setOpaque(false);
            if (sp.getViewport() != null) {
                sp.getViewport().setOpaque(false);
            }
        } else if (c instanceof JTextField t && !(t instanceof vista.componentes.TextFieldRedondo)) {
            t.setForeground(TEXTO);
        } else if (c instanceof JButton b && !(b instanceof vista.componentes.botonCircular)) {
            b.setFont(boton(b.getFont() != null ? b.getFont().getSize() : 14));
            boolean soloIcono = b.getIcon() != null
                    && (b.getText() == null || b.getText().isBlank());
            if (soloIcono) {
                // El icono ya es el boton (las cartas del menu, la rueda de
                // ajustes): pintar ademas el fondo le dejaba un halo gris.
                b.setContentAreaFilled(false);
                b.setBorderPainted(false);
                b.setOpaque(false);
                b.setFocusPainted(false);
                b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
        }
    }

    private static Color traducirFondo(Color viejo) {
        if (viejo == null) {
            return null;
        }
        if (igual(viejo, VIEJO_SALMON)) {
            return ROJO;
        }
        if (igual(viejo, VIEJO_AZUL_FUERTE) || igual(viejo, VIEJO_AZUL_MEDIO)) {
            return AZUL;
        }
        if (igual(viejo, VIEJO_ROJO)) {
            return ROJO_PROFUNDO;
        }
        if (igual(viejo, VIEJO_AMARILLO) || igual(viejo, VIEJO_AMARILLO2)) {
            return AMARILLO;
        }
        if (igual(viejo, VIEJO_VERDE) || igual(viejo, VIEJO_VERDE2)) {
            return VERDE;
        }
        return null;
    }

    private static Color traducirTexto(Color viejo) {
        if (viejo == null) {
            return null;
        }
        if (igual(viejo, VIEJO_AZUL_FUERTE) || igual(viejo, VIEJO_AZUL_MEDIO)) {
            return AZUL;
        }
        if (igual(viejo, VIEJO_ROJO)) {
            return ROJO_PROFUNDO;
        }
        return null;
    }

    private static boolean igual(Color a, Color b) {
        return a.getRGB() == b.getRGB();
    }
}
