/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comandos;

import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import red.GestorSalas;

/**
 *
 * @author USER
 */
public class ComandoUnirsePartida implements IComandoServidor {

    private final ManejadorNodos ManejadorNodos;
    private final GestorSalas gestorSalas;

    public ComandoUnirsePartida(ManejadorNodos ManejadorNodos, GestorSalas gestorSalas) {
        this.ManejadorNodos = ManejadorNodos;
        this.gestorSalas = gestorSalas;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        String nombreInviado = obtenerNombreJugador(mensaje.getDatos().get("nombre"));
        String codigoSala = normalizarCodigo(mensaje.getDatos().get("codigoSala"));
        IProxy proxySolicitante = (IProxy) mensaje.getDatos().get("proxy");
        GestorSalas.SalaDisponible sala = gestorSalas.obtenerSala(codigoSala);

        System.out.println("[COMANDO-UNIRSE-PARTIDA] " + nombreInviado + "intenta unirse ala partida" + codigoSala);
        MensajeDTO respuesta = new MensajeDTO();
        respuesta.setRemitente("SERVIDOR");

        if (sala == null) {
            respuesta.setTipo("ERROR_UNIRSE");
            respuesta.getDatos().put("motivo", "La sala no existe o el codigo es incorrecto");
            enviarRespuesta(proxySolicitante, respuesta);
            return;
        }

        if (!gestorSalas.unirJugador(codigoSala)) {
            respuesta.setTipo("ERROR_UNIRSE");
            respuesta.getDatos().put("motivo", "La sala ya esta llena");
            enviarRespuesta(proxySolicitante, respuesta);
            return;
        }

        sala = gestorSalas.obtenerSala(codigoSala);
        List<Map<String, String>> listaJugadoresConAvatar = construirListaJugadores();

        respuesta.setTipo("UNIDO_EXITO");
        respuesta.getDatos().put("codigoSala", codigoSala);
        respuesta.getDatos().put("nombre", nombreInviado);
        respuesta.getDatos().put("nombreSala", sala.getNombreSala());
        respuesta.getDatos().put("host", sala.getHost());
        respuesta.getDatos().put("limiteJugadores", sala.getLimiteJugadores());
        respuesta.getDatos().put("jugadoresActuales", sala.getJugadoresActuales());
        respuesta.getDatos().put("jugadores", listaJugadoresConAvatar);
        respuesta.getDatos().put("esHost", false);
        enviarRespuesta(proxySolicitante, respuesta);

        MensajeDTO notificacionLista = new MensajeDTO();
        notificacionLista.setTipo("LISTA_ACTUALIZADA");
        notificacionLista.setRemitente("SERVIDOR");
        Map<String, Object> datosLista = new HashMap<>();
        datosLista.put("jugadores", listaJugadoresConAvatar);
        notificacionLista.setDatos(datosLista);

        for (NodoCliente n : ManejadorNodos.obtenerNodosConectados()) {
            n.enviarMensaje(notificacionLista);
        }

        notificarPartidasDisponibles();
    }

    private List<Map<String, String>> construirListaJugadores() {
        List<Map<String, String>> listaJugadoresConAvatar = new java.util.ArrayList<>();
        for (NodoCliente n : ManejadorNodos.obtenerNodosConectados()) {
            Map<String, String> datosJugador = new HashMap<>();
            datosJugador.put("nombre", n.getNombre());
            if (n.getAvatar() != null && !n.getAvatar().equals("no hay")) {
                datosJugador.put("avatar", n.getAvatar());
            } else {
                datosJugador.put("avatar", "pfp");
            }
            datosJugador.put("estaListo", String.valueOf(n.isEstaListo()));
            listaJugadoresConAvatar.add(datosJugador);
        }
        return listaJugadoresConAvatar;
    }

    private String obtenerNombreJugador(Object valor) {
        if (valor instanceof String texto && !texto.isBlank()) {
            return texto.trim();
        }
        return "Invitado";
    }

    private String normalizarCodigo(Object valor) {
        if (valor instanceof String texto) {
            return texto.trim().toUpperCase();
        }
        return "";
    }

    private void enviarRespuesta(IProxy proxySolicitante, MensajeDTO respuesta) {
        if (proxySolicitante != null) {
            proxySolicitante.enviarMensaje(respuesta);
        }
    }

    private void notificarPartidasDisponibles() {
        MensajeDTO listaPartidas = new MensajeDTO();
        listaPartidas.setTipo("LISTA_PARTIDAS_DISPONIBLES");
        listaPartidas.setRemitente("SERVIDOR");

        Map<String, Object> datos = new HashMap<>();
        datos.put("partidas", gestorSalas.obtenerSalasSerializables());
        listaPartidas.setDatos(datos);

        for (NodoCliente nodo : ManejadorNodos.obtenerNodosConectados()) {
            nodo.enviarMensaje(listaPartidas);
        }
    }
}
