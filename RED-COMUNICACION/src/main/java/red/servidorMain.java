package red;


import Lector.LectorConfiguracion;

public class servidorMain {

    public static void main(String[] args) {
        LectorConfiguracion config = new LectorConfiguracion();
        int puerto = config.getPuertoServidor();
        String ip = config.getIpServidor();
        Servidor servidor = new Servidor(puerto,ip);
        servidor.iniciar();

        try {
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("el hilo principal se interrumpio");
        }
        
    }
}
