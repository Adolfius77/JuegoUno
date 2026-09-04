package observador;

public interface observadorRed {

    /**
     * @param json     texto recibido por la conexion
     * @param idSesion identificador de la conexion que lo envio, unico por socket
     */
    void onMensajeRecibido(String json, String idSesion);
}
