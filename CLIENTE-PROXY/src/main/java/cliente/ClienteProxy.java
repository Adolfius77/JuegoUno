package cliente;

import Interfacez.IProxy;
import Interfacez.ISerializador;
import dtos.MensajeDTO;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ClienteProxy implements IProxy {
    private Socket socket;
    private ISerializador serializador;
    private OutputStream salida;
    private InputStream entrada;
    private Consumer<MensajeDTO> accionAlRecibirMensaje;

    public ClienteProxy(Socket socket,ISerializador serializador) {
        this.socket = socket;
        this.serializador = serializador;
    }

    // la accion la recibira mediante el controlador
    public void setReceptor(Consumer<MensajeDTO> accion){
        this.accionAlRecibirMensaje = accion;
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

    @Override
    public void run() {
        try {
            this.entrada = socket.getInputStream();
            salida = socket.getOutputStream();
            BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8));
            while(true){
                String jsonRecibido = lector.readLine();
                if(jsonRecibido == null){
                    break;
                }
                MensajeDTO mensaje = serializador.desearealizar(jsonRecibido);
                if(accionAlRecibirMensaje != null){
                    accionAlRecibirMensaje.accept(mensaje);
                }
            }

        }catch (Exception e){
            System.out.println("error en la red" + e.getMessage());
        }finally {
            cerrarConexion();
        }
    }
    private void cerrarConexion(){
        try{
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Conexion cerrada");
        } catch (IOException e) {
            throw new RuntimeException("error cerrando conexion" + e.getMessage());
        }
    }
}
