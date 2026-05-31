package org.example.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.persistencia.GestorArchivos;

public class AlimentacionController extends BaseController {

    @FXML private TextField tfBuscarFicha;
    @FXML private TextField tfNombreTabla;
    @FXML private TextField tfFamiliaCliente;
    @FXML private TextField tfSalario;
    @FXML private Label lblPorcentaje;
    @FXML private Label lblNombreNino;
    @FXML private Label lblHijosDetalle;

    @FXML private ComboBox<String> cbFiltroFamilia;
    @FXML private Label lblVistaPDF;

    @FXML private CheckBox chkVitaminas;
    @FXML private CheckBox chkCaldo;
    @FXML private CheckBox chkFruta;
    @FXML private CheckBox chkComedor;
    @FXML private CheckBox chkManana;
    @FXML private CheckBox chkTarde;
    @FXML private CheckBox chkSemiCompleta;
    @FXML private CheckBox chkExtendido;

    @FXML private Button btnAsignar;
    @FXML private Button btnRecargar;

    @FXML private TableView<RegistroFamilia> tablaFamilia;
    @FXML private TableColumn<RegistroFamilia, String> colFamilia;
    @FXML private TableColumn<RegistroFamilia, String> colNino;
    @FXML private TableColumn<RegistroFamilia, String> colEmpleados;
    @FXML private TableColumn<RegistroFamilia, String> colTotal;

    @FXML private TableView<RegistroDieta> tablaDieta;
    @FXML private TableColumn<RegistroDieta, String> colAlimentosDieta;
    @FXML private TableColumn<RegistroDieta, String> colNinoDieta;
    @FXML private TableColumn<RegistroDieta, String> colTotalDieta;

    @FXML private TableView<RegistroProhibido> tablaProhibidos;
    @FXML private TableColumn<RegistroProhibido, String> colAlimentosProh;
    @FXML private TableColumn<RegistroProhibido, String> colMotivos;
    @FXML
    private TableColumn<RegistroProhibido, String> colOnzas;

    private final ObservableList<RegistroFamilia> listaFamilia = FXCollections.observableArrayList();
    private final ObservableList<RegistroDieta> listaDieta = FXCollections.observableArrayList();
    private final ObservableList<RegistroProhibido> listaProhibidos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTablas();
        cargarFamiliasDesdeNinosCSV();
        actualizarComboFamilias();
        actualizarDietaDesdeControles();
        actualizarCalculoReferencial();

        btnAsignar.setOnAction(e -> asignarFamilia());
        btnRecargar.setOnAction(e -> recargarDatos());
        lblVistaPDF.setOnMouseClicked(event -> vistaPreviaPDF());
        cbFiltroFamilia.setOnAction(e -> filtrarFamilia());
        tfBuscarFicha.textProperty().addListener((obs, oldText, newText) -> filtrarFamilia());

