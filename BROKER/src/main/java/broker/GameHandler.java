package broker;

import Entidades.Lobby;
import Entidades.Logica.Partida;
import Interfacez.IBroker;
import Mappers.PartidaMapper;
import dtos.*;

import java.util.List;

public class GameHandler {

    private final IBroker broker;

    public GameHandler(IBroker broker) {
        this.broker = broker;
        registrarSuscripciones();
        System.out.println("[GameHandler] Suscripciones registradas.");
    }

    // ── Suscripciones ─────────────────────────────────────────────────────────
    private void registrarSuscripciones() {
        broker.subscribirse("SOLICITUD_REGISTRO", msg -> procesarRegistro((MensajeDTO) msg));
        broker.subscribirse("UNIRSE_A_SALA", msg -> procesarUnirseASala((MensajeDTO) msg));
        broker.subscribirse("CREAR_PARTIDA", msg -> procesarCrearPartida((MensajeDTO) msg));
        broker.subscribirse("INTENCION_INICIAR_PARTIDA", msg -> procesarJugadorListo((MensajeDTO) msg));
        broker.subscribirse("SOLICITAR_LISTA_PARTIDAS", msg -> procesarListaPartidas((MensajeDTO) msg));
        broker.subscribirse("JUGAR_CARTA", msg -> procesarJugarCarta((MensajeDTO) msg));
        broker.subscribirse("ROBAR_CARTA", msg -> procesarRobarCarta((MensajeDTO) msg));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    /**
     * Envía un mensaje a UN jugador específico por su canal personal.
     */
    private void enviarA(MensajeDTO mensaje, String canalRespuesta) {
        broker.publicar(canalRespuesta, mensaje);
    }

    /**
     * Difunde un mensaje a todos los jugadores de una sala.
     */
    private void difundirASala(String codigoSala, MensajeDTO mensaje) {
        List<String> canales = GestorSalas.getInstance().getCanalesDeSala(codigoSala);
        for (String canal : canales) {
            broker.publicar(canal, mensaje);
        }
    }

    private String extraerCanalRespuesta(MensajeDTO msg) {
        return (String) msg.getDatos().get("canalRespuesta");
    }

    private String extraerNombre(MensajeDTO msg) {
        return (String) msg.getDatos().get("nombreJugador");
    }

    private String extraerSala(MensajeDTO msg) {
        return (String) msg.getDatos().get("codigoSala");
    }

    // ── Handlers ─────────────────────────────────────────────────────────────
    private void procesarRegistro(MensajeDTO mensaje) {
        System.out.println("[GameHandler] procesarRegistro ejecutado");
        System.out.println("[GameHandler] datos: " + mensaje.getDatos());

        String nombre = extraerNombre(mensaje);
        String canalRespuesta = extraerCanalRespuesta(mensaje);

        System.out.println("[GameHandler] nombre=" + nombre + " canal=" + canalRespuesta);

        boolean yaExiste = GestorSalas.getInstance().getCanalJugador(nombre) != null;

        if (yaExiste) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "NOMBRE_EN_USO"), canalRespuesta);
            return;
        }

        GestorSalas.getInstance().registrarCanalJugador(nombre, canalRespuesta);
        enviarA(new MensajeNotificacionDTO("SERVIDOR", false, "REGISTRO_EXITOSO"), canalRespuesta);
        System.out.println("[GameHandler] Jugador registrado: " + nombre);
    }

    private void procesarCrearPartida(MensajeDTO mensaje) {
        String nombre = extraerNombre(mensaje);
        String canalRespuesta = extraerCanalRespuesta(mensaje);
        String nombreSala = (String) mensaje.getDatos().get("nombreSala");
        int limite = ((Number) mensaje.getDatos().get("limiteJugadores")).intValue();

        String codigoSala = GestorSalas.getInstance().crearSala(nombreSala, limite);
        GestorSalas.getInstance().getSala(codigoSala).agregarJugador(nombre);

        enviarA(new MensajeNotificacionDTO("SERVIDOR", false, "SALA_CREADA:" + codigoSala),
                canalRespuesta);
        System.out.println("[GameHandler] Sala creada: " + codigoSala + " por " + nombre);
    }

    private void procesarUnirseASala(MensajeDTO mensaje) {
        String nombre = extraerNombre(mensaje);
        String canalRespuesta = extraerCanalRespuesta(mensaje);
        String codigoSala = extraerSala(mensaje);

        GestorSalas gestor = GestorSalas.getInstance();

        if (!gestor.existeSala(codigoSala)) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "SALA_NO_EXISTE"), canalRespuesta);
            return;
        }

        Lobby sala = gestor.getSala(codigoSala);
        if (sala.estaLleno()) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "SALA_LLENA"), canalRespuesta);
            return;
        }

        sala.agregarJugador(nombre);
        // El canal ya fue registrado en el registro; solo actualizamos la sala
        enviarA(new MensajeNotificacionDTO("SERVIDOR", false, "SALA_UNIDO:" + codigoSala),
                canalRespuesta);

        // Difundir lista actualizada a todos en la sala
        difundirListaSala(codigoSala);
        System.out.println("[GameHandler] " + nombre + " se unió a sala: " + codigoSala);
    }

    private void procesarJugadorListo(MensajeDTO mensaje) {
        String nombre = extraerNombre(mensaje);
        String codigoSala = extraerSala(mensaje);

        boolean todosListos = GestorSalas.getInstance().marcarListo(codigoSala, nombre);
        System.out.println("[GameHandler] " + nombre + " listo en sala " + codigoSala);

        if (todosListos) {
            iniciarPartida(codigoSala);
        }
    }

    private void iniciarPartida(String codigoSala) {
        try {
            Lobby sala = GestorSalas.getInstance().getSala(codigoSala);

            facades.GestorJuegoFacade facade = new facades.GestorJuegoFacade(
                    new Entidades.fabricas.CartaFactory(),
                    new Entidades.fabricas.MazoClasicoFactory(),
                    Entidades.fabricas.EstadoFactory.crearEstadoEsperando()
            );
            facade.prepararIniciarPartida(sala.getNombreJugadores());
            Partida partida = facade.getPartidaActual();

            GestorSalas.getInstance().guardarPartida(codigoSala, partida);
            GestorSalas.getInstance().limpiarListos(codigoSala);

            MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
            respuesta.setTipo("ACTUALIZACION_PARTIDA");
            respuesta.setPartida(PartidaMapper.toDTO(partida));

            difundirASala(codigoSala, respuesta);
            System.out.println("[GameHandler] Partida iniciada en sala: " + codigoSala);
        } catch (Exception e) {
            System.out.println("[GameHandler] Error al iniciar partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarRobarCarta(MensajeDTO mensaje) {
        String nombre = extraerNombre(mensaje);
        String codigoSala = extraerSala(mensaje);
        String canalRespuesta = extraerCanalRespuesta(mensaje);

        Partida partida = GestorSalas.getInstance().getPartida(codigoSala);
        if (partida == null) {
            return;
        }

        Entidades.Jugador jugador = buscarJugador(partida, nombre);
        if (jugador == null) {
            return;
        }

        if (!partida.getJugadorActual().equals(jugador)) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "NO_ES_TU_TURNO"), canalRespuesta);
            return;
        }

        partida.tomarCarta(jugador);
        partida.pasarTurno();

        MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
        respuesta.setTipo("ACTUALIZACION_PARTIDA");
        respuesta.setPartida(PartidaMapper.toDTO(partida));

        difundirASala(codigoSala, respuesta);
        System.out.println("[GameHandler] " + nombre + " robó una carta.");
    }

    private void procesarJugarCarta(MensajeDTO mensaje) {
        String nombre = extraerNombre(mensaje);
        String codigoSala = extraerSala(mensaje);
        String canalRespuesta = extraerCanalRespuesta(mensaje);
        String color = (String) mensaje.getDatos().get("color");
        String valor = (String) mensaje.getDatos().get("valor");
        String colorElegido = (String) mensaje.getDatos().get("colorElegido");

        Partida partida = GestorSalas.getInstance().getPartida(codigoSala);
        if (partida == null) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "PARTIDA_NO_ENCONTRADA"), canalRespuesta);
            return;
        }

        Entidades.Jugador jugador = buscarJugador(partida, nombre);
        if (jugador == null) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "JUGADOR_NO_ENCONTRADO"), canalRespuesta);
            return;
        }

        Entidades.Carta cartaAJugar = jugador.getMano().getCartas().stream()
                .filter(c -> {
                    dtos.CartaDTO dto = new Mappers.CartaMapper().toDTO(c);
                    return dto.getColor().equals(color) && dto.getValor().equals(valor);
                })
                .findFirst().orElse(null);

        if (cartaAJugar == null) {
            enviarA(new MensajeNotificacionDTO("SERVIDOR", true, "CARTA_NO_EN_MANO"), canalRespuesta);
            return;
        }

        partida.jugarCarta(cartaAJugar, jugador);

        if (colorElegido != null) {
            partida.getPilaCartas().setColorActivo(
                    Entidades.enums.Color.valueOf(colorElegido));
        }

        // ¿Hay ganador?
        Entidades.Jugador ganador = partida.getJugadores().stream()
                .filter(j -> j.getMano().getCartas().isEmpty())
                .findFirst().orElse(null);

        if (ganador != null) {
            MensajeNotificacionDTO notif = new MensajeNotificacionDTO(
                    "SERVIDOR", false, "PARTIDA_TERMINADA:" + ganador.getNombre());
            difundirASala(codigoSala, notif);
            System.out.println("[GameHandler] Partida terminada. Ganador: " + ganador.getNombre());
        } else {
            MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
            respuesta.setTipo("ACTUALIZACION_PARTIDA");
            respuesta.setPartida(PartidaMapper.toDTO(partida));
            difundirASala(codigoSala, respuesta);
            System.out.println("[GameHandler] " + nombre + " jugó: " + color + " " + valor
                    + (colorElegido != null ? " → color activo: " + colorElegido : ""));
        }
    }

    private void procesarListaPartidas(MensajeDTO mensaje) {
        String canalRespuesta = extraerCanalRespuesta(mensaje);
        String lista = String.join(",", GestorSalas.getInstance().getSalas().keySet());
        enviarA(new MensajeNotificacionDTO("SERVIDOR", false, "LISTA_PARTIDAS:" + lista),
                canalRespuesta);
    }

    // ── Utils ─────────────────────────────────────────────────────────────────
    private Entidades.Jugador buscarJugador(Partida partida, String nombre) {
        return partida.getJugadores().stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(nombre))
                .findFirst().orElse(null);
    }

    private void difundirListaSala(String codigoSala) {
        Lobby sala = GestorSalas.getInstance().getSala(codigoSala);
        if (sala == null) {
            return;
        }
        MensajeListaJugadoresDTO msgLista = new MensajeListaJugadoresDTO(sala.getNombreJugadores());
        difundirASala(codigoSala, msgLista);
    }
}
