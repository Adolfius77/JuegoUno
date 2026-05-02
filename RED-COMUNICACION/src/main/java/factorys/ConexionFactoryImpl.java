/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package factorys;

import interfaces.IConexionFactory;
import interfaces.IGestorPartida;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Controladores.ServerController;

/**
 *
 * @author USER
 */
public class ConexionFactoryImpl implements IConexionFactory{

    @Override
    public Socket crearSocket(String ip, int puerto) throws IOException {
        return SocketFactory.crearSocket(ip, puerto);
    }

    @Override
    public ObjectOutputStream crearOutputStream(Socket socket) throws IOException {
        return StreamFactory.crearOutputStream(socket);
    }

    @Override
    public ObjectInputStream crearInputStream(Socket socket) throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public Thread crearHiloCliente(ObjectInputStream in, IGestorPartida gestor){
            return ClienteHiloFactory.crearHilo(in, (ServerController) gestor);
    }
    
}
