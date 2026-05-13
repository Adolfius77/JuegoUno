package comandos;

import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

public class comandoCrearPartida implements IComandoServidor {

    private final ManejadorNodos ManejadorNodos;

    public comandoCrearPartida(Nodos.ManejadorNodos manejadorNodos) {
        ManejadorNodos = manejadorNodos;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        System.out.println("[COMANDO-CREAR-PARTIDA] procesado peticion para crear partida");
        String nombreHost = (String) mensaje.getDatos().get("nombre");
        String codigoSala = generarCodigoSala();
        IProxy proxySolicitante = (IProxy) mensaje.getDatos().get("proxy");
        List<java.util.Map<String, String>> listaJugadoresConAvatar = construirListaJugadores();

        MensajeDTO respuesto = new MensajeDTO();
        respuesto.setTipo("SALA_CREADA");
        respuesto.setRemitente("SERVIDOR");
        respuesto.getDatos().put("codigoSala", codigoSala);
        respuesto.getDatos().put("nombre", nombreHost);
        respuesto.getDatos().put("jugadores", listaJugadoresConAvatar);
        respuesto.getDatos().put("esHost", true);

        if (proxySolicitante != null) {
            proxySolicitante.enviarMensaje(respuesto);
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

    private String generarCodigoSala() {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private List<java.util.Map<String, String>> construirListaJugadores() {
        List<java.util.Map<String, String>> listaJugadoresConAvatar = new ArrayList<>();
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
        return listaJugadoresConAvatar;
    }
}
