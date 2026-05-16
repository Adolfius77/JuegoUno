/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import dtos.PartidaDTO;
import static enums.TipoMensaje.*;
import interfaces.IComandoServidor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import red.GestorSalas;
import red.JuegoServidor;

/**
 *
 * @author USER
 */
public class comandoIniciarPartida implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final JuegoServidor juegoServidor;
    private final GestorSalas gestorSalas;

    public comandoIniciarPartida(ManejadorNodos manejadorNodos, JuegoServidor juegoServidor, GestorSalas gestorSalas) {
        this.manejadorNodos = manejadorNodos;
        this.juegoServidor = juegoServidor;
        this.gestorSalas = gestorSalas;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        System.out.println("[COMANDO-INICIAR-PARTIDA] el broker recibio el msj");

        IProxy proxySolicitante = (IProxy) mensaje.getDatos().get("proxy");
        NodoCliente nodoSolicitante = manejadorNodos.obtenerNodoPorProxy(proxySolicitante);
        if (nodoSolicitante == null) {
            return;
        }

        String codigoSala = mensaje.getDatos().get("codigoSala") != null
                ? String.valueOf(mensaje.getDatos().get("codigoSala")).trim().toUpperCase()
                : "";
        GestorSalas.SalaDisponible sala = gestorSalas.obtenerSala(codigoSala);
        if (sala == null) {
            enviarErrorInicio(proxySolicitante, "No se encontro la sala para iniciar.");
            return;
        }

        if (!sala.getHost().equalsIgnoreCase(nodoSolicitante.getNombre())) {
            enviarErrorInicio(proxySolicitante, "Solo el host puede iniciar la partida.");
            return;
        }

        List<String> jugadores = manejadorNodos.obtenerNombresDeNodosConectados();
        if (jugadores.size() < 2) {
            enviarErrorInicio(proxySolicitante, "Se requieren al menos 2 jugadores para iniciar.");
            return;
        }

        if (!manejadorNodos.estanTodosListos()) {
            enviarErrorInicio(proxySolicitante, "Todos los jugadores deben estar listos.");
            return;
        }

        PartidaDTO estadoInicialDTO = juegoServidor.iniciarNuevoJuego(jugadores, this.manejadorNodos);
        MensajeDTO estadoPartida = new MensajeDTO();
        estadoPartida.setTipo("PARTIDA_INICIADA");
        estadoPartida.setRemitente("SERVIDOR");

        //guardamos los datos en el sobre
        Map<String, Object> datos = new HashMap();
        datos.put("partida", estadoInicialDTO);
        estadoPartida.setDatos(datos);

        //enviamos el sobre a todos los jugadores
        for (NodoCliente nodo : manejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(estadoPartida);
        }

    }

    private void enviarErrorInicio(IProxy proxySolicitante, String motivo) {
        if (proxySolicitante == null) {
            return;
        }
        MensajeDTO error = new MensajeDTO();
        error.setTipo("ERROR_INICIAR_PARTIDA");
        error.setRemitente("SERVIDOR");
        Map<String, Object> datosError = new HashMap<>();
        datosError.put("motivo", motivo);
        error.setDatos(datosError);
        proxySolicitante.enviarMensaje(error);
    }
}
