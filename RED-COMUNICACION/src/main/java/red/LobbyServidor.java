/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package red;

import Comandos.ComandoIniciarPartida;
import Interfacez.IBroker;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import comandos.ComandoRegistrarJugador;
import comandos.comandoIniciarPartida;

/**
 *
 * @author USER
 */
public class LobbyServidor {

    private final IBroker broker;
    private final JuegoServidor juegoServidor;
    private final ManejadorNodos manejadorNodos;

    public LobbyServidor(IBroker broker, JuegoServidor juegoServidor) {
        this.broker = broker;
        this.manejadorNodos = new ManejadorNodos();
        this.juegoServidor = juegoServidor;
        //comandos de la lobby
        this.broker.subscribirse("INTENCION_INICIAR_PARTIDA", new comandoIniciarPartida(manejadorNodos, juegoServidor)::ejecutar);
    }

    public void registrarNuevoJugadorTemporal(NodoCliente nuevoNodo) {
        manejadorNodos.registrarNuevoJugador(nuevoNodo);
    }
}
