package controlador.bot;

import cliente.ClienteProxy;
import com.google.gson.Gson;
import controlador.ControladorSuscriptor;
import dtos.CartaDTO;
import dtos.JugadorDTO;
import dtos.MensajeDTO;
import dtos.PartidaDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Un jugador automatico: se registra, entra a una sala, se marca listo y juega
 * su turno solo.
 *
 * Es un controlador mas del cliente, suscrito al mismo Broker que los demas, y
 * a proposito no toca nada de Swing: asi corre en una terminal, sin pantalla,
 * en la maquina de quien quiera un rival.
 *
 * No reutiliza GameController porque ese abre un JOptionPane para elegir el
 * color de un comodin y un podioView al terminar, y ambos necesitan ventana.
 */
public class BotController extends ControladorSuscriptor {

    /** Lo que tarda en "pensar", para que se siga con la vista. */
    private static final long MS_PENSAR = 1200;

    /**
     * Si tras jugar no llega un estado nuevo en este tiempo, algo se perdio.
     * Hace falta porque EstadoJugando.jugarCarta descarta una jugada invalida
     * sin avisar a nadie: sin este vigilante el bot se quedaria esperando para
     * siempre y la partida no avanzaria nunca.
     */
    private static final long MS_VIGILANTE = 10_000;

    /** Se pasa como codigo de sala para que el bot cree la suya y la anuncie. */
    public static final String CREAR_SALA = "crear";

    private final ClienteProxy proxy;
    private final String nombre;
    private final boolean anfitrion;
    private final String avatar;
    private final EstrategiaBot estrategia;

    private volatile String codigoSala;
    private volatile boolean partidaPedida;

