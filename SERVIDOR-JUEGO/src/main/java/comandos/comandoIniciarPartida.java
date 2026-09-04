/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import dtos.PartidaDTO;

import interfaces.IComandoServidor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import servidor.GestorSalas;
import servidor.JuegoServidor;

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

        String idSesion = mensaje.getIdSesion();
        NodoCliente nodoSolicitante = manejadorNodos.obtenerNodoPorSesion(idSesion);
        if (nodoSolicitante == null) {
            return;
        }

        // La sala sale del nodo del solicitante; el codigo del mensaje solo se
        // usa como respaldo, porque el cliente puede mandarlo vacio.
        String codigoSala = nodoSolicitante.getCodigoSala();
        if (codigoSala == null && mensaje.getDatos().get("codigoSala") != null) {
            codigoSala = String.valueOf(mensaje.getDatos().get("codigoSala")).trim().toUpperCase();
        }
        GestorSalas.SalaDisponible sala = gestorSalas.obtenerSala(codigoSala);
        if (sala == null) {
            enviarErrorInicio(idSesion, "No se encontro la sala para iniciar.");
            return;
        }

        if (!sala.getHost().equalsIgnoreCase(nodoSolicitante.getNombre())) {
            enviarErrorInicio(idSesion, "Solo el host puede iniciar la partida.");
            return;
        }

        // Solo los jugadores de ESTA sala. Antes se tomaban todos los conectados
        // del servidor, asi que arrancar una partida arrastraba a quien estuviera
        // en cualquier otra sala.
        List<String> jugadores = manejadorNodos.obtenerNombresDeSala(codigoSala);
        if (jugadores.size() < 2) {
            enviarErrorInicio(idSesion, "Se requieren al menos 2 jugadores para iniciar.");
            return;
        }

        if (!manejadorNodos.estanTodosListosEnSala(codigoSala)) {
            enviarErrorInicio(idSesion, "Todos los jugadores deben estar listos.");
            return;
        }

        // Se envian los avatares junto con los nombres: antes solo iban los
        // nombres y la foto elegida en el menu se quedaba en la sesion.
        PartidaDTO estadoInicialDTO = juegoServidor.iniciarNuevoJuego(
                codigoSala, manejadorNodos.obtenerAvataresDeSala(codigoSala), this.manejadorNodos);
        MensajeDTO estadoPartida = new MensajeDTO();
        estadoPartida.setTipo("PARTIDA_INICIADA");
        estadoPartida.setRemitente("SERVIDOR");

        //guardamos los datos en el sobre
        Map<String, Object> datos = new HashMap();
        datos.put("partida", estadoInicialDTO);
        estadoPartida.setDatos(datos);

        //enviamos el sobre a los jugadores de la sala
        manejadorNodos.notificarASala(codigoSala, estadoPartida);

    }

    private void enviarErrorInicio(String idSesion, String motivo) {
        if (idSesion == null) {
            return;
        }
        MensajeDTO error = new MensajeDTO();
        error.setTipo("ERROR_INICIAR_PARTIDA");
        error.setRemitente("SERVIDOR");
        Map<String, Object> datosError = new HashMap<>();
        datosError.put("motivo", motivo);
        error.setDatos(datosError);
        manejadorNodos.enviarNodo(idSesion, error);
    }
}
