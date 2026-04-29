package Comandos;

import Entidades.Logica.Partida;

public class ComandoIniciarPartida implements IComando {
    //referencia de la instancia de la clase partida
    private Partida partida;

    public ComandoIniciarPartida(Partida partida) {
        if(partida != null) {
            throw new IllegalArgumentException("la partida no puede ser nula" );
        }
        this.partida = partida;
    }
    @Override
    public void ejecutar() {
        System.out.println("ejecutando comando de iniciar partida correctamente.....");
        this.partida.iniciar();
    }
}
