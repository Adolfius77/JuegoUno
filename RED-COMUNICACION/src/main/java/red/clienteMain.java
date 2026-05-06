package red;

import Controladores.ServerController;
import Lector.LectorConfiguracion;
import factorys.ConexionFactoryImpl;
import interfaces.IConexionFactory;
import interfaces.IGestorPartida;
import java.io.IOException;




public class clienteMain {
   
    public static void main(String[] args) throws IOException {
        
      
        try {
            LectorConfiguracion config = new LectorConfiguracion();
            IGestorPartida gestor = new ServerController();
            IConexionFactory conexion = new ConexionFactoryImpl();
            Cliente cliente = new Cliente(gestor, conexion);
            
            String ip = config.getIpServidor();
            int puerto = config.getPuertoServidor();
            
            System.out.println("conectando...");
            cliente.conectar(ip, puerto);
            cliente.iniciar();
            System.out.println("conectado");
        }catch (IOException e){
            System.out.printf("Error al conectar el servidor\n" + e.getMessage());
        }
    }
}
