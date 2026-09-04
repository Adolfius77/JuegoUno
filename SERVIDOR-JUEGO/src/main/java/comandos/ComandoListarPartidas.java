package comandos;

import Nodos.ManejadorNodos;
import dtos.MensajeDTO;
import interfaces.IComandoServidor;
import java.util.HashMap;
import java.util.Map;
import servidor.GestorSalas;

public class ComandoListarPartidas implements IComandoServidor {

    private final ManejadorNodos manejadorNodos;
    private final GestorSalas gestorSalas;

    public ComandoListarPartidas(ManejadorNodos manejadorNodos, GestorSalas gestorSalas) {
        this.manejadorNodos = manejadorNodos;
        this.gestorSalas = gestorSalas;
    }

    @Override
    public void ejecutar(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getIdSesion() == null) {
            return;
        }

        MensajeDTO respuesta = new MensajeDTO();
        respuesta.setTipo("LISTA_PARTIDAS_DISPONIBLES");
        respuesta.setRemitente("SERVIDOR");

        Map<String, Object> datos = new HashMap<>();
        datos.put("partidas", gestorSalas.obtenerSalasSerializables());
        respuesta.setDatos(datos);

        manejadorNodos.enviarNodo(mensaje.getIdSesion(), respuesta);
    }
}
