package red;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GestorSalas {

    private final Map<String, SalaDisponible> salasPorCodigo = new ConcurrentHashMap<>();

    public SalaDisponible registrarSala(String codigoSala, String nombreSala, String host, int limiteJugadores) {
        String codigoNormalizado = normalizarCodigo(codigoSala);
        int limiteNormalizado = normalizarLimite(limiteJugadores);
        String nombreSalaNormalizado = normalizarNombreSala(nombreSala, host);

        SalaDisponible sala = new SalaDisponible(
                codigoNormalizado,
                nombreSalaNormalizado,
                host != null ? host : "",
                limiteNormalizado
        );
        salasPorCodigo.put(codigoNormalizado, sala);
        return sala;
    }

    public boolean existeSala(String codigoSala) {
        return salasPorCodigo.containsKey(normalizarCodigo(codigoSala));
    }

    public SalaDisponible obtenerSala(String codigoSala) {
        return salasPorCodigo.get(normalizarCodigo(codigoSala));
    }

    public boolean unirJugador(String codigoSala) {
        SalaDisponible sala = obtenerSala(codigoSala);
        if (sala == null) {
            return false;
        }
        return sala.unirJugador();
    }

    public List<Map<String, Object>> obtenerSalasSerializables() {
        List<Map<String, Object>> serializables = new ArrayList<>();
        for (SalaDisponible sala : salasPorCodigo.values()) {
            serializables.add(sala.aMapaSerializable());
        }

        serializables.sort(Comparator.comparing(mapa -> String.valueOf(mapa.get("codigoSala"))));
        return serializables;
    }

    private String normalizarNombreSala(String nombreSala, String host) {
        if (nombreSala != null && !nombreSala.isBlank()) {
            return nombreSala.trim();
        }
        String hostSeguro = (host != null && !host.isBlank()) ? host.trim() : "Host";
        return "Sala de " + hostSeguro;
    }

    private String normalizarCodigo(String codigoSala) {
        return codigoSala == null ? "" : codigoSala.trim().toUpperCase();
    }

    private int normalizarLimite(int limiteJugadores) {
        if (limiteJugadores < 2) {
            return 2;
        }
        if (limiteJugadores > 4) {
            return 4;
        }
        return limiteJugadores;
    }

    public static final class SalaDisponible {

        private final String codigoSala;
        private final String nombreSala;
        private final String host;
        private final int limiteJugadores;
        private final AtomicInteger jugadoresActuales;

        private SalaDisponible(String codigoSala, String nombreSala, String host, int limiteJugadores) {
            this.codigoSala = codigoSala;
            this.nombreSala = nombreSala;
            this.host = host;
            this.limiteJugadores = limiteJugadores;
            this.jugadoresActuales = new AtomicInteger(1);
        }

        public boolean unirJugador() {
            while (true) {
                int jugadores = jugadoresActuales.get();
                if (jugadores >= limiteJugadores) {
                    return false;
                }
                if (jugadoresActuales.compareAndSet(jugadores, jugadores + 1)) {
                    return true;
                }
            }
        }

        public boolean estaLlena() {
            return jugadoresActuales.get() >= limiteJugadores;
        }

        public int getJugadoresActuales() {
            return jugadoresActuales.get();
        }

        public int getLimiteJugadores() {
            return limiteJugadores;
        }

        public String getCodigoSala() {
            return codigoSala;
        }

        public String getNombreSala() {
            return nombreSala;
        }

        public String getHost() {
            return host;
        }

        public Map<String, Object> aMapaSerializable() {
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("codigoSala", codigoSala);
            mapa.put("nombreSala", nombreSala);
            mapa.put("host", host);
            mapa.put("jugadoresActuales", getJugadoresActuales());
            mapa.put("limiteJugadores", limiteJugadores);
            return mapa;
        }
    }
}
