package comandos;

import dtos.MensajeDTO;
import dtos.PartidaDTO;
import interfaces.IComandoServidor;
import facades.GestorJuegoFacade;
import broker.Broker;
import Mappers.PartidaMapper; 

public class ComandoDesconectarJugador implements IComandoServidor {

    private GestorJuegoFacade gestorJuego;
    private Broker broker;

    public ComandoDesconectarJugador(GestorJuegoFacade gestorJuego, Broker broker) {
        this.gestorJuego = gestorJuego;
        this.broker = broker;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null) {
            return;
        }

        try {
            String jugadorQueSalio = mensaje.getRemitente();

            gestorJuego.eliminarJugador(jugadorQueSalio);

            PartidaDTO partidaActualizada = PartidaMapper.toDTO(gestorJuego.getPartidaActual());

            MensajeDTO avisoParaLosDemas = new MensajeDTO();
            avisoParaLosDemas.setTipo("ACTUALIZAR_TABLERO");
            avisoParaLosDemas.setRemitente("SERVIDOR");
            
            avisoParaLosDemas.getDatos().put("partida", partidaActualizada);

            broker.publicar("ACTUALIZAR_TABLERO", avisoParaLosDemas);

            System.out.println("[COMANDO-DESCONECTAR] " + jugadorQueSalio + " fue eliminado exitosamente.");

        } catch (Exception e) {
            System.out.println("[ERROR] Falló la desconexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}