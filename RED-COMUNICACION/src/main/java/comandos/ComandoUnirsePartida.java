/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;

/**
 *
 * @author USER
 */
public class ComandoUnirsePartida implements IComandoServidor {

    private ManejadorNodos ManejadorNodos;

    public ComandoUnirsePartida(ManejadorNodos ManejadorNodos) {
        this.ManejadorNodos = ManejadorNodos;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        String nombreInviado = (String) mensaje.getDatos().get("nombre");
        String codigoSala = (String) mensaje.getDatos().get("codigoSala");

        System.out.println("[COMANDO-UNIRSE-PARTIDA] " + nombreInviado + "intenta unirse ala partida" + codigoSala);
        boolean salaExistente = true;

        MensajeDTO respuesta = new MensajeDTO();
        if (salaExistente) {
            respuesta.setTipo("UNIDO_EXITO");
            respuesta.getDatos().put("codigoSala", codigoSala);
            respuesta.getDatos().put("nombre", nombreInviado);

        } else {
            respuesta.setTipo("ERROR_UNIRSE");
            respuesta.getDatos().put("motivo", "La sala no existe o el codigo es incorrecto");
        }
        respuesta.setRemitente("SERVIDOR");

        for (NodoCliente nodo : ManejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(respuesta);
        }
        if(salaExistente) {
            java.util.List<java.util.Map<String, String>> listaJugadoresConAvatar = new java.util.ArrayList<>();

            for (NodoCliente n : ManejadorNodos.obtenerNodosConectados()) {
                java.util.Map<String, String> datosJugador = new java.util.HashMap<>();
                datosJugador.put("nombre", n.getNombre());

                if (n.getAvatar() != null && !n.getAvatar().equals("no hay")) {
                    datosJugador.put("avatar", n.getAvatar());
                } else {
                    datosJugador.put("avatar", "pfp");
                }
                listaJugadoresConAvatar.add(datosJugador);
            }

            MensajeDTO notificacionLista = new MensajeDTO();
            notificacionLista.setTipo("LISTA_ACTUALIZADA");
            notificacionLista.setRemitente("SERVIDOR");

            java.util.Map<String, Object> datosLista = new java.util.HashMap<>();
            datosLista.put("jugadores", listaJugadoresConAvatar);
            notificacionLista.setDatos(datosLista);

            for (NodoCliente n : ManejadorNodos.obtenerNodosConectados()) {
                n.enviarMensaje(notificacionLista);
            }
        }
    }

}
