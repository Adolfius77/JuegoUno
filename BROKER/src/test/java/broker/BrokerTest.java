package broker;

import dtos.MensajeDTO;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {

    private MensajeDTO mensaje(String tipo) {
        MensajeDTO m = new MensajeDTO();
        m.setTipo(tipo);
        return m;
    }

    @Test
    void variosSuscriptoresRecibenElMismoEvento() {
        // Es el defecto que tenia el cliente: con un unico receptor, el segundo
        // suscriptor desplazaba al primero.
        Broker broker = new Broker();
        List<String> recibidos = new CopyOnWriteArrayList<>();

        broker.subscribirse("JUGAR_CARTA", m -> recibidos.add("A"));
        broker.subscribirse("JUGAR_CARTA", m -> recibidos.add("B"));
        broker.publicar("JUGAR_CARTA", mensaje("JUGAR_CARTA"));

        assertEquals(List.of("A", "B"), recibidos);
    }

    @Test
    void soloRecibenLosSuscritosAEseTipo() {
        Broker broker = new Broker();
        List<String> recibidos = new CopyOnWriteArrayList<>();

        broker.subscribirse("JUGAR_CARTA", m -> recibidos.add("carta"));
        broker.subscribirse("GRITO_UNO", m -> recibidos.add("uno"));
        broker.publicar("GRITO_UNO", mensaje("GRITO_UNO"));

        assertEquals(List.of("uno"), recibidos);
    }

    @Test
    void desuscribirseDetieneLaEntrega() {
        Broker broker = new Broker();
        List<String> recibidos = new CopyOnWriteArrayList<>();
        Consumer<MensajeDTO> manejador = m -> recibidos.add("A");

        broker.subscribirse("TURNO", manejador);
        broker.publicar("TURNO", mensaje("TURNO"));
        broker.desuscribirse("TURNO", manejador);
        broker.publicar("TURNO", mensaje("TURNO"));

        assertEquals(1, recibidos.size(), "tras desuscribirse no debe recibir mas");
    }

    @Test
    void unSuscriptorQueFallaNoCortaLaEntregaALosDemas() {
        Broker broker = new Broker();
        List<String> recibidos = new CopyOnWriteArrayList<>();

        broker.subscribirse("TURNO", m -> { throw new IllegalStateException("falla"); });
        broker.subscribirse("TURNO", m -> recibidos.add("B"));
        broker.publicar("TURNO", mensaje("TURNO"));

        assertEquals(List.of("B"), recibidos);
    }

    @Test
    void publicarEnTipoSinSuscriptoresNoFalla() {
        Broker broker = new Broker();
        assertDoesNotThrow(() -> broker.publicar("NADIE_ESCUCHA", mensaje("NADIE_ESCUCHA")));
    }

    @Test
    void suscribirseDurantePublicarNoLanzaConcurrentModification() throws Exception {
        // El servidor tiene un hilo por cliente: publicar mientras otro hilo se
        // suscribe rompia con ArrayList.
        Broker broker = new Broker();
        CountDownLatch listo = new CountDownLatch(1);

        broker.subscribirse("TURNO", m -> broker.subscribirse("TURNO", otro -> { }));
        broker.subscribirse("TURNO", m -> listo.countDown());

        assertDoesNotThrow(() -> broker.publicar("TURNO", mensaje("TURNO")));
        assertTrue(listo.await(1, TimeUnit.SECONDS));
    }
}
