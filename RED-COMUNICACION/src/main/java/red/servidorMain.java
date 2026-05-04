package red;


import Lector.LectorConfiguracion;
import dtos.paqueteRedDTO;
import interfaces.IReceptorMensajes;

public class servidorMain {

    public static void main(String[] args) throws InterruptedException {
        IReceptorMensajes receptor = new IReceptorMensajes() {
            @Override
            public void procesarMensaje(paqueteRedDTO paquete, ServidorHilo hiloRemintente) {
                System.out.println("el servidor responde: " + paquete.getClass().getSimpleName());
            }
        };
        LectorConfiguracion config = new LectorConfiguracion();
        String ip = config.getIpServidor();
        int puerto = config.getPuertoServidor();
        
        Servidor servidor = new Servidor(puerto, receptor);
        servidor.iniciar();
        
        
    }
}
