package dtos;

import java.io.Serializable;

public class CartaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String color;
    private String valor;

    public CartaDTO(String color, String valor) {
        this.color = color;
        this.valor = valor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
