package Mappers;

import Entidades.Carta;
import Entidades.CartaAccion;
import Entidades.CartaNumerica;
import Entidades.cartaComodin;
import Entidades.enums.Color;
import Entidades.enums.TipoAccion;
import Entidades.fabricas.ICartaFactory;
import dtos.CartaDTO;

public class CartaMapper {
    public CartaDTO toDTO(Carta carta) {
        if(carta == null) {
            return null;
        }
        String colorStr = carta.getColor().name();
        String valorStr = "";

        if(carta instanceof CartaNumerica) {
            CartaNumerica cn = (CartaNumerica) carta;
            valorStr = String.valueOf(cn.getValor());
        } else if (carta instanceof CartaAccion) {
            CartaAccion ca = (CartaAccion) carta;
            valorStr = ca.getAccion().name();
        } else if (carta instanceof cartaComodin) {
            cartaComodin cc = (cartaComodin) carta;
            valorStr = cc.esMasCuatro() ? "MAS_4": "CAMBIO_COLOR";
        }
        return new CartaDTO(colorStr, valorStr);

    }
    public Carta toEntity(CartaDTO dto, String id, ICartaFactory factory) {
        if(dto != null){
            Color color = Color.valueOf(dto.getColor());
            String valor = dto.getValor();

            if(valor.matches("\\d+")) {
                int numero = Integer.parseInt(valor);
                return factory.crearNumerica(id, color, numero);
            }
            if(valor.equals("MAS_4") || valor.equals("CAMBIO_COLOR")) {
                boolean esMasCuatro = valor.equals("MAS_4");
                return factory.crearComodin(id,color,esMasCuatro);
            }
            TipoAccion accion = TipoAccion.valueOf(valor);
            return factory.crearAccion(id,color,accion);

        }
        return null;
    }
}
