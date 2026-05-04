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
        IBroker broker = new Broker(9090, miSerializador,proxyFactory);

        Consumer<MensajeDTO> moduloJuego = mensaje -> {
            System.out.println("[MODULO JUEGO] Recibi el evento: " + mensaje.getTipo() + mensaje.getRemitente() + mensaje.getTimestamp());
        };

        Consumer<MensajeDTO> moduloNotificaciones = mensaje -> {
            System.out.println("[MÓDULO NOTIFICACIONES] Alerta en pantalla: Alguien grito UNO!");
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