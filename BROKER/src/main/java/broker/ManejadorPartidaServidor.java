package broker;

import Entidades.Estados.IEstadoPartida;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Mappers.PartidaMapper;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import facades.GestorJuegoFacade;

import java.util.List;

public class ManejadorPartidaServidor {
    private Broker broker;
    private ICartaFactory cartaFactory;
    private IMazoFactory mazoFactory;
    private IEstadoPartida estado;

    public ManejadorPartidaServidor(Broker broker,  ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estado) {
        this.broker = broker;
        this.broker.subscribirse("INTENCION_INICIAR_PARTIDA", this::procesarInicioPartida);
        this.cartaFactory = cartaFactory;
        this.mazoFactory = mazoFactory;
        this.estado = estado;
    }
    private void procesarInicioPartida(MensajeDTO mensaje) {
        System.out.println("Servidor: Peticion de inicio partida recibida");

        List<String > nombreJugadores = broker.obtenerNombresDeNodosConectados();

        if(nombreJugadores.size() < 2) {
            System.out.println("no hay suficentes jugadores deben ser almenos mas de 2 o 2");
            return;
        }
        GestorJuegoFacade fachadaJuego = new GestorJuegoFacade(cartaFactory,mazoFactory,estado);
        fachadaJuego.prepararIniciarPartida(nombreJugadores);

        PartidaDTO estadoInicialDTO = PartidaMapper.toDTO(fachadaJuego.getPartidaActual());
        estadoInicialDTO.setTipo("PARTIDA_INICIADA");
        broker.publicar("PARTIDA_INICIADA", estadoInicialDTO);
    }
}
