package utileria;


import javax.sound.sampled.*;
import java.net.URL;

public class GestorAudio {
    private static GestorAudio gestorAudio;
    private Clip  musicaFondo;
    private String cancionActual = "";

    private GestorAudio() {}

    public static GestorAudio getInstancia() {
        if (gestorAudio == null) {
            gestorAudio = new GestorAudio();
        }
        return gestorAudio;
    }

    public void reproducirMusica(String ruta){
        if (musicaFondo != null && musicaFondo.isRunning() && cancionActual.equals(ruta)) {
            return;
        }

        detenerMusica();

        try {
            URL cancion = getClass().getResource(ruta);

            if (cancion == null) {
                System.out.println("No se encontró el archivo de audio: " + ruta);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(cancion);
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioIn);

            FloatControl gainControl = (FloatControl) musicaFondo.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-25.0f);

            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
            this.cancionActual  = ruta;
        } catch (Exception e) {
            System.out.println("Error al reproducir el audio: " + e.getMessage());
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }
}
