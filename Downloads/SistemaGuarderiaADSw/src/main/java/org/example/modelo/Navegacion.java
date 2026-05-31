package org.example.controlador;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navegacion {

    public static void cambiarPantalla(Stage stage, String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(Navegacion.class.getResource(rutaFXML));
            Scene scene = new Scene(loader.load(), 1280, 900);
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}