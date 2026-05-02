package red;


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
        Servidor servidor = new Servidor(9090, receptor);
        servidor.iniciar();
    }
}
