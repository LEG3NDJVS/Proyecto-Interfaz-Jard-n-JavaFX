package org.example.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;

public class BaseController {

    protected void cambiarPantalla(Node nodo, String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = (Stage) nodo.getScene().getWindow();

            Scene sceneActual = stage.getScene();

            if (sceneActual == null) {
                sceneActual = new Scene(root, 1400, 900);
                stage.setScene(sceneActual);
            } else {
                sceneActual.setRoot(root);
            }

            stage.setTitle(titulo);
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de navegación");
            alerta.setHeaderText("No se pudo abrir la pantalla");
            alerta.setContentText("Ruta: " + rutaFXML + "\n\n" + e.getMessage());
            alerta.showAndWait();
        }
    }

    @FXML
    protected void abrirInicio(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/PanelPrincipal.fxml", "Panel Principal");
    }

    @FXML
    protected void abrirInscripcion(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/InscripcionNino.fxml", "Inscripción de Niño");
    }

    @FXML
    protected void abrirPagos(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/PagoPension.fxml", "Pago de Pensión");
    }

    @FXML
    protected void abrirAlimentacion(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/Alimentacion.fxml", "Alimentación");
    }

    @FXML
    protected void abrirVacunacion(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/Vacunacion.fxml", "Vacunación");
    }

    @FXML
    protected void abrirActividad(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/Actividad.fxml", "Actividad");
    }

    @FXML
    protected void abrirRegistroEmpleado(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/RegistroEmpleado.fxml", "Registro de Empleado");
    }

    @FXML
    protected void abrirEvaluacionEmpleado(javafx.event.ActionEvent event) {
        cambiarPantalla((Node) event.getSource(), "/fxml/EvaluacionEmpleado.fxml", "Evaluación de Empleado");
    }
}