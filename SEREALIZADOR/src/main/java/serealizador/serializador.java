package serealizador;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dtos.MensajeDTO;
import interfaces.ISerializador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class serializador implements ISerializador {

    private static final Gson gson = new Gson();

    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;

    public serializador(Socket socket) throws IOException {
        if(socket == null || socket.isClosed()) {
            throw new IOException("Socket no valido");
        }
        this.socket = socket;
        this.escritor = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        this.lector = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void enviarMensaje(MensajeDTO mensaje) throws IOException {
        try {

            String mensajeJson = gson.toJson(mensaje);
            escritor.println(mensajeJson);
            System.out.println("JSON enviado (" + mensajeJson.length() + " bytes) - Tipo: " + mensaje.getTipo());
        } catch (Exception e) {
            throw new IOException("Error al enviar el mensaje", e);
        }
    }

    @Override
    public MensajeDTO recibirMensaje(MensajeDTO mensaje) throws IOException {
        try {

            String mensajeJson = lector.readLine();

            if (mensajeJson == null) {
                throw new IOException("Conexión cerrada por el otro extremo");
            }

            
            return gson.fromJson(mensajeJson, MensajeDTO.class);

        } catch (JsonSyntaxException e) {
            throw new IOException("Error de formato JSON", e);
        }
    }

    @Override
    public void cerrarConexion() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al intentar cerrar el socket: " + e.getMessage());
        }
    }
}