        CheckBox[] checks = {chkVitaminas, chkCaldo, chkFruta, chkComedor, chkManana, chkTarde, chkSemiCompleta, chkExtendido};
        for (CheckBox check : checks) {
            if (check != null) check.setOnAction(e -> { actualizarDietaDesdeControles(); actualizarCalculoReferencial(); });
        }
        if (tfSalario != null) tfSalario.textProperty().addListener((obs, oldValue, newValue) -> actualizarCalculoReferencial());
    }

    private void configurarTablas() {
        colFamilia.setCellValueFactory(new PropertyValueFactory<>("familia"));
        colNino.setCellValueFactory(new PropertyValueFactory<>("nino"));
        colEmpleados.setCellValueFactory(new PropertyValueFactory<>("empleados"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tablaFamilia.setItems(listaFamilia);

        colAlimentosDieta.setCellValueFactory(new PropertyValueFactory<>("alimento"));
        colNinoDieta.setCellValueFactory(new PropertyValueFactory<>("nino"));
        colTotalDieta.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        tablaDieta.setItems(listaDieta);

        colAlimentosProh.setCellValueFactory(new PropertyValueFactory<>("alimento"));
        colMotivos.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colOnzas.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        tablaProhibidos.setItems(listaProhibidos);
    }

    @FXML
    private void crearFichaNutricional() {
        String nombreNino = obtenerNino();
        String familia = obtenerFamilia();
        String servicios = serviciosSeleccionados();

        GestorArchivos.guardarAlimentacionCSV(nombreNino + ";" + familia + ";" + servicios);
        GestorArchivos.guardarAlimentacionJSON(nombreNino, familia, servicios);

        if (listaFamilia.stream().noneMatch(r -> r.getNino().equalsIgnoreCase(nombreNino))) {
            listaFamilia.add(new RegistroFamilia(familia, nombreNino, "Nutricionista", "Ficha activa"));
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Ficha Nutricional", "Ficha creada correctamente",
                "Niño: " + nombreNino + "\nFamilia: " + familia + "\nServicios: " + servicios + "\n\nGuardado en alimentacion.csv y alimentacion.json");
    }

    @FXML
    private void asignarFamilia() {
        String nombreNino = obtenerNino();
        String familia = obtenerFamilia();
        listaFamilia.add(new RegistroFamilia(familia, nombreNino, "Nutricionista", "Asignado"));
        actualizarComboFamilias();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Asignación", "Familia asignada", "El niño fue asociado correctamente a la familia cliente.");
    }

    @FXML
    private void recargarDatos() {
        cargarFamiliasDesdeNinosCSV();
        actualizarComboFamilias();
        actualizarDietaDesdeControles();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Recarga", "Información actualizada", "Se recargaron los niños registrados desde ninos.csv.");
    }

    @FXML
    private void vistaPreviaPDF() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Vista previa PDF", "Ficha nutricional",
                "ADSw-AGUGU\n\nFicha nutricional\n" +
                        "Niño: " + obtenerNino() + "\n" +
                        "Familia: " + obtenerFamilia() + "\n\n" +
                        "Dieta inicial:\n" + tablaDietaComoTexto() + "\n" +
                        "Alimentos prohibidos:\n" + tablaProhibidosComoTexto());
    }

    private void cargarFamiliasDesdeNinosCSV() {
        listaFamilia.clear();
        for (String linea : GestorArchivos.leerLineas("ninos.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 1 && !datos[0].isBlank()) {
                listaFamilia.add(new RegistroFamilia("Gómez", datos[0], "Nutricionista", "Registrado"));
            }
        }
        if (listaFamilia.isEmpty()) {
            listaFamilia.add(new RegistroFamilia("Gómez", "Lucas Gómez - 4 meses", "Nutricionista", "Ejemplo"));
        }
        filtrarFamilia();
    }

    private void actualizarComboFamilias() {
        cbFiltroFamilia.getItems().clear();
        cbFiltroFamilia.getItems().add("Todos");
        for (RegistroFamilia registro : listaFamilia) {
            if (!cbFiltroFamilia.getItems().contains(registro.getNino())) {
                cbFiltroFamilia.getItems().add(registro.getNino());
            }
        }
        cbFiltroFamilia.setValue("Todos");
    }

    private void filtrarFamilia() {
        String filtroCombo = cbFiltroFamilia.getValue() == null ? "Todos" : cbFiltroFamilia.getValue();
        String filtroTexto = tfBuscarFicha.getText() == null ? "" : tfBuscarFicha.getText().trim().toLowerCase();
        ObservableList<RegistroFamilia> filtrados = FXCollections.observableArrayList();
        for (RegistroFamilia registro : listaFamilia) {
            boolean coincideCombo = filtroCombo.equals("Todos") || registro.getNino().equalsIgnoreCase(filtroCombo);
            boolean coincideTexto = filtroTexto.isEmpty() || registro.getNino().toLowerCase().contains(filtroTexto) || registro.getFamilia().toLowerCase().contains(filtroTexto);
            if (coincideCombo && coincideTexto) filtrados.add(registro);
        }
        tablaFamilia.setItems(filtrados);
    }

    private void actualizarDietaDesdeControles() {
        listaDieta.clear();
        listaDieta.add(new RegistroDieta("Leche fórmula", "Lucas", "4 onzas"));
        listaDieta.add(new RegistroDieta("Puré de frutas", "Lucas", "1 porción"));
        listaDieta.add(new RegistroDieta("Cereal infantil", "Lucas", "1 taza"));
        if (chkVitaminas.isSelected()) listaDieta.add(new RegistroDieta("Vitaminas", "Lucas", "1 dosis"));
        if (chkCaldo.isSelected()) listaDieta.add(new RegistroDieta("Caldo especial", "Lucas", "1 taza"));
        if (chkFruta.isSelected()) listaDieta.add(new RegistroDieta("Fruta extra", "Lucas", "1 porción"));
        if (chkComedor.isSelected()) listaDieta.add(new RegistroDieta("Comedor", "Lucas", "Servicio activo"));

        listaProhibidos.clear();
        listaProhibidos.add(new RegistroProhibido("Miel", "Menor de 1 año", "0"));
        listaProhibidos.add(new RegistroProhibido("Frutos secos", "Riesgo de asfixia", "0"));
        listaProhibidos.add(new RegistroProhibido("Leche entera", "Restricción por edad", "0"));
    }

    private void actualizarCalculoReferencial() {
        if (lblPorcentaje == null || tfSalario == null) return;
        double salario = parseDinero(tfSalario.getText());
        double base = salario * 0.15;
        int jornadas = 0;
        if (chkManana.isSelected()) jornadas++;
        if (chkTarde.isSelected()) jornadas++;
        if (chkSemiCompleta.isSelected()) jornadas++;
        if (chkExtendido.isSelected()) jornadas++;
        lblPorcentaje.setText("$" + String.format("%,.0f", base * Math.max(jornadas, 1)));
    }

    private String obtenerNino() {
        String nino = tfNombreTabla.getText() == null ? "" : tfNombreTabla.getText().trim();
        if (nino.isEmpty() && lblNombreNino != null) nino = lblNombreNino.getText();
        return nino.isEmpty() ? "Lucas Gómez - 4 meses" : nino;
    }

    private String obtenerFamilia() {
        String familia = tfFamiliaCliente.getText() == null ? "" : tfFamiliaCliente.getText().trim();
        return familia.isEmpty() ? "Gómez" : familia;
    }

    private String serviciosSeleccionados() {
        StringBuilder servicios = new StringBuilder();
        if (chkVitaminas.isSelected()) servicios.append("Vitaminas, ");
        if (chkCaldo.isSelected()) servicios.append("Caldo Especial, ");
        if (chkFruta.isSelected()) servicios.append("Fruta Extra, ");
        if (chkComedor.isSelected()) servicios.append("Comedor, ");
        return servicios.length() == 0 ? "Sin servicios extra" : servicios.substring(0, servicios.length() - 2);
    }

    private String tablaDietaComoTexto() {
        StringBuilder sb = new StringBuilder();
        for (RegistroDieta dieta : listaDieta) sb.append("- ").append(dieta.getAlimento()).append(": ").append(dieta.getCantidad()).append("\n");
        return sb.toString();
    }

    private String tablaProhibidosComoTexto() {
        StringBuilder sb = new StringBuilder();
        for (RegistroProhibido prohibido : listaProhibidos) sb.append("- ").append(prohibido.getAlimento()).append(": ").append(prohibido.getMotivo()).append("\n");
        return sb.toString();
    }

    private double parseDinero(String texto) {
        if (texto == null || texto.isBlank()) return 0;
        try { return Double.parseDouble(texto.replace(".", "").replace(",", ".")); }
        catch (NumberFormatException e) { return 0; }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static class RegistroFamilia {
        private final String familia, nino, empleados, total;
        public RegistroFamilia(String familia, String nino, String empleados, String total) { this.familia = familia; this.nino = nino; this.empleados = empleados; this.total = total; }
        public String getFamilia() { return familia; }
        public String getNino() { return nino; }
        public String getEmpleados() { return empleados; }
        public String getTotal() { return total; }
    }
    public static class RegistroDieta {
        private final String alimento, nino, cantidad;
        public RegistroDieta(String alimento, String nino, String cantidad) { this.alimento = alimento; this.nino = nino; this.cantidad = cantidad; }
        public String getAlimento() { return alimento; }
        public String getNino() { return nino; }
        public String getCantidad() { return cantidad; }
    }
    public static class RegistroProhibido {
        private final String alimento, motivo, cantidad;
        public RegistroProhibido(String alimento, String motivo, String cantidad) { this.alimento = alimento; this.motivo = motivo; this.cantidad = cantidad; }
        public String getAlimento() { return alimento; }
        public String getMotivo() { return motivo; }
        public String getCantidad() { return cantidad; }
    }
}
