package comandos;

import Nodos.ManejadorNodos;
import Nodos.NodoCliente;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;

import java.util.UUID;

public class comandoCrearPartida implements IComandoServidor {
    private final ManejadorNodos ManejadorNodos;

    public comandoCrearPartida(Nodos.ManejadorNodos manejadorNodos) {
        ManejadorNodos = manejadorNodos;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        System.out.println("[COMANDO-CREAR-PARTIDA] procesado peticion para crear partida");
        String nombreHost = (String)  mensaje.getDatos().get("nombre");
        String codigoSala = generarCodigoSala();

        MensajeDTO respuesto = new MensajeDTO();
        respuesto.setTipo("SALA_CREADA");
        respuesto.setRemitente("SERVIDOR");
        respuesto.getDatos().put("codigoSala", nombreHost);
        respuesto.getDatos().put("nombre", nombreHost);

        //notifica a todos
        for(NodoCliente nodo: ManejadorNodos.obtenerNodosConectados()){
                nodo.enviarMensaje(respuesto);
        }
    }
    private String generarCodigoSala() {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
