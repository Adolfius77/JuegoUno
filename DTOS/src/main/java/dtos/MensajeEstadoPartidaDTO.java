package dtos;

public class MensajeEstadoPartidaDTO extends MensajeDTO {

    private static final long serialVersionUID = 1L;

    private PartidaDTO partida;

    public MensajeEstadoPartidaDTO(PartidaDTO partida) {
        super("ACTUALIZACION_PARTIDA", "SERVIDOR");
        this.partida = partida;
        getDatos().put("partida", partida);
    }

    public MensajeEstadoPartidaDTO() {
        super("ACTUALIZACION_PARTIDA", "SERVIDOR");
    }

    public PartidaDTO getPartida() {
        return partida;
    }

    public void setPartida(PartidaDTO partida) {
        this.partida = partida;
        getDatos().put("partida", partida);
    }
}
