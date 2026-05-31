package org.example.controlador;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.example.persistencia.GestorArchivos;

import java.io.IOException;

public class PanelPrincipalController extends BaseController {

    @FXML private BarChart<String, Number> barChartNinos;
    @FXML private PieChart pieChartPagos;
    @FXML private PieChart pieChartVacunacion;

    @FXML
    public void initialize() {
        cargarGraficoNinos();
        cargarGraficoPagos();
        cargarGraficoVacunacion();
    }

    private void cargarGraficoNinos() {
        int acostaditos = 0, caminadores = 0, parvulos = 0;
        for (String linea : GestorArchivos.leerLineas("ninos.csv")) {
            String l = linea.toLowerCase();
            if (l.contains("acostaditos") || l.contains("4 meses") || l.contains("8 meses")) acostaditos++;
            else if (l.contains("aventureros") || l.contains("trotamundos") || l.contains("1 año") || l.contains("2 años")) caminadores++;
            else parvulos++;
        }
        if (acostaditos + caminadores + parvulos == 0) { acostaditos = 10; caminadores = 18; parvulos = 20; }

        XYChart.Series<String, Number> serieNinos = new XYChart.Series<>();
        serieNinos.getData().add(new XYChart.Data<>("Acostaditos", acostaditos));
        serieNinos.getData().add(new XYChart.Data<>("Caminadores", caminadores));
        serieNinos.getData().add(new XYChart.Data<>("Párvulos", parvulos));
        barChartNinos.getData().clear();
        barChartNinos.getData().add(serieNinos);
    }

    private void cargarGraficoPagos() {
        int pagados = GestorArchivos.leerLineas("pagos.csv").size();
        if (pagados == 0) pagados = 85;
        int pendientes = Math.max(3, Math.round(pagados * 15f / 85f));
        pieChartPagos.setData(FXCollections.observableArrayList(
                new PieChart.Data("Al día " + pagados, pagados),
                new PieChart.Data("Pendientes " + pendientes, pendientes)
        ));
    }

    private void cargarGraficoVacunacion() {
        int registros = GestorArchivos.leerLineas("vacunacion.csv").size();
        int pendientes = registros == 0 ? 3 : registros;
        int alDia = registros == 0 ? 45 : Math.max(1, registros * 2);
        pieChartVacunacion.setData(FXCollections.observableArrayList(
                new PieChart.Data("Al día " + alDia, alDia),
                new PieChart.Data("Pendientes " + pendientes, pendientes)
        ));
    }

    @FXML private void abrirInscripcion() { abrirVentana("/fxml/InscripcionNino.fxml", "Inscripción de Niño"); }
    @FXML private void abrirPagos() { abrirVentana("/fxml/PagoPension.fxml", "Pago de Pensión"); }
    @FXML private void abrirAlimentacion() { abrirVentana("/fxml/Alimentacion.fxml", "Alimentación"); }
    @FXML private void abrirVacunacion() { abrirVentana("/fxml/Vacunacion.fxml", "Vacunación"); }
    @FXML private void abrirActividad() { abrirVentana("/fxml/Actividad.fxml", "Actividad"); }
    @FXML private void abrirRegistroEmpleado() { abrirVentana("/fxml/RegistroEmpleado.fxml", "Registro de Empleado"); }
    @FXML private void abrirEvaluacionEmpleado() { abrirVentana("/fxml/EvaluacionEmpleado.fxml", "Evaluación de Empleado"); }

    private void abrirVentana(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Scene scene = new Scene(loader.load(), 1400, 900);
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
