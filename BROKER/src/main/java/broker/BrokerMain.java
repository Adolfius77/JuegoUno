package broker;


import dtos.MensajeDTO;
import dtos.MensajeRegistroDTO;

public class BrokerMain {
    public static void main(String[] args) {
        Broker broker = new Broker(8080,4);
        broker.iniciarServidor();

        broker.subscribirse("INICIO_JUEGO", msg ->{
            System.out.println("iniciar juego recibido" + msg);
        });
        broker.subscribirse("TURNO", msg ->{
            System.out.println("turno recibido" + msg);
        });
        try{
            Thread.sleep(2000);
            System.out.printf("publicando eventos");
            broker.publicar("INICIO_JUEGO", new MensajeDTO("broker","juego iniciado") {
            });
            broker.publicar("TURNO", new MensajeDTO("broker","juego turno") {

            });
            Thread.sleep(5000);
            System.out.println("si llego aqui es por que si jala");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
