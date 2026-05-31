package org.example.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.modelo.Empleado;
import org.example.persistencia.GestorArchivos;

public class RegistroEmpleadoController extends BaseController{

    @FXML private TextField tfNombreCompleto;
    @FXML private TextField tfCedula;
    @FXML private TextField tfFechaNacimiento;
    @FXML private TextField tfDireccion;
    @FXML private TextField tfTelefonoFijo;
    @FXML private TextField tfCelular;
    @FXML private ComboBox<String> cbTituloUniversitario;
    @FXML private TextField tfTarjetaProfesional;
    @FXML private ComboBox<String> cbCargo;
    @FXML private ComboBox<String> cbSucursal;
    @FXML private TextField tfExperiencia;
    @FXML private TextField tfSalarioBase;
    @FXML private ComboBox<String> cbTipoContrato;

    @FXML
    public void initialize() {
        cbTituloUniversitario.getItems().setAll("Licenciatura Infantil", "Psicología", "Nutrición", "Enfermería", "Terapia Ocupacional", "Terapia de Lenguaje");
        cbCargo.getItems().setAll("Educadora Infantil", "Psicóloga", "Nutricionista", "Auxiliar", "Pediatra", "Terapeuta");
        cbSucursal.getItems().setAll("Bogotá", "Medellín", "Tocancipá");
        cbTipoContrato.getItems().setAll("Directo", "Prestación de Servicios", "Temporal");
        cbTituloUniversitario.setValue("Licenciatura Infantil");
        cbCargo.setValue("Educadora Infantil");
        cbSucursal.setValue("Bogotá");
        cbTipoContrato.setValue("Directo");
    }

    @FXML
    private void guardarEmpleado() {
        if (tfNombreCompleto.getText().isBlank() || tfCedula.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Empleado", "Campos obligatorios", "Complete nombre y cédula.");
            return;
        }

        Empleado empleado = new Empleado(tfNombreCompleto.getText().trim(), tfCedula.getText().trim(), cbCargo.getValue(), cbSucursal.getValue());
        String linea = empleado.getNombre() + ";" + empleado.getCedula() + ";" + empleado.getCargo() + ";" + empleado.getSucursal() + ";" +
                valor(tfFechaNacimiento) + ";" + valor(tfDireccion) + ";" + valor(tfTelefonoFijo) + ";" + valor(tfCelular) + ";" +
                cbTituloUniversitario.getValue() + ";" + valor(tfTarjetaProfesional) + ";" + valor(tfExperiencia) + ";" + valor(tfSalarioBase) + ";" + cbTipoContrato.getValue();
        GestorArchivos.guardarEmpleadoCSV(linea);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado", "Empleado registrado",
                "Nombre: " + empleado.getNombre() + "\nCargo: " + empleado.getCargo() + "\nSucursal: " + empleado.getSucursal() + "\n\nGuardado en empleados.csv");
    }

    @FXML
    private void cancelar() {
        limpiarFormulario();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Empleado", "Operación cancelada", "El formulario fue limpiado.");
    }

    @FXML
    private void limpiarFormulario() {
        tfNombreCompleto.clear(); tfCedula.clear(); tfFechaNacimiento.clear(); tfDireccion.clear(); tfTelefonoFijo.clear(); tfCelular.clear();
        tfTarjetaProfesional.clear(); tfExperiencia.clear(); tfSalarioBase.clear();
        cbTituloUniversitario.setValue("Licenciatura Infantil");
        cbCargo.setValue("Educadora Infantil");
        cbSucursal.setValue("Bogotá");
        cbTipoContrato.setValue("Directo");
    }

    private String valor(TextField campo) { return campo.getText() == null ? "" : campo.getText().trim(); }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
