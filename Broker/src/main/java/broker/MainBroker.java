package broker;


import factorys.enrutadorBrokerFactory;
import red.Servidor;

public class MainBroker {
    public static void main(String[] args) {
        int puerto = 8080;

        System.out.println("iniciado servidor broker");
        EnrutadorBroker enrutador = enrutadorBrokerFactory.CrearEnrutadorBroker();

        Servidor servidor = new Servidor(puerto, enrutador);
        servidor.iniciar();
    }
}
