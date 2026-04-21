package Server;

import Interfacez.IProxy;
import Interfacez.IBroker;
import dtos.MensajeDTO;
import interfaces.ISerializador;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerProxy implements IProxy {
    private Socket socket;
    private IBroker broker;
    private String nombreJugador;
    private ISerializador serializador;

    private InputStream entrada;
    private OutputStream salida;

    public ServerProxy(IBroker broker, Socket socket, ISerializador serializador) {
        this.broker = broker;
        this.socket = socket;
        this.nombreJugador = null;
        this.serializador = serializador;
    }

    @Override
    public void run() {
        try {
            this.entrada = socket.getInputStream();
            this.salida = socket.getOutputStream();
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));


            System.out.println("escuchando a jugador desde:" + socket.getInetAddress() + ":" + socket.getPort());
            while(true){
                String jsonRecibido = lector.readLine();
                if(jsonRecibido == null){
                    break;
                }
                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);
                broker.publicar(mensaje.getTipo(), mensaje);
                System.out.println("mensaje recibido" + mensaje.getTipo());

            }
        }catch (Exception e) {
            System.out.println("el cliente se a desconectado o hay un error: " + e.getMessage());
        }
    }

    @Override
    public void enviarMensaje(MensajeDTO mensaje) {
        try{
            PrintWriter escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            String json =  serializador.serealizar(mensaje);
            escritor.println(json);
        }catch (Exception e){
            System.out.println("error enviando los datos" + e.getMessage());
            e.printStackTrace();
        }
    }
}
