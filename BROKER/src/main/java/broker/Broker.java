package broker;

import Interfacez.IBroker;
import dtos.MensajeDTO;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Bus de eventos publicar/suscribir del patron Broker.
 *
 * Se usa tanto del lado servidor (SERVER-PROXY publica lo que llega de la red)
 * como del lado cliente (CLIENTE-PROXY publica lo que responde el servidor), por
 * eso este modulo no depende del dominio ni de la logica de juego.
 *
 * Los suscriptores se guardan en CopyOnWriteArrayList porque hay un hilo por
 * cliente publicando de forma concurrente: con ArrayList, iterar en publicar()
 * mientras otro hilo se suscribe lanzaba ConcurrentModificationException.
 */
public class Broker implements IBroker {

    private final Map<String, List<Consumer<MensajeDTO>>> suscriptores = new ConcurrentHashMap<>();

    @Override
    public void subscribirse(String tipoEvento, Consumer<MensajeDTO> manejador) {
        if (tipoEvento == null || manejador == null) {
            return;
        }
        suscriptores.computeIfAbsent(tipoEvento, k -> new CopyOnWriteArrayList<>()).add(manejador);
    }

    @Override
    public void desuscribirse(String tipoEvento, Consumer<MensajeDTO> manejador) {
        if (tipoEvento == null || manejador == null) {
            return;
        }
        List<Consumer<MensajeDTO>> manejadores = suscriptores.get(tipoEvento);
        if (manejadores != null) {
            manejadores.remove(manejador);
        }
    }

    @Override
    public void publicar(String tipoEvento, MensajeDTO mensaje) {
        if (tipoEvento == null) {
            return;
        }
        List<Consumer<MensajeDTO>> interesados = suscriptores.get(tipoEvento);
        if (interesados == null || interesados.isEmpty()) {
            return;
        }
        // Un suscriptor que falla no debe impedir la entrega a los demas.
        for (Consumer<MensajeDTO> consumidor : interesados) {
            try {
                consumidor.accept(mensaje);
            } catch (Exception ex) {
                System.err.println("[Broker] Suscriptor de '" + tipoEvento + "' fallo: " + ex);
            }
        }
    }
}
