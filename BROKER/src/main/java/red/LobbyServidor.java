/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package red;


import Entidades.fabricas.CartaFactory;
import Entidades.fabricas.EstadoFactory;
import Entidades.fabricas.MazoClasicoFactory;
import Interfacez.IBroker;
import Interfacez.IProxy;
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
        this.broker.subscribirse("PETICION_LISTA_PARTIDAS", new ComandoListarPartidas(gestorSalas)::ejecutar);
        this.broker.subscribirse("ACTUALIZAR_ESTADO_LISTO", new ComandoActualizarEstadoListo(manejadorNodos)::ejecutar);
        this.broker.subscribirse("PETICION_JUGAR_CARTA", new ComandoJugarCarta(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_TOMAR_CARTA", new ComandoTomarCarta(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_PASAR_TURNO", new ComandoPasarTurno(manejadorNodos, juegoServidor)::ejecutar);
        this.broker.subscribirse("PETICION_GRITAR_UNO", new ComandoGritarUno(manejadorNodos, juegoServidor)::ejecutar);
    }

    public static LobbyServidor crearLobbyPorDefecto(IBroker broker) {
        JuegoServidor juego = new JuegoServidor(
            broker,
            new CartaFactory(),
            new MazoClasicoFactory(),
            EstadoFactory.crearEstadoEsperando()
        );
        return new LobbyServidor(broker, juego);
    }

    public void registrarNuevoJugadorTemporal(NodoCliente nuevoNodo) {
        manejadorNodos.registrarNuevoJugador(nuevoNodo);
    }

    public void eliminarJugadorPorProxy(IProxy proxy) {
        manejadorNodos.eliminarPorProxy(proxy);
    }
}
