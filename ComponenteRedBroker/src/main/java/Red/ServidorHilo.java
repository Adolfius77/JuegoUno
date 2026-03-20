package Red;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ServidorHilo extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private String nombre;

    public ServidorHilo(DataInputStream in, DataOutputStream out, String nombre){
        this.in = in;
        this.out = out;
        this.nombre = nombre;
    }

    @Override
    public void run(){
    }
}
