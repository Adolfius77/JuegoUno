package red;

import Entidades.Estados.IEstadoPartida;
import Entidades.Lobby;
import Entidades.Logica.Partida;
import Entidades.fabricas.ICartaFactory;
import Entidades.fabricas.IMazoFactory;
import Mappers.PartidaMapper;
import dtos.*;
import facades.GestorJuegoFacade;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServidorHilo extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Lobby lobby;
    private GestorJuegoFacade fachadaJuego;
    private ICartaFactory carta;
    private IMazoFactory mazo;
    private IEstadoPartida estado;
    private String nombreJugador;
    private String codigoSala;
    private boolean listo = false;

    public ServidorHilo(ObjectInputStream in, ObjectOutputStream out, Lobby lobby) {
        this.in = in;
        this.out = out;
        this.lobby = lobby;
        this.fachadaJuego = new GestorJuegoFacade(carta, mazo, estado);
    }

    public synchronized void enviarDatos(Object mensaje) {
        try {
            if (out != null) {
                out.writeObject(mensaje);
                out.reset();
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Error enviando datos a " + (nombreJugador != null ? nombreJugador : "cliente") + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object objeto = in.readObject();

                if (objeto instanceof MensajeRegistroDTO) {
                    validarNombre((MensajeRegistroDTO) objeto);

                } else if (objeto instanceof MensajeCrearPartidaDTO) {
                    procesarCrearPartida((MensajeCrearPartidaDTO) objeto);

                } else if (objeto instanceof MensajeDTO) {
                    MensajeDTO mensaje = (MensajeDTO) objeto;

                    switch (mensaje.getTipo() != null ? mensaje.getTipo() : "") {
                        case "SOLICITUD_UNIRSE_LOBBY":
                            unirJugadorAlLobby();
                            break;
                        case "INTENCION_INICIAR_PARTIDA":
                            procesarJugadorListo();
                            break;
                        case "UNIRSE_A_SALA":
                            procesarUnirseASala(mensaje);
                            break;
                        case "SOLICITAR_LISTA_PARTIDAS":
                            String lista = String.join(",",
                                    GestorSalas.getInstance().getSalas().keySet());
                            enviarDatos(new MensajeNotificacionDTO(
                                    "SERVIDOR", false, "LISTA_PARTIDAS:" + lista));
                            break;
                        case "JUGAR_CARTA":
                            procesarJugarCarta(mensaje);
                            break;
                        case "ROBAR_CARTA":
                            procesarRobarCarta();
                            break;
                        default:
                            System.out.println("[ServidorHilo] Tipo no reconocido: "
                                    + mensaje.getTipo());
                            break;
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (this.nombreJugador != null && !this.nombreJugador.isBlank()) {
                if (this.codigoSala != null) {
                    Lobby sala = GestorSalas.getInstance().getSala(this.codigoSala);
                    if (sala != null) {
                        sala.getNombreJugadores().removeIf(
                                j -> j.equalsIgnoreCase(this.nombreJugador));
                        sala.notificarObservador("LISTA_ACTUALIZADA");
                    }
                }
                System.out.println("Jugador desconectado: " + this.nombreJugador);
            }
            Servidor.hilosConectados.remove(this);
        }
    }

    private void procesarRobarCarta() {
        Partida partida = GestorSalas.getInstance().getPartida(codigoSala);
        if (partida == null) {
            return;
        }

        Entidades.Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(this.nombreJugador))
                .findFirst().orElse(null);

        if (jugador == null) {
            return;
        }

        if (!partida.getJugadorActual().equals(jugador)) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "NO_ES_TU_TURNO"));
            return;
        }

        partida.tomarCarta(jugador);
        partida.pasarTurno();

        PartidaDTO partidaDTO = PartidaMapper.toDTO(partida);
        MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
        respuesta.setTipo("ACTUALIZACION_PARTIDA");
        respuesta.setPartida(partidaDTO);

        for (ServidorHilo cliente : Servidor.hilosConectados) {
            if (codigoSala.equals(cliente.codigoSala)) {
                cliente.enviarDatos(respuesta);
            }
        }
        System.out.println("[Servidor] " + nombreJugador + " robó una carta.");
    }

    private void unirJugadorAlLobby() {
        if (this.codigoSala == null || this.nombreJugador == null) {
            return;
        }

        Lobby sala = GestorSalas.getInstance().getSala(this.codigoSala);
        if (sala == null) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "SALA_NO_EXISTE"));
            return;
        }
        if (sala.estaLleno()) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "SALA_LLENA"));
            return;
        }

        boolean exito = sala.agregarJugador(this.nombreJugador);
        if (exito) {
            System.out.println("[LOBBY] " + nombreJugador + " entró a sala: " + codigoSala);
            difundirLista();
        }
    }

    private void procesarUnirseASala(MensajeDTO mensaje) {
        String codigo = (String) mensaje.getDatos().get("codigoSala");
        if (codigo == null || !GestorSalas.getInstance().existeSala(codigo)) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "SALA_NO_EXISTE"));
            return;
        }

        Lobby sala = GestorSalas.getInstance().getSala(codigo);
        if (sala.estaLleno()) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "SALA_LLENA"));
            return;
        }

        this.codigoSala = codigo;
        sala.agregarJugador(this.nombreJugador);
        enviarDatos(new MensajeNotificacionDTO("SERVIDOR", false, "SALA_UNIDO:" + codigo));
        difundirLista();
    }

    private void procesarCrearPartida(MensajeCrearPartidaDTO dto) {
        String codigo = GestorSalas.getInstance().crearSala(
                dto.getNombreSala(), dto.getLimiteJugadores());

        this.codigoSala = codigo;

        Lobby sala = GestorSalas.getInstance().getSala(codigo);
        sala.agregarJugador(this.nombreJugador);

        System.out.println("[Servidor] Sala creada: " + codigo + " por " + nombreJugador);

        enviarDatos(new MensajeNotificacionDTO("SERVIDOR", false, "SALA_CREADA:" + codigo));
    }

    private void procesarJugadorListo() {
        this.listo = true;
        System.out.println("[Servidor] " + nombreJugador + " marcado como listo");

        Lobby sala = GestorSalas.getInstance().getSala(codigoSala);
        System.out.println("[Servidor] codigoSala de este hilo: " + codigoSala);
        if (sala == null) {
            System.out.println("[Servidor] ⚠️ sala es NULL");
            return;
        }

        long totalEnSala = Servidor.hilosConectados.stream()
                .filter(h -> codigoSala.equals(h.codigoSala))
                .count();

        long totalListos = Servidor.hilosConectados.stream()
                .filter(h -> codigoSala.equals(h.codigoSala) && h.listo)
                .count();

        System.out.println("[Servidor] En sala: " + totalEnSala + " | Listos: " + totalListos);

        if (totalEnSala >= 2 && totalListos == totalEnSala) {
            System.out.println("[Servidor] Todos listos, iniciando partida...");
            iniciarPartidaEnSala();
        }
    }

    private void iniciarPartidaEnSala() {
        try {
            Lobby sala = GestorSalas.getInstance().getSala(codigoSala);
            GestorJuegoFacade facade = new GestorJuegoFacade(
                    new Entidades.fabricas.CartaFactory(),
                    new Entidades.fabricas.MazoClasicoFactory(),
                    Entidades.fabricas.EstadoFactory.crearEstadoEsperando()
            );
            facade.prepararIniciarPartida(sala.getNombreJugadores());
            Partida partida = facade.getPartidaActual();

            GestorSalas.getInstance().guardarPartida(codigoSala, partida);

            PartidaDTO partidaDTO = PartidaMapper.toDTO(partida);
            MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
            respuesta.setTipo("ACTUALIZACION_PARTIDA");
            respuesta.setPartida(partidaDTO);

            for (ServidorHilo cliente : Servidor.hilosConectados) {
                if (codigoSala.equals(cliente.codigoSala)) {
                    cliente.enviarDatos(respuesta);
                }
            }
            System.out.println("[Servidor] Partida iniciada en sala: " + codigoSala);
        } catch (Exception e) {
            enviarError("Error al iniciar la partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarJugarCarta(MensajeDTO mensaje) {
        Partida partida = GestorSalas.getInstance().getPartida(codigoSala);
        if (partida == null) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "PARTIDA_NO_ENCONTRADA"));
            return;
        }

        Entidades.Jugador jugador = partida.getJugadores().stream()
                .filter(j -> j.getNombre().equalsIgnoreCase(this.nombreJugador))
                .findFirst().orElse(null);

        if (jugador == null) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "JUGADOR_NO_ENCONTRADO"));
            return;
        }

        String color = (String) mensaje.getDatos().get("color");
        String valor = (String) mensaje.getDatos().get("valor");

        Entidades.Carta cartaAJugar = jugador.getMano().getCartas().stream()
                .filter(c -> {
                    dtos.CartaDTO dto = new Mappers.CartaMapper().toDTO(c);
                    return dto.getColor().equals(color) && dto.getValor().equals(valor);
                })
                .findFirst().orElse(null);

        if (cartaAJugar == null) {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "CARTA_NO_EN_MANO"));
            return;
        }

        partida.jugarCarta(cartaAJugar, jugador);

        PartidaDTO partidaDTO = PartidaMapper.toDTO(partida);
        MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
        respuesta.setTipo("ACTUALIZACION_PARTIDA");
        respuesta.setPartida(partidaDTO);

        for (ServidorHilo cliente : Servidor.hilosConectados) {
            if (codigoSala.equals(cliente.codigoSala)) {
                cliente.enviarDatos(respuesta);
            }
        }

        String colorElegido = (String) mensaje.getDatos().get("colorElegido");
        if (colorElegido != null) {
            partida.getPilaCartas().setColorActivo(
                    Entidades.enums.Color.valueOf(colorElegido));
        }
        
        System.out.println("[Servidor] " + nombreJugador + " jugó: " + color + " " + valor);
    }

    private void procesarInicioPartida() {
        try {
            fachadaJuego.prepararIniciarPartida(lobby.getNombreJugadores());
            Partida partida = fachadaJuego.getPartidaActual();
            PartidaDTO partidaDTO = PartidaMapper.toDTO(partida);

            MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
            respuesta.setTipo("ACTUALIZACION_PARTIDA");
            respuesta.setPartida(partidaDTO);

            for (ServidorHilo cliente : Servidor.hilosConectados) {
                cliente.enviarDatos(respuesta);
            }
            System.out.println("[servidor] Partida iniciada");
        } catch (Exception e) {
            enviarError(e.getMessage());
        }
    }

    private void enviarError(String msj) {
        try {
            out.writeObject(new MensajeNotificacionDTO("SERVIDOR", true, msj));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validarNombre(MensajeRegistroDTO dto) {
        boolean yaExiste = Servidor.hilosConectados.stream()
                .anyMatch(h -> dto.getNombre().equalsIgnoreCase(h.nombreJugador));

        if (!yaExiste) {
            this.nombreJugador = dto.getNombre();
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", false, "REGISTRO_EXITOSO"));
        } else {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "El nombre ya está en uso."));
        }
    }

    private void difundirLista() {
        if (codigoSala == null) {
            return;
        }
        Lobby sala = GestorSalas.getInstance().getSala(codigoSala);
        if (sala == null) {
            return;
        }

        MensajeListaJugadoresDTO msgLista = new MensajeListaJugadoresDTO(sala.getNombreJugadores());

        for (ServidorHilo cliente : Servidor.hilosConectados) {
            if (codigoSala.equals(cliente.codigoSala)) {
                cliente.enviarDatos(msgLista);
            }
        }
    }
}
