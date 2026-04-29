package red;

import factorys.ClienteHiloFactory;
import factorys.SocketFactory;
import factorys.StreamFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClienteHilo hilo;
    private GestorPartida gestor;
//metodo para estableces la conexion :p

    public void conectar(String ip, int puerto) throws IOException {
        this.socket = SocketFactory.crearSocket(ip, puerto);
        this.out = StreamFactory.crearOutputStream(socket);
        this.out.flush();
        this.in = StreamFactory.crearInputStream(socket);
    }
//con este metodo se inicia el hilo que escucha al servidor

    public void iniciar() throws IOException {
        if (this.in != null) {
            this.hilo = ClienteHiloFactory.crearHilo(in, gestor);
            this.hilo.start();
            System.out.println("[cliente] escuchando al servidor");
        }
    }
//servira para enviar jugadas

    public ObjectOutputStream getOut() {
        return out;
    }

    public void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.printf("Error al desconectar el servidor\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
