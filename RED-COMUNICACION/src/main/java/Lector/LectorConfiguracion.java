/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Lector;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author USER
 */
public class LectorConfiguracion {
    private Properties propiedades;
    
    public LectorConfiguracion(){
        propiedades = new Properties();
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if(input == null){
                System.out.println("no se encontro el archivo de propiedades");
                return;
            }
            propiedades.load(input);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public String getIpServidor(){
        return propiedades.getProperty("servidor.ip", "localhost");
    }
    public int getPuertoServidor() {
        String puerto = propiedades.getProperty("servidor.puerto", "8080");
        return Integer.parseInt(puerto);
    }
}
