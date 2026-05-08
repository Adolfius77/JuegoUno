package red;


import Interfacez.ISerializador;
import Lector.LectorConfiguracion;
import serealizador.serializador;

public class servidorMain {

    public static void main(String[] args) {
        LectorConfiguracion config = new LectorConfiguracion();
        serializador seri = new serializador();
        int puerto = config.getPuertoServidor();
        String ip = config.getIpServidor();
        Servidor servidor = new Servidor(puerto,ip,seri);
        servidor.iniciar();

        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("el hilo principal se interrumpio");
        }
        
    }
}
