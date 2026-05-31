package org.example.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.modelo.FamiliaCliente;
import org.example.modelo.Padre;
import org.example.modelo.Pension;
import org.example.persistencia.GestorArchivos;

import java.util.ArrayList;
import java.util.List;

public class PagoPensionController extends BaseController{

    @FXML private TextField tfSalario;
    @FXML private Label lblPorcentaje;
    @FXML private Label lblTotal;
    @FXML private Label lblSubSalario;
    @FXML private Label lblSubRecargos;
    @FXML private Label lblSubTotal;

    @FXML private TextField tfApellidoTabla;
    @FXML private TextField tfNombreTabla;
    @FXML private TextField tfBuscarFactura;

    @FXML private CheckBox chkManana;
    @FXML private CheckBox chkTarde;
    @FXML private CheckBox chkSemiCompleta;
    @FXML private CheckBox chkExtendido;
    @FXML private CheckBox chkIngles;
    @FXML private CheckBox chkTransporte;
    @FXML private CheckBox chkNatacion;
    @FXML private CheckBox chkComedor;

    @FXML private ComboBox<String> cbFiltroTabla;
    @FXML private Button btnAsignar;
    @FXML private Button btnRecargar;

    @FXML private TableView<RegistroPago> tablaResumen;
    @FXML private TableColumn<RegistroPago, String> colFamilia;
    @FXML private TableColumn<RegistroPago, String> colNino;
    @FXML private TableColumn<RegistroPago, String> colEmpleados;
    @FXML private TableColumn<RegistroPago, String> colTotal;

    private final ObservableList<RegistroPago> listaPagos = FXCollections.observableArrayList();
    private final ObservableList<RegistroPago> pagosFiltrados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbFiltroTabla.getItems().setAll("Todos", "Pagados", "Pendientes", "Familia Gómez");
        cbFiltroTabla.setValue("Todos");

        colFamilia.setCellValueFactory(new PropertyValueFactory<>("familia"));
        colNino.setCellValueFactory(new PropertyValueFactory<>("nino"));
        colEmpleados.setCellValueFactory(new PropertyValueFactory<>("empleados"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        tablaResumen.setItems(pagosFiltrados);

        cargarPagosExistentes();
        aplicarFiltro();

        CheckBox[] checks = {chkManana, chkTarde, chkSemiCompleta, chkExtendido, chkIngles, chkTransporte, chkNatacion, chkComedor};
        for (CheckBox check : checks) {
            check.setOnAction(e -> calcularPensionSeguro());
        }
        tfSalario.textProperty().addListener((obs, oldValue, newValue) -> calcularPensionSeguro());
        cbFiltroTabla.setOnAction(e -> aplicarFiltro());
        btnAsignar.setOnAction(e -> asignarFamilia());
        btnRecargar.setOnAction(e -> { cargarPagosExistentes(); aplicarFiltro(); });

        calcularPensionSeguro();
    }

    @FXML
    private void calcularPension() {
        calcularPensionSeguro();
    }

    private double calcularPensionSeguro() {
        double salario = parseDinero(tfSalario.getText());
        if (salario <= 0) {
            lblPorcentaje.setText("$0");
            lblTotal.setText("Total: $0");
            if (lblSubSalario != null) lblSubSalario.setText("$0");
            if (lblSubRecargos != null) lblSubRecargos.setText("$0");
            if (lblSubTotal != null) lblSubTotal.setText("$0");
            return 0;
        }

        Padre padre = new Padre("Padre Principal", salario);
        Pension pension = new Pension(padre.getSalario(), salario * 0.15);
        FamiliaCliente familia = new FamiliaCliente(obtenerFamilia());

        int cantidadJornadas = 0;
        if (chkManana.isSelected()) cantidadJornadas++;
        if (chkTarde.isSelected()) cantidadJornadas++;
        if (chkSemiCompleta.isSelected()) cantidadJornadas++;
        if (chkExtendido.isSelected()) cantidadJornadas++;

        double totalJornadas = pension.getValorPension() * cantidadJornadas;
        double serviciosExtra = calcularServiciosExtra();
        double total = totalJornadas + serviciosExtra;

        lblPorcentaje.setText(formatoDinero(pension.getValorPension()));
        lblTotal.setText("Total: " + formatoDinero(total));
        if (lblSubSalario != null) lblSubSalario.setText(formatoDinero(salario));
        if (lblSubRecargos != null) lblSubRecargos.setText(formatoDinero(serviciosExtra));
        if (lblSubTotal != null) lblSubTotal.setText(formatoDinero(total));

        System.out.println("Familia: " + familia.getApellido() + " | Total: " + total);
        return total;
    }

    @FXML
    private void realizarPago() {
        double totalCalculado = calcularPensionSeguro();
        if (totalCalculado <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Pago", "Datos incompletos", "Ingrese un salario válido antes de realizar el pago.");
            return;
        }

        String familia = obtenerFamilia();
        String nino = obtenerNino();
        String total = formatoDinero(totalCalculado);
        String salario = tfSalario.getText();
        String porcentaje = lblPorcentaje.getText();

        String lineaCSV = familia + ";" + nino + ";" + salario + ";" + porcentaje + ";" + total;
        GestorArchivos.guardarPagoCSV(lineaCSV);

        RegistroPago registro = new RegistroPago(familia, nino, obtenerCantidadServicios(), total);
        listaPagos.add(registro);
        aplicarFiltro();

        mostrarAlerta(Alert.AlertType.INFORMATION, "Pago", "Pago realizado", "La pensión fue registrada correctamente en pagos.csv y en la tabla.");
    }

    @FXML
    private void generarPDF() {
        calcularPensionSeguro();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Recibo PDF", "Vista previa del recibo",
                "ADSw-AGUGU\n\n" +
                        "Recibo de pago\n" +
                        "Familia: " + obtenerFamilia() + "\n" +
                        "Niño: " + obtenerNino() + "\n" +
                        "Pensión base: " + lblPorcentaje.getText() + "\n" +
                        "Total pagado: " + lblTotal.getText() + "\n\n" +
                        "Este recibo se puede exportar como PDF en una versión posterior.");
    }

