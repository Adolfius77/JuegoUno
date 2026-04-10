package broker;

import interfaces.IReceptorMensajes;
import parqueteRed.paqueteRedDTO;
import red.ServidorHilo;

import java.io.BufferedReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnrutadorBroker implements IReceptorMensajes {
   private final Map<String, ServidorHilo> direcctorioClientes;
   private ServidorHilo servidor;

    public EnrutadorBroker() {
        this.direcctorioClientes = new ConcurrentHashMap<>();
    }
    @Override
    public synchronized void procesarMensaje(paqueteRedDTO paquete, ServidorHilo hiloRemitente ){
        System.out.println("broker recibido " + paquete.getAccion() + "de:" + paquete.getRemitente() + "para: " + paquete.getDestino());
        String destino = paquete.getDestino();

        //falta implementar la logica del servidor
        switch (destino){
            case "REGISTRO_SERVIDOR":
                break;
            case "REGISTRO_CLIENTE":
                break;
            case "SERVIDOR":
                break;
            case "TODOS":
                break;
            default:
                break;
        }
    }
    //faltaria un metodo para desconectar del servido
}
