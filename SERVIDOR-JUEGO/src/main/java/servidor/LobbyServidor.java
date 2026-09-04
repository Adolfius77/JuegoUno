/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.Logica.Partida;
import Entidades.Jugador;
import Entidades.Mano;
import Entidades.fabricas.MazoClasicoFactory;
import Interfacez.IBroker;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import comandos.ComandoListarPartidas;
import comandos.ComandoActualizarEstadoListo;
import comandos.ComandoRegistrarJugador;
import comandos.ComandoGritarUno;
import comandos.ComandoPasarTurno;
import comandos.ComandoJugarCarta;
import comandos.ComandoTomarCarta;
import comandos.ComandoUnirsePartida;
import comandos.ComandoVolverLobby;
import comandos.comandoCrearPartida;
import comandos.comandoIniciarPartida;

/**
 *
 * @author USER
 */
public class LobbyServidor {

    private final IBroker broker;
    private final JuegoServidor juegoServidor;
    private final ManejadorNodos manejadorNodos;
    private final GestorSalas gestorSalas;

    public LobbyServidor(IBroker broker, JuegoServidor juegoServidor) {
        this.broker = broker;
        this.manejadorNodos = new ManejadorNodos();
        this.gestorSalas = new GestorSalas();
        this.juegoServidor = juegoServidor;
        //comandos de la lobby
        this.broker.subscribirse("INTENCION_INICIAR_PARTIDA", new comandoIniciarPartida(manejadorNodos, juegoServidor, gestorSalas)::ejecutar);
        this.broker.subscribirse("REGISTRO_JUGADOR", new ComandoRegistrarJugador(manejadorNodos)::ejecutar);
        this.broker.subscribirse("PETICION_CREAR_PARTIDA", new comandoCrearPartida(manejadorNodos, gestorSalas)::ejecutar);
        this.broker.subscribirse("PETICION_UNIRSE_PARTIDA", new ComandoUnirsePartida(manejadorNodos, gestorSalas)::ejecutar);
        this.broker.subscribirse("PETICION_LISTA_PARTIDAS", new ComandoListarPartidas(manejadorNodos, gestorSalas)::ejecutar);
        this.broker.subscribirse("ACTUALIZAR_ESTADO_LISTO", new ComandoActualizarEstadoListo(manejadorNodos)::ejecutar);
        this.broker.subscribirse("PETICION_JUGAR_CARTA", new ComandoJugarCarta(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_TOMAR_CARTA", new ComandoTomarCarta(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_PASAR_TURNO", new ComandoPasarTurno(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_GRITAR_UNO", new ComandoGritarUno(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_VOLVER_LOBBY", new ComandoVolverLobby(manejadorNodos, juegoServidor)::ejecutar);
    }

    public static LobbyServidor crearLobbyPorDefecto(IBroker broker) {
        JuegoServidor juego = new JuegoServidor(
                new CartaFactory(),
                new MazoClasicoFactory(),
                EstadoFactory.crearEstadoEsperando()
        );
        return new LobbyServidor(broker, juego);
    }

    public void registrarNuevoJugadorTemporal(NodoCliente nuevoNodo) {
        manejadorNodos.registrarNuevoJugador(nuevoNodo);
    }

    public boolean conoceSesion(String idSesion) {
        return manejadorNodos.obtenerNodoPorSesion(idSesion) != null;
    }

    public void eliminarJugadorPorSesion(String idSesion) {
        if (idSesion == null) {
            return;
        }

        NodoCliente nodo = manejadorNodos.obtenerNodoPorSesion(idSesion);
        if (nodo == null) {
            return;
        }
        String nombreJugador = nodo.getNombre();
        String codigoSala = nodo.getCodigoSala();

        manejadorNodos.eliminarNodo(idSesion);
        if (codigoSala != null) {
            // Libera la plaza para que la sala no quede llena para siempre.
            gestorSalas.salirJugador(codigoSala);
        }

        try {
            Partida partida = null;
            if (this.juegoServidor != null && codigoSala != null) {
                partida = this.juegoServidor.getPartidaDeSala(codigoSala);
            }

            if (partida != null && nombreJugador != null) {
                Jugador desconectado = partida.getJugadores().stream()
                        .filter(j -> nombreJugador.equalsIgnoreCase(j.getNombre()))
                        .findFirst().orElse(null);

                if (desconectado != null) {
                    int jugadoresActivos = partida.getJugadores().size();
                    if (jugadoresActivos <= 2) {
                        Jugador ganador = partida.getJugadores().stream()
                                .filter(j -> !j.getNombre().equalsIgnoreCase(nombreJugador))
                                .findFirst().orElse(null);
                        if (ganador != null) {
                            partida.setEstado(EstadoFactory.crearEstadoFinalizada());
                            partida.notificarObservador("PARTIDA_FINALIZADA:" + ganador.getNombre());
                        }
                    } else {
                        desconectado.setMano(new Mano());
                        partida.notificarObservador("JUGADOR_DESCONECTADO:" + nombreJugador);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("[LobbyServidor] Error al procesar desconexion de jugador: " + ex.getMessage());
        }
    }
}
