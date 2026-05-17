package facades;

import Entidades.*;
import Entidades.Estados.EstadoEsperando;
import Entidades.Estados.IEstadoPartida;
import Entidades.Logica.Partida;
import Entidades.fabricas.*;

import java.util.ArrayList;
import java.util.List;

public class GestorJuegoFacade {

    private Partida partidaActual;
    private final ICartaFactory cartaFactory;
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
        this.partidaActual = PartidaFactory.crearPartida(listaJugadores, this.cartaFactory, this.mazoFactory, new EstadoEsperando());
        this.partidaActual.getMazo().barajear();
        this.partidaActual.iniciar();

        System.out.println("fachada: construyendo mazo y partida....");

    }

    public void eliminarJugador(String nombreJugador) {
        if (this.partidaActual != null) {
            this.partidaActual.getJugadores().removeIf(jugador -> jugador.getNombre().equals(nombreJugador));
            System.out.println("Fachada: Jugador " + nombreJugador + " eliminado de la partida.");
        }
    }
    
    public String verificarGanador(){
        if (this.partidaActual != null) {
            for (Jugador jugador : this.getPartidaActual().getJugadores()) {
                if (jugador.getMano().getCartas().isEmpty()) {
                    return jugador.getNombre();
                }
            }
        }
        return null;
    }
    
    public Partida getPartidaActual() {
        return this.partidaActual;
    }
}
