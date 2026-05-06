/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package red;

import Entidades.Estados.IEstadoPartida;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Interfacez.IBroker;
import Mappers.PartidaMapper;
import dtos.PartidaDTO;
import facades.GestorJuegoFacade;
import java.util.List;

/**
 *
 * @author USER
 */
public class JuegoServidor {
    private final IBroker broker;
    private GestorJuegoFacade fachadaJuego;
    private final ICartaFactory cartaFactory;
    private final IMazoFactory mazoFactory;
    private final IEstadoPartida estadoInicial;

    public JuegoServidor(IBroker broker, ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estadoInicial) {
        this.broker = broker;
        this.cartaFactory = cartaFactory;
        this.mazoFactory = mazoFactory;
        this.estadoInicial = estadoInicial;
        //aqui pondremos los comandos para el caso de uso inicial que es ejercer turno
    }
    
    public PartidaDTO iniciarNuevoJuego(List<String> nombreJugadores){
        this.fachadaJuego = new GestorJuegoFacade(cartaFactory, mazoFactory, estadoInicial);
        this.fachadaJuego.prepararIniciarPartida(nombreJugadores);
        return PartidaMapper.toDTO(this.fachadaJuego.getPartidaActual());
    }
}
