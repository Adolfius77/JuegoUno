package facades;

import Comandos.ComandoIniciarPartida;
import Comandos.IComando;
import Entidades.Estados.IEstadoPartida;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.Mano;
import Entidades.fabricas.*;

import java.util.ArrayList;
import java.util.List;

public class GestorJuegoFacade {
    private Partida partidaActual;
    private final ICartaFactory  cartaFactory;
    private final IMazoFactory mazoFactory;
    private final IEstadoPartida estado;
    

    public GestorJuegoFacade(ICartaFactory cartaFactory, IMazoFactory mazoFactory, IEstadoPartida estado) {
        this.cartaFactory = cartaFactory;
        this.mazoFactory = mazoFactory;
        this.estado = estado;
    }

    public void prepararIniciarPartida(List<String> nombresJugadores) {
        System.out.println("Fachada: iniciando los preservativos de la partida...");

        List<Jugador> listaJugadores = new ArrayList<Jugador>();
        for (String nombre : nombresJugadores) {
            Jugador nuevoJugador = new Jugador();
            nuevoJugador.setNombre(String.valueOf(nombre));
            nuevoJugador.setMano(new Mano());
            listaJugadores.add(nuevoJugador);
        }
        this.partidaActual = PartidaFactory.crearPartida(listaJugadores, this.cartaFactory, this.mazoFactory,this.estado);
        System.out.println("fachada: construyendo mazo y partida....");
        IComando comandoIniciar = new ComandoIniciarPartida(this.partidaActual);
        comandoIniciar.ejecutar();
    }
    public Partida getPartidaActual() {
        return this.partidaActual;
    }
}
