package broker;

import Interfacez.IBroker;
import dtos.MensajeDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Broker implements IBroker {

    private Map<String, List<Consumer<MensajeDTO>>> suscriptores = new ConcurrentHashMap<>();

    public Broker() {
        this.suscriptores = new ConcurrentHashMap<>();
    }

    @Override
    public void subscribirse(String tipoEvento, Consumer<MensajeDTO> manejador) {
        suscriptores.computeIfAbsent(tipoEvento, k -> new ArrayList<>()).add(manejador);
    }

    @Override
    public void desuscribirse(String tipoEvento, Consumer<MensajeDTO> manejador) {
        List<Consumer<MensajeDTO>> manejadores = suscriptores.get(tipoEvento);
        if (manejadores != null) {
            manejadores.remove(manejador);
        }
    }

    @Override
    public void publicar(String tipoEvento, MensajeDTO mensaje) {
        List<Consumer<MensajeDTO>> interesados = suscriptores.getOrDefault(tipoEvento, new ArrayList<>());
        for (Consumer<MensajeDTO> consumidor : interesados) {
            consumidor.accept(mensaje);
        }
    }
}
