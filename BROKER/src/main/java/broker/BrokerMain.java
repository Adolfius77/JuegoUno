package broker;


import Interfacez.IBroker;
import dtos.MensajeDTO;
import dtos.MensajeRegistroDTO;
import interfaces.ISerializador;

import java.util.function.Consumer;


public class BrokerMain {
    private static ISerializador serializador;
    public static void main(String[] args) {
        IBroker broker = new Broker();
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
