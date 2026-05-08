package red;

import Entidades.Lobby;
import Entidades.Logica.Partida;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GestorSalas {

    private static GestorSalas instance;
    private final Map<String, Lobby> salas = new ConcurrentHashMap<>();
    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();

    private GestorSalas() {
    }

    public static GestorSalas getInstance() {
        if (instance == null) {
            instance = new GestorSalas();
        }
        return instance;
    }

    public String crearSala(String nombreSala, int limite) {
        String codigo = nombreSala.toUpperCase().replaceAll("\\s+", "_");
        Lobby lobby = new Lobby();
        lobby.setLimiteJugadores(limite);
        salas.put(codigo, lobby);
        System.out.println("[GestorSalas] Sala creada: " + codigo);
        return codigo;
    }

    public void guardarPartida(String codigoSala, Partida partida) {
        partidas.put(codigoSala.toUpperCase(), partida);
    }

    public Partida getPartida(String codigoSala) {
        return partidas.get(codigoSala.toUpperCase());
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

}
