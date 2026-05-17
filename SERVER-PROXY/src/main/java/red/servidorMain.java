package red;

import Lector.LectorConfiguracion;
import Server.ServerProxy;
import serealizador.serializador;

public class servidorMain {

    public static void main(String[] args) {
        LectorConfiguracion config = new LectorConfiguracion();
        serializador seri = new serializador();
        int puerto = config.getPuertoServidor();
        String ip = config.getIpServidor();
        ServerProxy serverProxy = new ServerProxy(puerto, ip, seri);
        serverProxy.iniciar();
    }
}
