package Entidades.fabricas;

import Entidades.Mazo;
//fabrica abstracta para el mazo
public interface IMazoFactory {
    Mazo crearMazo(ICartaFactory cartaFactory);
}
