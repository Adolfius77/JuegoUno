package serealizador;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dtos.MensajeDTO;
import interfaces.ISerializador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class serializador implements ISerializador {

    private static final Gson gson = new Gson();


    @Override
    public String  serealizar(MensajeDTO mensaje) {
        if(mensaje == null){
            return null;
        }else{
            return gson.toJson(mensaje);
        }
    }

    @Override
    public MensajeDTO desearealizar(String json) {
        if(json == null || json.trim().isEmpty()){
            return null;
        }
        try{
            return gson.fromJson(json, MensajeDTO.class);

        }catch(JsonSyntaxException e){
            System.err.println("Error al deserializar: " + e.getMessage());
            return null;
        }
    }
}