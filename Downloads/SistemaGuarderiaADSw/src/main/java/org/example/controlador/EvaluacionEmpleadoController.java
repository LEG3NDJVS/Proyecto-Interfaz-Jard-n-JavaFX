package org.example.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.modelo.Evaluacion;
import org.example.persistencia.GestorArchivos;

public class EvaluacionEmpleadoController extends BaseController{

    @FXML private TextField tfEmpleadoNombre;
    @FXML private TextField tfBuscarEmpleado;
    @FXML private TextField tfNombre;
    @FXML private TextField tfCargo;
    @FXML private TextField tfSucursal;
    @FXML private TextField tfFechaIngreso;
    @FXML private TextField tfExperiencia;
    @FXML private Slider sliderPuntaje;
    @FXML private TextField tfPuntaje;
    @FXML private TextArea taFortalezas;
    @FXML private TextArea taMejora;
    @FXML private TextArea taObservaciones;
    @FXML private TextField tfFechaEvaluacion;

    @FXML
    public void initialize() {
        tfPuntaje.setText(String.valueOf((int) sliderPuntaje.getValue()));
        sliderPuntaje.valueProperty().addListener((obs, oldVal, newVal) -> tfPuntaje.setText(String.valueOf(newVal.intValue())));
        tfPuntaje.textProperty().addListener((obs, oldValue, newValue) -> actualizarSliderDesdeTexto(newValue));
    }

    @FXML
    private void guardarEvaluacion() {
        if (tfNombre.getText().isBlank() || tfCargo.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Evaluación", "Empleado incompleto", "Busque o escriba un empleado antes de guardar la evaluación.");
            return;
        }
        Evaluacion evaluacion = new Evaluacion(tfNombre.getText().trim(), tfCargo.getText().trim(), tfPuntaje.getText().trim());
        String linea = evaluacion.getEmpleado() + ";" + evaluacion.getCargo() + ";" + evaluacion.getCalificacion() + ";" +
                valor(tfFechaEvaluacion) + ";" + limpiar(taFortalezas.getText()) + ";" + limpiar(taMejora.getText()) + ";" + limpiar(taObservaciones.getText());
        GestorArchivos.guardarEvaluacionCSV(linea);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Evaluación", "Evaluación guardada",
                "Empleado: " + evaluacion.getEmpleado() + "\nCargo: " + evaluacion.getCargo() + "\nPuntaje: " + evaluacion.getCalificacion() + "\n\nGuardado en evaluaciones.csv");
    }

    @FXML
    private void cancelar() {
        tfBuscarEmpleado.clear(); tfEmpleadoNombre.clear(); tfNombre.clear(); tfCargo.clear(); tfSucursal.clear(); tfFechaIngreso.clear(); tfExperiencia.clear();
        taFortalezas.clear(); taMejora.clear(); taObservaciones.clear();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Evaluación", "Operación cancelada", "El formulario fue limpiado.");
    }

    @FXML
    private void verHistorial() {
        StringBuilder historial = new StringBuilder();
        int contador = 0;
        for (String linea : GestorArchivos.leerLineas("evaluaciones.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 3) {
                contador++;
                historial.append(contador).append(". ").append(datos[0]).append(" - ").append(datos[1]).append(" - Puntaje: ").append(datos[2]).append("\n");
            }
        }
        if (contador == 0) historial.append("No hay evaluaciones registradas todavía.");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Historial", "Evaluaciones registradas", historial.toString());
    }

    @FXML
    private void buscarEmpleado() {
        String busqueda = tfBuscarEmpleado.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Búsqueda", "Campo vacío", "Escriba nombre o cédula.");
            return;
        }
        for (String linea : GestorArchivos.leerLineas("empleados.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 4) {
                String nombre = datos[0].trim();
                String cedula = datos[1].trim();
                if (nombre.toLowerCase().contains(busqueda) || cedula.toLowerCase().contains(busqueda)) {
                    tfEmpleadoNombre.setText(nombre);
                    tfNombre.setText(nombre);
                    tfCargo.setText(datos[2].trim());
                    tfSucursal.setText(datos[3].trim());
                    if (datos.length > 4) tfFechaIngreso.setText(datos[4].trim());
                    if (datos.length > 10) tfExperiencia.setText(datos[10].trim() + " años");
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Búsqueda", "Empleado encontrado", "Se cargaron los datos de: " + nombre);
                    return;
                }
            }
        }
        mostrarAlerta(Alert.AlertType.WARNING, "Búsqueda", "Empleado no encontrado", "No se encontró: " + busqueda);
    }

    private void actualizarSliderDesdeTexto(String texto) {
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor >= 1 && valor <= 100 && (int) sliderPuntaje.getValue() != valor) sliderPuntaje.setValue(valor);
        } catch (NumberFormatException ignored) {}
    }

    private String valor(TextField campo) { return campo.getText() == null ? "" : campo.getText().trim(); }
    private String limpiar(String texto) { return texto == null ? "" : texto.replace(";", ",").replace("\n", " "); }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