    private void asignarFamilia() {
        if (tfApellidoTabla.getText().isBlank()) tfApellidoTabla.setText("Gómez");
        if (tfNombreTabla.getText().isBlank()) tfNombreTabla.setText("Lucas Gómez");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Familia", "Datos asignados", "La familia y el niño quedaron listos para registrar el pago.");
    }

    private void cargarPagosExistentes() {
        listaPagos.clear();
        for (String linea : GestorArchivos.leerLineas("pagos.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 5) {
                listaPagos.add(new RegistroPago(datos[0], datos[1], "Registrado", datos[4]));
            } else if (datos.length >= 4) {
                listaPagos.add(new RegistroPago(datos[0].replace("Familia ", ""), "Lucas Gómez", "Registrado", datos[3]));
            }
        }
    }

    private void aplicarFiltro() {
        String filtro = cbFiltroTabla.getValue() == null ? "Todos" : cbFiltroTabla.getValue();
        pagosFiltrados.clear();
        for (RegistroPago pago : listaPagos) {
            if (filtro.equals("Todos") || filtro.equals("Pagados") || pago.getFamilia().toLowerCase().contains("gómez") || pago.getFamilia().toLowerCase().contains("gomez")) {
                pagosFiltrados.add(pago);
            }
        }
    }

    private double calcularServiciosExtra() {
        double serviciosExtra = 0;
        if (chkIngles.isSelected()) serviciosExtra += 120000;
        if (chkTransporte.isSelected()) serviciosExtra += 180000;
        if (chkNatacion.isSelected()) serviciosExtra += 150000;
        if (chkComedor.isSelected()) serviciosExtra += 200000;
        return serviciosExtra;
    }

    private String obtenerCantidadServicios() {
        int cantidad = 0;
        if (chkIngles.isSelected()) cantidad++;
        if (chkTransporte.isSelected()) cantidad++;
        if (chkNatacion.isSelected()) cantidad++;
        if (chkComedor.isSelected()) cantidad++;
        return String.valueOf(cantidad);
    }

    private String obtenerFamilia() {
        String familia = tfApellidoTabla == null ? "Gómez" : tfApellidoTabla.getText().trim();
        return familia.isEmpty() ? "Gómez" : familia;
    }

    private String obtenerNino() {
        String nino = tfNombreTabla == null ? "Lucas Gómez" : tfNombreTabla.getText().trim();
        return nino.isEmpty() ? "Lucas Gómez" : nino;
    }

    private double parseDinero(String texto) {
        if (texto == null || texto.isBlank()) return 0;
        try {
            return Double.parseDouble(texto.replace("$", "").replace("COP", "").replace(".", "").replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatoDinero(double valor) {
        return "$" + String.format("%,.0f", valor);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    public static class RegistroPago {
        private final String familia;
        private final String nino;
        private final String empleados;
        private final String total;

        public RegistroPago(String familia, String nino, String empleados, String total) {
            this.familia = familia;
            this.nino = nino;
            this.empleados = empleados;
            this.total = total;
        }
        public String getFamilia() { return familia; }
        public String getNino() { return nino; }
        public String getEmpleados() { return empleados; }
        public String getTotal() { return total; }
    }
}
