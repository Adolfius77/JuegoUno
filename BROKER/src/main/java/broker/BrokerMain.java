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
        // 1. Inicialización de componentes base
        ISerializador miSerializador = new serializador();
        IProxyFactory proxyFactory = new ServerProxyFactory();
        
        Broker broker = new Broker(9090, miSerializador, proxyFactory);

        Consumer<MensajeDTO> moduloJuego = mensaje -> {
            System.out.println("[MÓDULO JUEGO] Recibi el evento: " + mensaje.getTipo() 
                + " de: " + mensaje.getRemitente() 
                + " a las: " + mensaje.getTimestamp());
        };

        Consumer<MensajeDTO> moduloNotificaciones = mensaje -> {
            System.out.println("[MÓDULO NOTIFICACIONES] Alerta en pantalla: ¡Alguien gritó UNO!");
        };

        System.out.println("Registrando suscriptores en el Broker...");
        broker.subscribirse("JUGAR_CARTA", moduloJuego);
        broker.subscribirse("ROBAR_CARTA", moduloJuego);
        broker.subscribirse("GRITO_UNO", moduloNotificaciones);

        broker.iniciarServidor();
        
        System.out.println("======================================");
        System.out.println("   BROKER DE UNO - ITSON ACTIVADO     ");
        System.out.println("   Escuchando en puerto: 9090         ");
        System.out.println("======================================");

        System.out.println("Publicando evento de prueba...");
        MensajeDTO mensajePrueba = new MensajeDTO();
        mensajePrueba.setTipo("JUGAR_CARTA");
        mensajePrueba.setRemitente("Sistema_Prueba");
        broker.publicar("JUGAR_CARTA", mensajePrueba);


        while (true) {
            try {
                Thread.sleep(60000); 
            } catch (InterruptedException e) {
                System.out.println("Broker detenido.");
                break;
            }
        }
    }
}