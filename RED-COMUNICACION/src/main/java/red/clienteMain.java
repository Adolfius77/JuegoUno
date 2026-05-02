package red;

import Controladores.ServerController;
import factorys.ConexionFactoryImpl;
import interfaces.IConexionFactory;
import interfaces.IGestorPartida;
import java.io.IOException;

public class clienteMain {
   
    public static void main(String[] args) throws IOException {
        
      
        try {
            IGestorPartida gestor = new ServerController();
            IConexionFactory conexion = new ConexionFactoryImpl();
            Cliente cliente = new Cliente(gestor, conexion);
            
            System.out.println("conectando...");
            cliente.conectar("127.0.0.1", 8080);
            cliente.iniciar();
            System.out.println("conectado");
        }catch (IOException e){
            System.out.printf("Error al conectar el servidor\n" + e.getMessage());
        }
    }
}
