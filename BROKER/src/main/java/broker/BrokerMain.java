package broker;

import Interfacez.IBroker;
import Interfacez.IProxyFactory;
import Interfacez.ISerializador;
import dtos.MensajeDTO;
import fabricas.ServerProxyFactory;
import serealizador.serializador;

import java.util.function.Consumer;

public class BrokerMain {

    public static void main(String[] args) {


        ISerializador miSerializador = new serializador();
        IProxyFactory proxyFactory = new ServerProxyFactory();
        IBroker broker = new Broker(9000, miSerializador,proxyFactory);

        Consumer<MensajeDTO> moduloJuego = mensaje -> {
            System.out.println("[MÓDULO JUEGO] Recibí el evento: " + mensaje.getTipo() + mensaje.getRemitente() + mensaje.getTimestamp());
        };

        Consumer<MensajeDTO> moduloNotificaciones = mensaje -> {
            System.out.println("[MÓDULO NOTIFICACIONES] Alerta en pantalla: Alguien gritó UNO!");
        };

        System.out.println("registrando subscriptores");
        broker.subscribirse("JUGAR_CARTA", moduloJuego);
        broker.subscribirse("ROBAR_CARTA", moduloJuego);
        broker.subscribirse("GRITO_UNO", moduloNotificaciones);

        System.out.println("publicando eventos");
        MensajeDTO mensaje = new MensajeDTO();
        mensaje.setTipo("JUGAR_CARTA");
        broker.publicar("JUGAR_CARTA", mensaje);
    }
}