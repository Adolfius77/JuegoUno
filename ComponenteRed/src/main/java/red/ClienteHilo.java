package red;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ClienteHilo extends Thread{
    private DataInputStream in;
    private DataOutputStream out;

    public ClienteHilo(DataOutputStream out, DataInputStream in){
        this.out = out;
        this.in = in;
    }

    @Override
    public void run(){
        while (true){
            System.out.println("Esperando jugada...");

        }
    }
}