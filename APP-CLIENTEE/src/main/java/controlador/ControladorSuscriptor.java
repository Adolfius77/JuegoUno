package controlador;

import Interfacez.IBroker;
import dtos.MensajeDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base de los controladores del cliente que reaccionan a eventos de red.
 *
 * Antes cada controlador llamaba a ClienteProxy.setReceptor(...), que admitia un
 * unico receptor: los cuatro controladores se pisaban entre si y solo el ultimo
 * registrado recibia eventos. Ahora todos se suscriben al mismo IBroker, que es
 * el mismo bus que usa el servidor.
 *
 * Las referencias a los manejadores se guardan porque un method reference
 * (this::metodo) crea un objeto distinto en cada evaluacion: sin conservarlo no
 * se podria desuscribir despues.
 */
public abstract class ControladorSuscriptor {

    private final IBroker broker;
    private final Map<String, Consumer<MensajeDTO>> suscripciones = new LinkedHashMap<>();

    protected ControladorSuscriptor(IBroker broker) {
        if (broker == null) {
            throw new IllegalArgumentException("El broker del cliente es obligatorio.");
        }
        this.broker = broker;
    }

    /** Registra un manejador para un tipo de evento y recuerda como desuscribirlo. */
    protected final void suscribir(String tipoEvento, Consumer<MensajeDTO> manejador) {
        if (tipoEvento == null || manejador == null) {
            return;
        }
        Consumer<MensajeDTO> anterior = suscripciones.put(tipoEvento, manejador);
        if (anterior != null) {
            broker.desuscribirse(tipoEvento, anterior);
        }
        broker.subscribirse(tipoEvento, manejador);
    }

    /**
     * Cancela todas las suscripciones de este controlador. Se llama cuando el
     * controlador cede la pantalla a otro: sin esto, el controlador anterior
     * seguiria reaccionando a los mismos eventos que el nuevo.
     */
    public final void liberar() {
        suscripciones.forEach(broker::desuscribirse);
        suscripciones.clear();
    }

    protected final IBroker getBroker() {
        return broker;
    }
}
