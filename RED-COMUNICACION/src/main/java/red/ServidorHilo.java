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

    public ServidorHilo(ObjectInputStream in, ObjectOutputStream out, Lobby lobby) {
        this.in = in;
        this.out = out;
        this.lobby = lobby;
        this.fachadaJuego = new GestorJuegoFacade(carta,mazo,estado);
    }

    public synchronized void enviarDatos(Object mensaje){
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
                    MensajeRegistroDTO dto = (MensajeRegistroDTO) objeto;
                    validarNombre(dto);
                }else if(objeto instanceof MensajeDTO){
                    MensajeDTO mensaje = (MensajeDTO) objeto;
                    if("INTENCION_INICIAR_PARTIDA".equals(mensaje.getTipo())){
                            procesarInicioPartida();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (this.nombreJugador != null && !this.nombreJugador.isBlank()) {
                lobby.getNombreJugadores().removeIf(j -> j.equalsIgnoreCase(this.nombreJugador));
                lobby.notificarObservador("LISTA_ACTUALIZADA");
                System.out.println("Jugador removido del lobby: " + this.nombreJugador);
            }
            Servidor.hilosConectados.remove(this);
        }
    }
    private void procesarInicioPartida() {
        try {
            fachadaJuego.prepararIniciarPartida(lobby.getNombreJugadores());
            Partida partida = fachadaJuego.getPartidaActual();
            PartidaDTO partidaDTO = PartidaMapper.toDTO(partida);

            MensajeEstadoPartidaDTO respuesta = new MensajeEstadoPartidaDTO();
            respuesta.setTipo("ACTUALIZACION_PARTIDA");
            respuesta.setPartida(partidaDTO);

            for (ServidorHilo cliente: Servidor.hilosConectados){
                cliente.enviarDatos(respuesta);
            }
            System.out.println("[servidor] Partida iniciada");
        }catch (Exception e){
            enviarError(e.getMessage());
        }
    }
    private void enviarError(String msj) {
        try {
            out.writeObject(new MensajeNotificacionDTO("SERVIDOR", true, msj));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace(); }
    }


    private void validarNombre(MensajeRegistroDTO dto) {
        boolean exito = lobby.agregarJugador(dto.getNombre());

        if (exito) {
            this.nombreJugador = dto.getNombre();
            
            System.out.println("[REGISTRO] El usuario '" + this.nombreJugador + "' se ha unido al lobby.");
            System.out.println("Jugadores actuales en el lobby: " + lobby.getNombreJugadores());
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", false, "Registro exitoso"));
            difundirLista();
        } else {
            enviarDatos(new MensajeNotificacionDTO("SERVIDOR", true, "El nombre ya esta en uso."));
        }
    }

    private void difundirLista() {
        MensajeListaJugadoresDTO msgLista = new MensajeListaJugadoresDTO(lobby.getNombreJugadores());

        for (ServidorHilo cliente : Servidor.hilosConectados){
            try{
                cliente.out.writeObject(msgLista);
                cliente.out.flush();
            } catch (IOException e) {
                System.out.println("Error al escribir lista de jugadores.");
            }
        }
    }
}