    /** Un solo hilo: nunca en el del proxy, que si se bloquea deja de leer. */
    private final ScheduledExecutorService obrero =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "bot-" + System.nanoTime());
                t.setDaemon(true);
                return t;
            });

    private final CountDownLatch finDePartida = new CountDownLatch(1);
    private final AtomicLong versionEstado = new AtomicLong();

    private volatile PartidaDTO estado;
    private volatile String turnoAnterior;
    private volatile boolean yaRoboEsteTurno;
    private volatile boolean terminado;

    public BotController(ClienteProxy proxy, String nombre, String codigoSala,
                         String avatar, EstrategiaBot estrategia) {
        super(proxy != null ? proxy.getBroker() : null);
        if (nombre == null || nombre.isBlank() || codigoSala == null || codigoSala.isBlank()) {
            throw new IllegalArgumentException("El bot necesita un nombre y un codigo de sala.");
        }
        this.proxy = proxy;
        this.nombre = nombre.trim();
        this.anfitrion = CREAR_SALA.equalsIgnoreCase(codigoSala.trim());
        this.codigoSala = anfitrion ? null : codigoSala.trim().toUpperCase();
        this.avatar = avatar;
        this.estrategia = estrategia;

        suscribir("REGISTRO_EXITOSO", m -> entrarALaSala());
        suscribir("ERROR_REGISTRO", m -> abortar("No se pudo registrar", m));
        suscribir("UNIDO_EXITO", m -> marcarseListo());
        suscribir("ERROR_UNIRSE", m -> abortar("No se pudo entrar a la sala", m));
        suscribir("SALA_CREADA", this::recibirSalaCreada);
        suscribir("LISTA_ACTUALIZADA", this::revisarSiYaEmpezamos);
        suscribir("ERROR_INICIAR_PARTIDA", m -> reintentarInicio(m));

        suscribir("PARTIDA_INICIADA", this::recibirEstado);
        suscribir("ACTUALIZACION_MESA", this::recibirEstado);
        suscribir("ACTUALIZACION_TABLERO", this::recibirEstado);
        suscribir("PARTIDA_FINALIZADA", this::recibirFinal);
        // Todos los errores de partida, no solo ERROR_GENERAL: si el servidor
        // rechaza un grito de UNO o un robo y nadie lo escucha, el bot sigue
        // como si nada y el fallo solo se nota en el marcador.
        for (String tipoError : List.of("ERROR_GENERAL", "ERROR_GRITAR_UNO",
                "ERROR_TOMAR_CARTA", "ERROR_PASAR_TURNO")) {
            suscribir(tipoError, this::registrarError);
        }
    }

    // --- Entrada a la sala -------------------------------------------------

    /** Arranca el saludo; el resto se encadena con las respuestas del servidor. */
    public void entrar() {
        decir("me presento como \"" + nombre + "\" (nivel " + estrategia.nombre() + ")"
                + (anfitrion ? "; voy a crear una sala" : ""));
        MensajeDTO registro = new MensajeDTO("REGISTRO_JUGADOR", nombre);
        registro.getDatos().put("nombre", nombre);
        registro.getDatos().put("avatar", avatar);
        proxy.enviarMensaje(registro);
    }

    private void entrarALaSala() {
        if (anfitrion) {
            MensajeDTO creacion = new MensajeDTO("PETICION_CREAR_PARTIDA", nombre);
            creacion.getDatos().put("nombre", nombre);
            creacion.getDatos().put("nombreSala", "Sala de " + nombre);
            creacion.getDatos().put("limiteJugadores", 4);
            proxy.enviarMensaje(creacion);
            return;
        }
        decir("entrando a la sala " + codigoSala);
        MensajeDTO union = new MensajeDTO("PETICION_UNIRSE_PARTIDA", nombre);
        union.getDatos().put("nombre", nombre);
        union.getDatos().put("codigoSala", codigoSala);
        proxy.enviarMensaje(union);
    }

    private void recibirSalaCreada(MensajeDTO mensaje) {
        Object codigo = mensaje.getDatos() != null ? mensaje.getDatos().get("codigoSala") : null;
        if (codigo == null) {
            return;
        }
        this.codigoSala = String.valueOf(codigo);
        decir("abri la sala. CODIGO: " + codigoSala + " - entra con ese codigo y jugamos");
        marcarseListo();
    }

    /**
     * Solo el anfitrion puede arrancar la partida, y el servidor exige que
     * esten todos listos y que sean al menos dos. Se revisa en cada cambio de
     * la lista, que es lo unico que anuncia que alguien se puso listo.
     */
    private void revisarSiYaEmpezamos(MensajeDTO mensaje) {
        if (!anfitrion || partidaPedida || codigoSala == null || mensaje.getDatos() == null) {
            return;
        }
        Object crudo = mensaje.getDatos().get("jugadores");
        if (!(crudo instanceof List<?> jugadores) || jugadores.size() < 2) {
            return;
        }
        for (Object jugador : jugadores) {
            if (!(jugador instanceof Map<?, ?> datos)
                    || !Boolean.parseBoolean(String.valueOf(datos.get("estaListo")))) {
                return;
            }
        }
        partidaPedida = true;
        decir("ya estamos todos; arranco la partida");
        MensajeDTO inicio = new MensajeDTO("INTENCION_INICIAR_PARTIDA", nombre);
        inicio.getDatos().put("codigoSala", codigoSala);
        proxy.enviarMensaje(inicio);
    }

    /** Si el servidor rechaza el arranque, se vuelve a intentar al siguiente cambio. */
    private void reintentarInicio(MensajeDTO mensaje) {
        partidaPedida = false;
        registrarError(mensaje);
    }

    private void marcarseListo() {
        decir("listo; esperando a que empiecen");
        MensajeDTO listo = new MensajeDTO("ACTUALIZAR_ESTADO_LISTO", nombre);
        listo.getDatos().put("estaListo", true);
        proxy.enviarMensaje(listo);
    }

    /** Espera a que la partida acabe (o a que algo falle). */
    public void esperarFinal() throws InterruptedException {
        finDePartida.await();
    }

    public boolean esperarFinal(long tiempo, TimeUnit unidad) throws InterruptedException {
        return finDePartida.await(tiempo, unidad);
    }

    public void cerrar() {
        terminado = true;
        liberar();
        obrero.shutdownNow();
        finDePartida.countDown();
    }

    // --- Partida -----------------------------------------------------------

    private void recibirEstado(MensajeDTO mensaje) {
        PartidaDTO nueva = extraerPartida(mensaje);
        if (nueva == null || terminado) {
            return;
        }
        this.estado = nueva;
        long version = versionEstado.incrementAndGet();

        String turno = nueva.getTurnoJugadorId();
        if (turnoAnterior == null || !turnoAnterior.equals(turno)) {
            turnoAnterior = turno;
            yaRoboEsteTurno = false;
        }

        if (nombre.equals(turno)) {
            obrero.schedule(() -> jugarTurno(version), MS_PENSAR, TimeUnit.MILLISECONDS);
        }
    }

    private void jugarTurno(long version) {
        if (terminado) {
            return;
        }
        // Un mismo turno puede difundirse varias veces (la carta, su efecto, el
        // cambio de turno), y cada difusion programaba una jugada: el bot
        // actuaba dos veces con la misma mano, y la segunda vez con datos ya
        // viejos. Solo sigue adelante la tarea del ultimo estado recibido.
        if (versionEstado.get() != version) {
            return;
        }
        PartidaDTO actual = this.estado;
        if (actual == null || !actual.isEnCurso() || !nombre.equals(actual.getTurnoJugadorId())) {
            return;
        }

        List<CartaDTO> mano = miMano(actual);
        CartaDTO centro = actual.getCartaCentro();

        Decision decision = estrategia.decidir(mano, centro, actual, nombre, yaRoboEsteTurno);
        decir("tengo " + mano.size() + " cartas, en la mesa hay "
                + describir(centro) + " -> " + decision);

        gritarUnoSiHaceFalta(mano, decision);

        switch (decision.tipo()) {
            case JUGAR -> {
                MensajeDTO jugada = new MensajeDTO("PETICION_JUGAR_CARTA", nombre);
                jugada.getDatos().put("carta", decision.carta());
                if (decision.colorElegido() != null) {
                    jugada.getDatos().put("colorElegido", decision.colorElegido());
                }
                proxy.enviarMensaje(jugada);
            }
            case ROBAR -> {
                yaRoboEsteTurno = true;
                proxy.enviarMensaje(new MensajeDTO("PETICION_TOMAR_CARTA", nombre));
            }
            case PASAR -> proxy.enviarMensaje(new MensajeDTO("PETICION_PASAR_TURNO", nombre));
        }

        vigilar(version);
    }

    /**
     * Grita UNO cuando la accion va a cerrar el turno dejandolo con una carta.
     *
     * Solo cuentan jugar y pasar, que son las que llevan a Partida.pasarTurno,
     * donde esta el castigo de 3 cartas. Robar no: deja la mano en dos y ni
     * siquiera pasa el turno.
     *
     * Se grita siempre, sin mirar si ya se habia gritado. Partida.pasarTurno
     * apaga dijoUno cada vez que cambia el turno, asi que la bandera de la
     * ultima foto del estado no dice nada fiable; y un grito de mas no molesta,
     * porque ComandoGritarUno lo acepta con una o con dos cartas en mano, que
     * es justo cuando lo mandamos.
     */
    private void gritarUnoSiHaceFalta(List<CartaDTO> mano, Decision decision) {
        boolean cierraElTurno = decision.tipo() == Decision.Tipo.JUGAR
                || decision.tipo() == Decision.Tipo.PASAR;
        int cartasDespues = decision.tipo() == Decision.Tipo.JUGAR ? mano.size() - 1 : mano.size();

        if (!cierraElTurno || cartasDespues != 1) {
            return;
        }
        decir("UNO!");
        proxy.enviarMensaje(new MensajeDTO("PETICION_GRITAR_UNO", nombre));
    }

    /**
     * Si el estado no se movio, es que la jugada se perdio: se pasa el turno
     * para que la partida no se quede congelada esperando al bot.
     */
    private void vigilar(long versionAlJugar) {
        obrero.schedule(() -> {
            if (terminado || versionEstado.get() != versionAlJugar) {
                return;
            }
            PartidaDTO actual = this.estado;
            if (actual == null || !nombre.equals(actual.getTurnoJugadorId())) {
                return;
            }
            decir("mi jugada no tuvo efecto; paso el turno para no atorar la partida");
            yaRoboEsteTurno = true;
            proxy.enviarMensaje(new MensajeDTO("PETICION_PASAR_TURNO", nombre));
        }, MS_VIGILANTE, TimeUnit.MILLISECONDS);
    }

    private void recibirFinal(MensajeDTO mensaje) {
        Object ganador = mensaje.getDatos() != null ? mensaje.getDatos().get("ganador") : null;
        String quien = ganador != null ? String.valueOf(ganador) : "alguien";
        decir(nombre.equals(quien) ? "gane la partida" : "gano " + quien);
        terminado = true;
        finDePartida.countDown();
    }

    private void registrarError(MensajeDTO mensaje) {
        Object motivo = mensaje.getDatos() != null ? mensaje.getDatos().get("motivo") : null;
        decir("el servidor rechazo " + mensaje.getTipo() + ": " + motivo);
    }

    private void abortar(String queFallo, MensajeDTO mensaje) {
        Object motivo = mensaje.getDatos() != null ? mensaje.getDatos().get("motivo") : null;
        decir(queFallo + (motivo != null ? ": " + motivo : ""));
        terminado = true;
        finDePartida.countDown();
    }

    // --- Utilidades --------------------------------------------------------

    private PartidaDTO extraerPartida(MensajeDTO mensaje) {
        if (mensaje == null || mensaje.getDatos() == null
                || !mensaje.getDatos().containsKey("partida")) {
            return null;
        }
        Gson gson = new Gson();
        String json = gson.toJson(mensaje.getDatos().get("partida"));
        return gson.fromJson(json, PartidaDTO.class);
    }

    private List<CartaDTO> miMano(PartidaDTO partida) {
        if (partida.getJugadores() == null) {
            return Collections.emptyList();
        }
        for (JugadorDTO jugador : partida.getJugadores()) {
            if (jugador != null && nombre.equals(jugador.getNombre())) {
                return jugador.getMano() != null && jugador.getMano().getCartas() != null
                        ? jugador.getMano().getCartas()
                        : Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private static String describir(CartaDTO carta) {
        return carta == null ? "nada" : carta.getColor() + " " + carta.getValor();
    }

    private void decir(String texto) {
        System.out.println("[" + nombre + "] " + texto);
    }
}
