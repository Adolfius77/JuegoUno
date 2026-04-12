package red;

import Entidades.Lobby;
import interfaces.IReceptorMensajes;
import parqueteRed.paqueteRedDTO;

import java.io.*;
import java.net.Socket;

public class ServidorHilo extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private IReceptorMensajes receptor;
    private Lobby lobby;
    private Socket socket;
    private boolean conectado;

    public ServidorHilo(Socket socket, IReceptorMensajes receptor) {
        this.socket = socket;
        this.lobby = lobby;
        this.receptor = receptor;
        this.conectado = true;
    }

    @Override
    public void run() {
        try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while (conectado) {
                    Object objetoLeido = in.readObject();

                    if(objetoLeido instanceof paqueteRedDTO) {
                        paqueteRedDTO paquete = (paqueteRedDTO) objetoLeido;
                        receptor.procesarMensaje(paquete,this);
                    }
                }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cliente desconectado.");
        }finally {
            cerrarConexion();
        }
    }
    private void enviarMensaje(paqueteRedDTO paquete) {
        try{
            if(out != null) {
                out.writeObject(paquete);
                out.flush();

            }
        } catch (IOException e) {
            System.out.println("error al intentar enviar el mensaje");
            cerrarConexion();
        }
    }
    private void cerrarConexion() {
        try{
            conectado = false;
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null && socket.isClosed()){
                socket.close();
            }
        }catch (Exception e) {
            System.err.println("error al cerra la conexion");
        }
    }
//    private void validarNombre(MensajeRegistroDTO dto) throws IOException {
//        boolean nombreExistente = lobby.getNombreJugadores().stream().anyMatch(j -> j.equalsIgnoreCase(dto.getNombre()));
//        if (nombreExistente) {
//            out.writeObject(new MensajeNotificacionDTO("SERVIDOR",true , "error: el nombre" + dto.getNombre() + "ya esta en uso"));
//        } else {
//            lobby.agregarJugador(dto.getNombre());
//            out.writeObject(new MensajeNotificacionDTO("SERVIDOR",false , "Registro exitoso"));
//            difundirLista();
//        }
//        out.flush();
//    }
    //falta corregir este metodo ando hacindo que jale con el broker
//    private void difundirLista() {
//        MensajeListaJugadoresDTO msgLista = new MensajeListaJugadoresDTO(lobby.getNombreJugadores());
//
//        for (ServidorHilo cliente : Servidor.hilosConectados){
//            try{
//                cliente.out.writeObject(msgLista);
//                cliente.out.flush();
//            } catch (IOException e) {
//                System.out.println("Error al escribir lista de jugadores.");
//            }
//        }
//    }
}