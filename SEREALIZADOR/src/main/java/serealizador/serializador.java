package serealizador;

import com.google.gson.Gson;
import dtos.MensajeDTO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.imageio.IIOException;

public class serializador {

    private static final Gson gson = new Gson();
    private static final String DELIMITADOR = "\n";

    public static void enviarMensaje(Socket socket, MensajeDTO mensaje) throws IOException {
        if (socket == null || socket.isClosed()) {
            throw new IOException("Socket no valido");
        }
        try {
            String mensajeJson = gson.toJson(mensaje);

            PrintWriter escritor = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true
            );
            escritor.println(mensajeJson);
            System.out.println("json enviado: " + mensajeJson.length() + "bytes");
            System.out.println("tipo:" + mensaje.getTipo());
        } catch (IOException e) {
            System.out.println("error enviando json: " + e.getMessage());
            throw e;
        }
    }

    public static MensajeDTO recibirMensaje(Socket socket) throws IOException, Exception {
        if (socket == null || socket.isClosed()) {
            throw new IOException("socket no valido");
        }
        try {
            BufferedReader lector = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String mensajeJson = lector.readLine();
            if (mensajeJson == null) {

                throw new IOException("conexion cerrada");
            }
            MensajeDTO mensaje = gson.fromJson(mensajeJson, MensajeDTO.class);
            System.out.println("json recibido: " + mensajeJson.length() + "bytes");
            System.out.println("tipo: " + mensaje.getTipo());
            return mensaje;
        } catch (Exception e) {
            throw new Exception("conexion cerrada");

        }
    }
    public static void cerrarConexion(Socket socket) throws IOException{
        if(socket != null && !socket.isClosed()){
            socket.close();
            System.out.println("conexion cerrada");
        }
    }

}
