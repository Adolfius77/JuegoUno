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

    /**
     * @param avataresPorNombre nombre del jugador -> id de su avatar, en orden de
     *                          turno. Antes solo se recibian los nombres, asi que
     *                          el avatar elegido en el menu nunca llegaba a la
     *                          partida y el tablero mostraba cuadros vacios.
     */
    public void prepararIniciarPartida(java.util.Map<String, String> avataresPorNombre) {
        List<Jugador> listaJugadores = new ArrayList<Jugador>();
        for (java.util.Map.Entry<String, String> entrada : avataresPorNombre.entrySet()) {
            Jugador nuevoJugador = new Jugador();
            nuevoJugador.setNombre(String.valueOf(entrada.getKey()));
            nuevoJugador.setAvatar(entrada.getValue());
            nuevoJugador.setMano(new Mano());
            listaJugadores.add(nuevoJugador);
        }
        this.partidaActual = PartidaFactory.crearPartida(listaJugadores, this.cartaFactory, this.mazoFactory, new EstadoEsperando());
        this.partidaActual.getMazo().barajear();
        this.partidaActual.iniciar();
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
