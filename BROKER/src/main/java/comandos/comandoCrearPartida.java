package comandos;

import Interfacez.IProxy;
import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import red.GestorSalas;

public class comandoCrearPartida implements IComandoServidor {

    private final ManejadorNodos ManejadorNodos;
    private final GestorSalas gestorSalas;

    public comandoCrearPartida(Nodos.ManejadorNodos manejadorNodos, GestorSalas gestorSalas) {
        ManejadorNodos = manejadorNodos;
        this.gestorSalas = gestorSalas;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        System.out.println("[COMANDO-CREAR-PARTIDA] procesado peticion para crear partida");
        String nombreHost = obtenerTexto(mensaje.getDatos().get("nombre"), "Host");
        String nombreSala = obtenerTexto(mensaje.getDatos().get("nombreSala"), "Sala de " + nombreHost);
        int limiteJugadores = obtenerLimite(mensaje.getDatos().get("limiteJugadores"));
        String codigoSala = generarCodigoSala();
        IProxy proxySolicitante = (IProxy) mensaje.getDatos().get("proxy");
        GestorSalas.SalaDisponible salaCreada = gestorSalas.registrarSala(codigoSala, nombreSala, nombreHost, limiteJugadores);
        List<java.util.Map<String, String>> listaJugadoresConAvatar = construirListaJugadores();

        MensajeDTO respuesto = new MensajeDTO();
        respuesto.setTipo("SALA_CREADA");
        respuesto.setRemitente("SERVIDOR");
        respuesto.getDatos().put("codigoSala", codigoSala);
        respuesto.getDatos().put("nombreSala", salaCreada.getNombreSala());
        respuesto.getDatos().put("nombre", nombreHost);
        respuesto.getDatos().put("host", salaCreada.getHost());
        respuesto.getDatos().put("limiteJugadores", salaCreada.getLimiteJugadores());
        respuesto.getDatos().put("jugadoresActuales", salaCreada.getJugadoresActuales());
        respuesto.getDatos().put("jugadores", listaJugadoresConAvatar);
        respuesto.getDatos().put("esHost", true);

        if (proxySolicitante != null) {
            proxySolicitante.enviarMensaje(respuesto);
        }

        MensajeDTO notificacionLista = new MensajeDTO();
        notificacionLista.setTipo("LISTA_ACTUALIZADA");
        notificacionLista.setRemitente("SERVIDOR");

        java.util.Map<String, Object> datosLista = new HashMap<>();
        datosLista.put("jugadores", listaJugadoresConAvatar);
        notificacionLista.setDatos(datosLista);

        for (NodoCliente n : ManejadorNodos.obtenerNodosConectados()) {
            n.enviarMensaje(notificacionLista);
        }

        notificarPartidasDisponibles();
    }

    private String generarCodigoSala() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } while (gestorSalas.existeSala(codigo));
        return codigo;
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

    private String obtenerTexto(Object valor, String defecto) {
        if (valor instanceof String texto && !texto.isBlank()) {
            return texto.trim();
        }
        return defecto;
    }

    private int obtenerLimite(Object valor) {
        int limite = 4;
        if (valor instanceof Number numero) {
            limite = numero.intValue();
        } else if (valor instanceof String texto && !texto.isBlank()) {
            try {
                limite = Integer.parseInt(texto.trim());
            } catch (NumberFormatException ex) {
                limite = 4;
            }
        }
        if (limite < 2) {
            return 2;
        }
        if (limite > 4) {
            return 4;
        }
        return limite;
    }

    private void notificarPartidasDisponibles() {
        MensajeDTO listaPartidas = new MensajeDTO();
        listaPartidas.setTipo("LISTA_PARTIDAS_DISPONIBLES");
        listaPartidas.setRemitente("SERVIDOR");

        java.util.Map<String, Object> datos = new HashMap<>();
        datos.put("partidas", gestorSalas.obtenerSalasSerializables());
        listaPartidas.setDatos(datos);

        for (NodoCliente n : ManejadorNodos.obtenerNodosConectados()) {
            n.enviarMensaje(listaPartidas);
        }
    }
}
