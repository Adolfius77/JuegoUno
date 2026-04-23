package eventos;

public enum TipoEventos {
    CLIENTE_REGISTRARSE("cliente.registrarse"),
    CLIENTE_INICIAR_PARTIDA("cliente.iniciarPartida"),
    CLIENTE_JUGAR_CARTA("cliente.jugarCarta"),
    CLIENTE_OBTENER_ESTADO("cliente.obtenerEstado"),
    CLIENTE_OBTENER_JUGADORES("cliente.obtenerJugadores"),
    CLIENTE_SALIR("cliente.salir"),

    SERVIDOR_REGISTRO_CONFIRMADO("servidor.registroConfirmado"),
    SERVIDOR_PARTIDA_INICIADA("servidor.partidaIniciada"),
    SERVIDOR_PARTIDA_FINALIZADA("servidor.partidaFinalizada"),
    SERVIDOR_CARTA_JUGADA("servidor.cartaJugada"),
    SERVIDOR_TURNO_CAMBIADO("servidor.turnoCambiado"),
    SERVIDOR_ESTADO_ACTUAL("servidor.estadoActual"),
    SERVIDOR_LISTA_JUGADORES("servidor.listaJugadores"),
    SERVIDOR_ERROR("servidor.error"),
    SERVIDOR_MINIMO_ALCANZADO("servidor.minimoAlcanzado"),
    SERVIDOR_NUEVO_JUGADOR("servidor.nuevoJugador"),
    SERVIDOR_JUGADOR_SALIO("servidor.jugadorSalió");

    private final String valor;

    private TipoEventos(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
