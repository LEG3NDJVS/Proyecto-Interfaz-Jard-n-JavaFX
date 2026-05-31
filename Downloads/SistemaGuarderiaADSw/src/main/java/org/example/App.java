package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource("/fxml/PanelPrincipal.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);

        stage.setTitle("ADSw-AGUGU - Sistema de Guardería");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}