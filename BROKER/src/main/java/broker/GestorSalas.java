package broker;

import Entidades.Lobby;
import Entidades.Logica.Partida;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GestorSalas {

    private static GestorSalas instance;
    private final Map<String, Lobby> salas = new ConcurrentHashMap<>();
    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();
    // Canal personal de cada jugador (nombreJugador → canal broker)
    private final Map<String, String> canalesJugador = new ConcurrentHashMap<>();
    // Jugadores listos por sala (codigoSala → set de nombres)
    private final Map<String, Set<String>> listosEnSala = new ConcurrentHashMap<>();

    private GestorSalas() {
    }

    public static GestorSalas getInstance() {
        if (instance == null) {
            instance = new GestorSalas();
        }
        return instance;
    }

    // ── Salas ────────────────────────────────────────────────────────────────
    public String crearSala(String nombreSala, int limite) {
        String codigo = nombreSala.toUpperCase().replaceAll("\\s+", "_");
        Lobby lobby = new Lobby();
        lobby.setLimiteJugadores(limite);
        salas.put(codigo, lobby);
        System.out.println("[GestorSalas] Sala creada: " + codigo);
        return codigo;
    }

    public Lobby getSala(String codigo) {
        return salas.get(codigo.toUpperCase());
    }

    public boolean existeSala(String codigo) {
        return salas.containsKey(codigo.toUpperCase());
    }

    public void eliminarSala(String codigo) {
        salas.remove(codigo.toUpperCase());
    }

    public Map<String, Lobby> getSalas() {
        return salas;
    }

    // ── Partidas ─────────────────────────────────────────────────────────────
    public void guardarPartida(String codigoSala, Partida partida) {
        partidas.put(codigoSala.toUpperCase(), partida);
    }

    public Partida getPartida(String codigoSala) {
        return partidas.get(codigoSala.toUpperCase());
    }

    // ── Canales (broker) ─────────────────────────────────────────────────────
    /**
     * Registra el canal personal de un jugador para poder enviarle mensajes.
     */
    public void registrarCanalJugador(String nombreJugador, String canal) {
        canalesJugador.put(nombreJugador.toLowerCase(), canal);
    }

    /**
     * Devuelve el canal personal de un jugador.
     */
    public String getCanalJugador(String nombreJugador) {
        return canalesJugador.get(nombreJugador.toLowerCase());
    }

    /**
     * Devuelve los canales de todos los jugadores que pertenecen a una sala,
     * consultando el Lobby para obtener sus nombres.
     */
    public List<String> getCanalesDeSala(String codigoSala) {
        Lobby sala = getSala(codigoSala);
        if (sala == null) {
            return Collections.emptyList();
        }

        List<String> canales = new ArrayList<>();
        for (String nombre : sala.getNombreJugadores()) {
            String canal = canalesJugador.get(nombre.toLowerCase());
            if (canal != null) {
                canales.add(canal);
            }
        }
        return canales;
    }

    // ── Listos ───────────────────────────────────────────────────────────────
    /**
     * Marca a un jugador como listo en su sala.
     *
     * @return true si TODOS los jugadores de la sala ya están listos.
     */
    public boolean marcarListo(String codigoSala, String nombreJugador) {
        String key = codigoSala.toUpperCase();
        listosEnSala.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet())
                .add(nombreJugador.toLowerCase());

        Lobby sala = getSala(codigoSala);
        if (sala == null) {
            return false;
        }

        int totalEnSala = sala.getNombreJugadores().size();
        int totalListos = listosEnSala.get(key).size();

        System.out.println("[GestorSalas] Listos en " + key + ": " + totalListos + "/" + totalEnSala);
        return totalEnSala >= 2 && totalListos == totalEnSala;
    }

    /**
     * Limpia el estado de "listos" de una sala (útil al iniciar la partida).
     */
    public void limpiarListos(String codigoSala) {
        listosEnSala.remove(codigoSala.toUpperCase());
    }
}
