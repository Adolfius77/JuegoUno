package Nodos;

import dtos.MensajeDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro de sesiones conectadas, indexado por idSesion.
 *
 * Antes la clave era el nombre del jugador, que es mutable: al confirmar el
 * registro habia que sacar y reinsertar el nodo, y dos clientes que compartian
 * entrada terminaban fundidos en uno solo.
 */
public class ManejadorNodos {

    private final Map<String, NodoCliente> nodosPorSesion = new ConcurrentHashMap<>();

    public void registrarNuevoJugador(NodoCliente nuevoNodo) {
        if (nuevoNodo != null) {
            nodosPorSesion.put(nuevoNodo.getIdSesion(), nuevoNodo);
            System.out.println("ManejadorNodos: Jugador registrado -> " + nuevoNodo.getIdSesion()
                    + " (" + nuevoNodo.getNombre() + ")");
        }
    }

    public List<NodoCliente> obtenerNodosConectados() {
        return new ArrayList<>(nodosPorSesion.values());
    }

    public NodoCliente obtenerNodoPorSesion(String idSesion) {
        return idSesion == null ? null : nodosPorSesion.get(idSesion);
    }

    public NodoCliente obtenerNodoPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (nombre.equalsIgnoreCase(nodo.getNombre())) {
                return nodo;
            }
        }
        return null;
    }

    /** true si el nombre ya lo usa otra sesion distinta de la indicada. */
    public boolean nombreEnUsoPorOtro(String nombre, String idSesionPropia) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (!nodo.getIdSesion().equals(idSesionPropia)
                    && nombre.trim().equalsIgnoreCase(nodo.getNombre())) {
                return true;
            }
        }
        return false;
    }

    public void eliminarNodo(String idSesion) {
        if (idSesion == null) {
            return;
        }
        NodoCliente nodo = nodosPorSesion.remove(idSesion);
        if (nodo != null) {
            System.out.println("Nodo eliminado exitosamente del manejador: " + idSesion
                    + " (" + nodo.getNombre() + ")");
        }
    }

    public void enviarNodo(String idSesion, MensajeDTO mensaje) {
        NodoCliente nodo = obtenerNodoPorSesion(idSesion);
        if (nodo != null) {
            nodo.enviarMensaje(mensaje);
        } else {
            System.out.println("Intento de envio fallido. No se encontro la sesion: " + idSesion);
        }
    }

    public List<String> obtenerNombresDeNodosConectados() {
        List<String> nombres = new ArrayList<>();
        for (NodoCliente nodo : nodosPorSesion.values()) {
            nombres.add(nodo.getNombre());
        }
        return nombres;
    }

    /**
     * Difunde a TODAS las sesiones del servidor. Reservar para eventos de
     * alcance global, como la lista de partidas disponibles. Para lo que ocurre
     * dentro de una partida usar notificarASala.
     */
    public void notificarATodos(MensajeDTO mensaje) {
        for (NodoCliente nodo : nodosPorSesion.values()) {
            nodo.enviarMensaje(mensaje);
        }
    }

    // --- Operaciones por sala ---------------------------------------------
    // Antes todo era global: iniciar una partida tomaba a todos los conectados
    // del servidor y cada evento se difundia a todo el mundo, aunque estuviera
    // en otra sala.

    public List<NodoCliente> obtenerNodosDeSala(String codigoSala) {
        List<NodoCliente> deLaSala = new ArrayList<>();
        if (codigoSala == null) {
            return deLaSala;
        }
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (nodo.estaEnSala(codigoSala)) {
                deLaSala.add(nodo);
            }
        }
        return deLaSala;
    }

    public List<String> obtenerNombresDeSala(String codigoSala) {
        List<String> nombres = new ArrayList<>();
        for (NodoCliente nodo : obtenerNodosDeSala(codigoSala)) {
            nombres.add(nodo.getNombre());
        }
        return nombres;
    }

    /**
     * Nombre -> avatar de cada jugador de la sala, en orden de turno.
     *
     * El avatar solo vivia aqui, en la sesion: al arrancar la partida se
     * enviaban unicamente los nombres, asi que el tablero nunca recibia la foto.
     */
    /**
     * Los jugadores de la sala tal y como los espera la pantalla de espera:
     * nombre, avatar y si esta listo.
     *
     * Vivia copiado en cada comando que difundia la lista, y por eso el aviso
     * de que alguien se habia ido nunca se llego a escribir: no habia de donde
     * sacar la lista sin copiarla una vez mas.
     */
    public List<java.util.Map<String, String>> construirListaJugadores(String codigoSala) {
        List<java.util.Map<String, String>> lista = new ArrayList<>();
        for (NodoCliente nodo : obtenerNodosDeSala(codigoSala)) {
            java.util.Map<String, String> jugador = new java.util.HashMap<>();
            jugador.put("nombre", nodo.getNombre());
            String avatar = nodo.getAvatar();
            jugador.put("avatar", (avatar != null && !avatar.equals("no hay")) ? avatar : "pfp");
            jugador.put("estaListo", String.valueOf(nodo.isEstaListo()));
            lista.add(jugador);
        }
        return lista;
    }

    /** Difunde a la sala la lista de quien sigue dentro. */
    public void difundirListaDeSala(String codigoSala) {
        if (codigoSala == null) {
            return;
        }
        MensajeDTO aviso = new MensajeDTO();
        aviso.setTipo("LISTA_ACTUALIZADA");
        aviso.setRemitente("SERVIDOR");
        aviso.getDatos().put("jugadores", construirListaJugadores(codigoSala));
        notificarASala(codigoSala, aviso);
    }

    public java.util.Map<String, String> obtenerAvataresDeSala(String codigoSala) {
        java.util.Map<String, String> avatares = new java.util.LinkedHashMap<>();
        for (NodoCliente nodo : obtenerNodosDeSala(codigoSala)) {
            String avatar = nodo.getAvatar();
            boolean sinAvatar = avatar == null || avatar.isBlank() || "no hay".equals(avatar);
            avatares.put(nodo.getNombre(), sinAvatar ? "" : avatar);
        }
        return avatares;
    }

    public void notificarASala(String codigoSala, MensajeDTO mensaje) {
        for (NodoCliente nodo : obtenerNodosDeSala(codigoSala)) {
            nodo.enviarMensaje(mensaje);
        }
    }

    /** Sesiones que todavia no entraron a ninguna sala. */
    public List<NodoCliente> obtenerNodosEnLobby() {
        List<NodoCliente> enLobby = new ArrayList<>();
        for (NodoCliente nodo : nodosPorSesion.values()) {
            if (nodo.getCodigoSala() == null) {
                enLobby.add(nodo);
            }
        }
        return enLobby;
    }

    public boolean estanTodosListosEnSala(String codigoSala) {
        List<NodoCliente> deLaSala = obtenerNodosDeSala(codigoSala);
        if (deLaSala.isEmpty()) {
            return false;
        }
        for (NodoCliente nodo : deLaSala) {
            if (!nodo.isEstaListo()) {
                return false;
            }
        }
        return true;
    }
}
