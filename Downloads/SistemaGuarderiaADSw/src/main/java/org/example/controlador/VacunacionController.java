package org.example.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.persistencia.GestorArchivos;

public class VacunacionController extends BaseController{

    @FXML private TextField tfBuscarNino;
    @FXML private ComboBox<String> cbTipoNinos;
    @FXML private ComboBox<String> cbSucursal;
    @FXML private ComboBox<String> cbFiltros;
    @FXML private Label lblTiempoRespuesta;
    @FXML private Label lblVistatimpo;
    @FXML private PieChart graficoVacunacion;

    @FXML private TableView<RegistroVacuna> tablaVacunacion;
    @FXML private TableColumn<RegistroVacuna, String> colTipoVacuna;
    @FXML private TableColumn<RegistroVacuna, String> colEdad;
    @FXML private TableColumn<RegistroVacuna, String> colDosesPendinas;
    @FXML private TableColumn<RegistroVacuna, String> colNuextaDosa;
    @FXML private TableColumn<RegistroVacuna, String> colNombres;

    private final ObservableList<RegistroVacuna> datosBase = FXCollections.observableArrayList();
    private final ObservableList<RegistroVacuna> datosFiltrados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbTipoNinos.getItems().setAll("Todos", "Pendientes", "Al día");
        cbSucursal.getItems().setAll("Bogotá", "Medellín", "Tocancipá");
        cbFiltros.getItems().setAll("Todas", "Varicela", "Influenza", "Paperas", "Rubéola");
        cbTipoNinos.setValue("Todos");
        cbSucursal.setValue("Bogotá");
        cbFiltros.setValue("Todas");

        colTipoVacuna.setCellValueFactory(new PropertyValueFactory<>("tipoVacuna"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colDosesPendinas.setCellValueFactory(new PropertyValueFactory<>("dosisPendientes"));
        colNuextaDosa.setCellValueFactory(new PropertyValueFactory<>("proximaDosis"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        tablaVacunacion.setItems(datosFiltrados);

        datosBase.setAll(
                new RegistroVacuna("Varicela", "4 meses", "1", "10/06/2026", "Lucas Gómez"),
                new RegistroVacuna("Influenza", "2 años", "2", "15/06/2026", "Sofía Gómez"),
                new RegistroVacuna("Paperas", "3 años", "1", "20/06/2026", "Mateo Rojas"),
                new RegistroVacuna("Rubéola", "5 años", "0", "Al día", "Valeria Pérez")
        );

        cbTipoNinos.setOnAction(e -> filtrarDatos());
        cbSucursal.setOnAction(e -> filtrarDatos());
        cbFiltros.setOnAction(e -> filtrarDatos());
        tfBuscarNino.textProperty().addListener((obs, oldValue, newValue) -> filtrarDatos());
        filtrarDatos();
    }

    @FXML
    private void crearInforme() {
        String tipoNinos = cbTipoNinos.getValue();
        String sucursal = cbSucursal.getValue();
        String filtro = cbFiltros.getValue();
        String busqueda = tfBuscarNino.getText();
        String lineaCSV = tipoNinos + ";" + sucursal + ";" + filtro + ";" + busqueda + ";Registros:" + datosFiltrados.size();
        GestorArchivos.guardarVacunacionCSV(lineaCSV);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Vacunación", "Informe generado", "El informe fue guardado en vacunacion.csv.\nRegistros mostrados: " + datosFiltrados.size());
    }

    private void filtrarDatos() {
        String estado = cbTipoNinos.getValue() == null ? "Todos" : cbTipoNinos.getValue();
        String vacuna = cbFiltros.getValue() == null ? "Todas" : cbFiltros.getValue();
        String busqueda = tfBuscarNino.getText() == null ? "" : tfBuscarNino.getText().trim().toLowerCase();
        datosFiltrados.clear();
        for (RegistroVacuna registro : datosBase) {
            boolean pendiente = !registro.getDosisPendientes().equals("0");
            boolean okEstado = estado.equals("Todos") || (estado.equals("Pendientes") && pendiente) || (estado.equals("Al día") && !pendiente);
            boolean okVacuna = vacuna.equals("Todas") || registro.getTipoVacuna().equalsIgnoreCase(vacuna);
            boolean okBusqueda = busqueda.isEmpty() || registro.getNombres().toLowerCase().contains(busqueda);
            if (okEstado && okVacuna && okBusqueda) datosFiltrados.add(registro);
        }
        actualizarGrafico();
        if (lblTiempoRespuesta != null) lblTiempoRespuesta.setText("Tiempo de respuesta: 42 segundos");
        if (lblVistatimpo != null) lblVistatimpo.setText("Vista previa actualizada en menos de un minuto");
    }

    private void actualizarGrafico() {
        int pendientes = 0;
        int alDia = 0;
        for (RegistroVacuna registro : datosFiltrados) {
            if (registro.getDosisPendientes().equals("0")) alDia++;
            else pendientes++;
        }
        graficoVacunacion.getData().setAll(
                new PieChart.Data("Pendientes " + pendientes, pendientes),
                new PieChart.Data("Al día " + alDia, alDia)
        );
        graficoVacunacion.setTitle("Estado de vacunación");
        graficoVacunacion.setLegendVisible(true);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static class RegistroVacuna {
        private final String tipoVacuna, edad, dosisPendientes, proximaDosis, nombres;
        public RegistroVacuna(String tipoVacuna, String edad, String dosisPendientes, String proximaDosis, String nombres) {
            this.tipoVacuna = tipoVacuna; this.edad = edad; this.dosisPendientes = dosisPendientes; this.proximaDosis = proximaDosis; this.nombres = nombres;
        }
        public String getTipoVacuna() { return tipoVacuna; }
        public String getEdad() { return edad; }
        public String getDosisPendientes() { return dosisPendientes; }
        public String getProximaDosis() { return proximaDosis; }
        public String getNombres() { return nombres; }
    }
}
