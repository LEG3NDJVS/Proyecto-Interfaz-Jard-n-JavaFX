package org.example.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.modelo.Actividad;
import org.example.persistencia.GestorArchivos;

public class ActividadController extends BaseController{

    @FXML private TextField tfBuscarActividad;
    @FXML private TextField tfLeche;
    @FXML private TextField tfPapillaFruta;
    @FXML private TextField tfPureVerdura;
    @FXML private TextField tfPureSuntana;
    @FXML private TextField tfBuncado;
    @FXML private TextField tfPapillaRrianta;
    @FXML private TextField tfPapillaLuca;
    @FXML private TextField tfLecheAhiera;
    @FXML private TextField tfUio;
    @FXML private TextField tfPlanto;
    @FXML private TextField tfProhibido1;
    @FXML private TextField tfProhibido2;
    @FXML private TextField tfProhibido3;
    @FXML private TextField tfProhibido4;
    @FXML private TextField tfProhibido5;
    @FXML private TextField tfProhibido6;
    @FXML private TextField tfMarcaLeche;
    @FXML private TextField tfOnzas;
    @FXML private Label lblNombreNino;
    @FXML private Label lblEtapaNino;

    @FXML private TableView<RegistroParticipacion> tablaParticipacion;
    @FXML private TableColumn<RegistroParticipacion, String> colNinoTabla;
    @FXML private TableColumn<RegistroParticipacion, String> colParticipacion;
    @FXML private ComboBox<String> cbEmpleados;
    @FXML private CheckBox chkPadre;
    @FXML private TextField tfCosto;
    @FXML private TextField tfRecursos;
    @FXML private Label lblTotal;
    @FXML private CheckBox chkFotografo;
    @FXML private CheckBox chkRefrigerio;
    @FXML private CheckBox chkTransporte;
    @FXML private CheckBox chkComedor;
    @FXML private Label lblTotalFinal;

    private final ObservableList<RegistroParticipacion> listaParticipacion = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarEmpleadosDesdeCSV();
        colNinoTabla.setCellValueFactory(new PropertyValueFactory<>("nino"));
        colParticipacion.setCellValueFactory(new PropertyValueFactory<>("participacion"));
        tablaParticipacion.setItems(listaParticipacion);
        cargarParticipantesDesdeCSV();

        CheckBox[] checks = {chkFotografo, chkRefrigerio, chkTransporte, chkComedor, chkPadre};
        for (CheckBox check : checks) check.setOnAction(event -> actualizarTotal());
        tfCosto.textProperty().addListener((obs, oldValue, newValue) -> actualizarTotal());
        tfBuscarActividad.textProperty().addListener((obs, oldValue, newValue) -> buscarActividad());
        actualizarTotal();
    }

    @FXML
    private void crearActividad() {
        String nombreActividad = obtenerNombreActividad();
        String nino = lblNombreNino.getText();
        String empleado = cbEmpleados.getValue();
        String costo = lblTotalFinal.getText();
        String recursos = tfRecursos.getText().isBlank() ? "Material didáctico" : tfRecursos.getText();

        Actividad actividad = new Actividad(nombreActividad, nino, empleado);
        GestorArchivos.guardarActividadCSV(actividad.getNombre() + ";" + actividad.getNino() + ";" + actividad.getEmpleado() + ";" + costo + ";" + recursos);

        listaParticipacion.add(new RegistroParticipacion(nino, "Confirmada"));
        mostrarAlerta(Alert.AlertType.INFORMATION, "Actividad", "Actividad registrada correctamente",
                "Actividad: " + actividad.getNombre() + "\nNiño: " + actividad.getNino() + "\nEmpleado responsable: " + actividad.getEmpleado() + "\nCosto final: " + costo + "\n\nGuardado en actividades.csv");
    }

    @FXML
    private void actualizarTotal() {
        double total = parseDinero(tfCosto.getText());
        if (chkFotografo.isSelected()) total += 50000;
        if (chkRefrigerio.isSelected()) total += 25000;
        if (chkTransporte.isSelected()) total += 80000;
        if (chkComedor.isSelected()) total += 40000;
        if (chkPadre.isSelected()) total *= 0.75;
        lblTotal.setText(formatoDinero(total));
        lblTotalFinal.setText(formatoDinero(total));
    }

    private void cargarEmpleadosDesdeCSV() {
        cbEmpleados.getItems().clear();
        for (String linea : GestorArchivos.leerLineas("empleados.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 1 && !datos[0].isBlank() && !cbEmpleados.getItems().contains(datos[0])) cbEmpleados.getItems().add(datos[0]);
        }
        if (cbEmpleados.getItems().isEmpty()) cbEmpleados.getItems().addAll("Marta Ramirez", "Juan Perez", "Laura Gomez", "Carlos Rojas");
        cbEmpleados.setValue(cbEmpleados.getItems().get(0));
    }

    private void cargarParticipantesDesdeCSV() {
        listaParticipacion.clear();
        for (String linea : GestorArchivos.leerLineas("ninos.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 1 && !datos[0].isBlank()) listaParticipacion.add(new RegistroParticipacion(datos[0], "Disponible"));
        }
        if (listaParticipacion.isEmpty()) listaParticipacion.add(new RegistroParticipacion(lblNombreNino.getText(), "Confirmada"));
    }

    private void buscarActividad() {
        String texto = tfBuscarActividad.getText() == null ? "" : tfBuscarActividad.getText().trim().toLowerCase();
        if (texto.isEmpty()) return;
        for (String linea : GestorArchivos.leerLineas("actividades.csv")) {
            if (linea.toLowerCase().contains(texto)) {
                String[] datos = linea.split(";");
                if (datos.length >= 5) {
                    lblNombreNino.setText(datos[1]);
                    cbEmpleados.setValue(datos[2]);
                    tfRecursos.setText(datos[4]);
                }
                return;
            }
        }
    }

    private String obtenerNombreActividad() {
        String texto = tfBuscarActividad.getText() == null ? "" : tfBuscarActividad.getText().trim();
        return texto.isEmpty() ? "Visita al Parque Simón Bolívar" : texto;
    }

    private double parseDinero(String texto) {
        if (texto == null || texto.isBlank()) return 0;
        try { return Double.parseDouble(texto.replace("$", "").replace(".", "").replace(",", ".").trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private String formatoDinero(double valor) { return "$" + String.format("%,.0f", valor); }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static class RegistroParticipacion {
        private final String nino;
        private final String participacion;
        public RegistroParticipacion(String nino, String participacion) { this.nino = nino; this.participacion = participacion; }
        public String getNino() { return nino; }
        public String getParticipacion() { return participacion; }
    }
}
