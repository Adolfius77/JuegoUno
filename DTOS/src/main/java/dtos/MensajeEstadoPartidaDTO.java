package dtos;

public class MensajeEstadoPartidaDTO extends MensajeDTO {
    private static final long serialVersionUID = 1L;

    private PartidaDTO partida;

    public MensajeEstadoPartidaDTO() {
        super("ACTUALIZACION_PARTIDA");
    }

    public MensajeEstadoPartidaDTO(PartidaDTO partida) {
        super("ACTUALIZACION_PARTIDA");
        this.partida = partida;
    }

    public PartidaDTO getPartida() {
        return partida;
    }

    public void setPartida(PartidaDTO partida) {
        this.partida = partida;
    }
}