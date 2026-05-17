package comandos;

import Interfacez.IProxy;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import java.util.HashMap;
import java.util.Map;
import red.GestorSalas;

public class ComandoListarPartidas implements IComandoServidor {

    private final GestorSalas gestorSalas;

    public ComandoListarPartidas(GestorSalas gestorSalas) {
        this.gestorSalas = gestorSalas;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null) {
            return;
        }

        IProxy proxySolicitante = (IProxy) mensaje.getDatos().get("proxy");
        if (proxySolicitante == null) {
            return;
        }

        MensajeDTO respuesta = new MensajeDTO();
        respuesta.setTipo("LISTA_PARTIDAS_DISPONIBLES");
        respuesta.setRemitente("SERVIDOR");

        Map<String, Object> datos = new HashMap<>();
        datos.put("partidas", gestorSalas.obtenerSalasSerializables());
        respuesta.setDatos(datos);

        proxySolicitante.enviarMensaje(respuesta);
    }
}
