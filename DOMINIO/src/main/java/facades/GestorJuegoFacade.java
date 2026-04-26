package facades;

import Comandos.ComandoIniciarPartida;
import Comandos.IComando;
import Entidades.Jugador;
import Entidades.Logica.Partida;
import Entidades.Mano;
import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.MazoClasicoFactory;
import Entidades.fabricas.PartidaFactory;

import java.util.ArrayList;
import java.util.List;

public class GestorJuegoFacade {
    private Partida partidaActual;

    public void prepararIniciarPartida(List<Jugador> nombresJugadores) {
        System.out.println("Fachada: iniciando los preservativos de la partida...");

        List<Jugador> listaJugadores = new ArrayList<Jugador>();
        for (String nombre : nombresJugadores) {
            Jugador nuevoJugador = new Jugador();
            nuevoJugador.setNombre(nombre);
            nuevoJugador.setMano(new Mano());
            listaJugadores.add(nuevoJugador);
        }
        CartaFactory cartaFactory = new CartaFactory();
        MazoClasicoFactory mazoFactory = new MazoClasicoFactory();

        System.out.println("fachada: construyendo mazo y partida....");
        this.partidaActual = PartidaFactory.crearPartida(listaJugadores, cartaFactory, mazoFactory);
        IComando comandoIniciar = new ComandoIniciarPartida(this.partidaActual);
        comandoIniciar.ejecutar();
    }
    public Partida getPartidaActual() {
        return this.partidaActual;
    }
}
