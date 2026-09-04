package vista.animacion;

import dtos.CartaDTO;
import vista.componentes.PanelCarta;
import vista.tema.Tema;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Capa transparente encima del tablero donde se dibujan las animaciones.
 *
 * Va en el JLayeredPane de la ventana, por encima de todo, y no intercepta el
 * raton: contains() devuelve false para que los clics pasen al tablero.
 *
 * El cliente recibe una foto completa del estado, no un evento "fulano jugo tal
 * carta", asi que quien decide que animar es AnimadorTablero comparando el
 * estado anterior con el nuevo.
 */
public class CapaAnimacion extends JComponent {

    private static final int MS_POR_CUADRO = 16;   // ~60 cuadros por segundo

    private final List<Animacion> activas = new CopyOnWriteArrayList<>();
    private final Timer reloj;

    public CapaAnimacion() {
        setOpaque(false);
        reloj = new Timer(MS_POR_CUADRO, e -> latido());
    }

    /** Los clics atraviesan la capa. */
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    private void latido() {
        long ahora = System.currentTimeMillis();
        activas.removeIf(a -> {
            if (a.termino(ahora)) {
                if (a.alTerminar != null) {
                    a.alTerminar.run();
                }
                return true;
            }
            return false;
        });
        if (activas.isEmpty()) {
            reloj.stop();
        }
        repaint();
    }

    private void arrancar(Animacion a) {
        activas.add(a);
        if (!reloj.isRunning()) {
            reloj.start();
        }
        repaint();
    }

    // --- API ---------------------------------------------------------------

    /** Una carta viaja de un punto a otro, girando y encogiendo al llegar. */
    public void volarCarta(CartaDTO carta, Point desde, Point hasta, int duracionMs, Runnable alTerminar) {
        if (carta == null || desde == null || hasta == null) {
            if (alTerminar != null) {
                alTerminar.run();
            }
            return;
        }
        arrancar(new VueloCarta(carta, desde, hasta, duracionMs, alTerminar));
    }

    /** Texto grande que aparece, se sostiene y se desvanece. */
    public void mostrarAviso(String texto, Color color, int duracionMs) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        arrancar(new Aviso(texto, color, duracionMs));
    }

    /** Detiene todo; se llama al cerrar la partida. */
    public void limpiar() {
        activas.clear();
        reloj.stop();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (activas.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        long ahora = System.currentTimeMillis();
        for (Animacion a : activas) {
            Graphics2D gc = (Graphics2D) g2.create();
            try {
                a.dibujar(gc, a.avance(ahora));
            } finally {
                gc.dispose();
            }
        }
        g2.dispose();
    }

    // --- Tipos de animacion ------------------------------------------------

    private abstract static class Animacion {
        final long inicio = System.currentTimeMillis();
        final int duracion;
        final Runnable alTerminar;

        Animacion(int duracion, Runnable alTerminar) {
            this.duracion = Math.max(1, duracion);
            this.alTerminar = alTerminar;
        }

        boolean termino(long ahora) {
            return ahora - inicio >= duracion;
        }

        /** 0 al empezar, 1 al terminar. */
        float avance(long ahora) {
            return Math.min(1f, (ahora - inicio) / (float) duracion);
        }

        abstract void dibujar(Graphics2D g2, float t);
    }

    /** Desaceleracion: rapido al salir, suave al llegar. */
    private static float suavizar(float t) {
        float inv = 1f - t;
        return 1f - inv * inv * inv;
    }

    private static class VueloCarta extends Animacion {
        private final java.awt.image.BufferedImage imagen;
        private final Point desde;
        private final Point hasta;

        VueloCarta(CartaDTO carta, Point desde, Point hasta, int duracion, Runnable alTerminar) {
            super(duracion, alTerminar);
            this.desde = desde;
            this.hasta = hasta;
            this.imagen = dibujarCartaAImagen(carta);
        }

        /**
         * La carta se rasteriza UNA vez y luego solo se dibuja la imagen.
         *
         * Pintar el PanelCarta directamente en cada cuadro reventaba: es un
         * componente sin padre, y el pipeline de doble bufer de Swing lanzaba
         * NullPointerException en BufferStrategyPaintManager. Ademas asi el
         * dibujo de la carta se hace una sola vez y no 60 veces por segundo.
         */
        private static java.awt.image.BufferedImage dibujarCartaAImagen(CartaDTO carta) {
            PanelCarta pintor = new PanelCarta(carta);
            java.awt.Dimension d = pintor.getPreferredSize();
            pintor.setSize(d);
            pintor.setDoubleBuffered(false);

            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                    Math.max(1, d.width), Math.max(1, d.height),
                    java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            try {
                pintor.paint(g);
            } finally {
                g.dispose();
            }
            return img;
        }

        @Override
        void dibujar(Graphics2D g2, float t) {
            float p = suavizar(t);
            int w = imagen.getWidth();
            int h = imagen.getHeight();

            double x = desde.x + (hasta.x - desde.x) * p;
            double y = desde.y + (hasta.y - desde.y) * p;

            // Arco: la carta se eleva a media trayectoria en vez de ir recta
            double altura = -Math.sin(Math.PI * p) * 42;

            // Crece un poco al salir y vuelve a su tamano al aterrizar
            double escala = 1.0 + 0.28 * Math.sin(Math.PI * p);
            double giro = Math.toRadians(18 * Math.sin(Math.PI * p));

            g2.translate(x, y + altura);
            g2.rotate(giro, w / 2.0, h / 2.0);
            g2.scale(escala, escala);

            // Sombra proyectada bajo la carta en vuelo
            g2.setColor(new Color(0, 0, 0, 45));
            g2.fillRoundRect(4, 8, w - 6, h - 6, 14, 14);

            g2.drawImage(imagen, 0, 0, null);
        }
    }

    private static class Aviso extends Animacion {
        private final String texto;
        private final Color color;

        Aviso(String texto, Color color, int duracion) {
            super(duracion, null);
            this.texto = texto;
            this.color = color == null ? Tema.AMARILLO : color;
        }

        @Override
        void dibujar(Graphics2D g2, float t) {
            // Entra creciendo, se sostiene, y se va desvaneciendo
            float opacidad = t < 0.15f ? t / 0.15f
                    : (t > 0.7f ? (1f - t) / 0.3f : 1f);
            float escala = t < 0.15f ? 0.7f + 0.3f * (t / 0.15f) : 1f;

            java.awt.Rectangle area = g2.getClipBounds();
            int cx = area != null ? area.width / 2 : 400;
            int cy = area != null ? (int) (area.height * 0.36) : 200;

            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, opacidad))));
            g2.translate(cx, cy);
            g2.scale(escala, escala);

            Font fuente = new Font(Tema.familia(), Font.BOLD, 54);
            g2.setFont(fuente);
            FontMetrics fm = g2.getFontMetrics();
            int ancho = fm.stringWidth(texto);
            int x = -ancho / 2;

            // Contorno oscuro para que se lea sobre cualquier fondo
            g2.setColor(new Color(0, 0, 0, 170));
            for (int dx = -3; dx <= 3; dx += 3) {
                for (int dy = -3; dy <= 3; dy += 3) {
                    g2.drawString(texto, x + dx, dy);
                }
            }
            g2.setColor(color);
            g2.drawString(texto, x, 0);
        }
    }
}
