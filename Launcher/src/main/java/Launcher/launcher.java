////package Launcher;
////
////import red.Servidor;
////import vista.MenuPrincipal;
////
////import java.awt.*;
////
////public class launcher {
////    public static void main(String[] args) {
////        Thread hiloServidor = new Thread(() -> {
////            try {
////                Servidor servidor = new Servidor(8080);
////                servidor.iniciar();
////            } catch (Exception e) {
////                System.out.println("error al iniciar el servidor: " + e.getMessage());
////            }
////        });
////        hiloServidor.start();
////        try {
////            Thread.sleep(500);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////        System.out.println("[cliente] construyendo interfaces y controladores");
////        EventQueue.invokeLater(new Runnable() {
////            @Override
////            public void run() {
////                try {
////                    MenuPrincipal menu = new MenuPrincipal();
////                    menu.setVisible(true);
////
////                } catch (Exception e) {
////                    System.out.println("error al iniciar la interfaz grafica: " + e.getMessage());
////                }
////            }
////        });
////    }
////}
////
////
