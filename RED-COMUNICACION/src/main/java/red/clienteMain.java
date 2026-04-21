package red;

import java.io.IOException;

public class clienteMain {
    public static void main(String[] args) throws IOException {
        Cliente cliente = new Cliente();
        try {
            System.out.println("conectando...");
            cliente.conectar("127.0.0.1", 8080);
            cliente.iniciar();
            System.out.println("conectado");
        }catch (IOException e){
            System.out.printf("Error al conectar el servidor\n" + e.getMessage());
        }
    }
}
