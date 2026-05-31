package org.example.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.modelo.CarnetVacunas;
import org.example.modelo.Dieta;
import org.example.modelo.Nino;
import org.example.persistencia.GestorArchivos;
import org.example.persistencia.Serializador;

import java.util.ArrayList;

public class InscripcionNinoController extends BaseController {

    @FXML private HBox bottomBar;
    @FXML private Button btnActividades;
    @FXML private Button btnAyuda;
    @FXML private Button btnCancelar;
    @FXML private Button btnEmpleados;
    @FXML private Button btnGuardar;
    @FXML private Button btnInicio;
    @FXML private Button btnListaEspera;
    @FXML private Button btnNinos;
    @FXML private Button btnPagos;
    @FXML private Button btnReportes;
    @FXML private Button btnTituloDatos;
    @FXML private Button btnTituloDetalles;
    @FXML private Button btnVacunas;
    @FXML private ComboBox<String> cbEtapaIzq;
    @FXML private ComboBox<String> cbTipoInscripcion;
    @FXML private VBox contentVBox;
    @FXML private VBox imgCarneVacunas;
    @FXML private Label lblSeccion;
    @FXML private Label lblTitulo;
    @FXML private Label lblUsuario;
    @FXML private HBox mainPanel;
    @FXML private VBox panelDatosNino;
    @FXML private VBox panelDetallesNino;
    @FXML private BorderPane rootPane;
    @FXML private ScrollPane scrollCenter;
    @FXML private VBox sideMenu;
    @FXML private TextArea taDietaInicial;
    @FXML private TextField tfBusqueda;
    @FXML private TextField tfEtapaDer;
    @FXML private TextField tfFechaNacimiento;
    @FXML private TextField tfMarcaLecheDer;
    @FXML private TextField tfMarcaLecheIzq;
    @FXML private TextField tfMarcaPanialDer;
    @FXML private TextField tfMarcaPanialIzq;
    @FXML private TextField tfNombreCompleto;
    @FXML private TextField tfOnzas;
    @FXML private TextField tfRegistroCivil;
    @FXML private HBox topBar;

    @FXML
    public void initialize() {
        cbTipoInscripcion.getItems().setAll("4 meses", "8 meses", "1 año", "2 años", "3 años", "4 años");
        cbEtapaIzq.getItems().setAll("Acostaditos", "Aventureros", "Trotamundos", "Juguetones/Aprendices", "Párvulos");
        cbTipoInscripcion.setValue("4 meses");
        cbEtapaIzq.setValue("Acostaditos");
        tfEtapaDer.setText("Acostaditos");

        cbEtapaIzq.setOnAction(e -> tfEtapaDer.setText(cbEtapaIzq.getValue()));
        tfMarcaPanialIzq.textProperty().addListener((obs, oldValue, newValue) -> tfMarcaPanialDer.setText(newValue));
        tfMarcaLecheIzq.textProperty().addListener((obs, oldValue, newValue) -> tfMarcaLecheDer.setText(newValue));
        tfBusqueda.textProperty().addListener((obs, oldValue, newValue) -> buscarNino(newValue));
    }

    @FXML
    private void guardarNino() {
        if (tfNombreCompleto.getText().isBlank() || tfFechaNacimiento.getText().isBlank() || tfRegistroCivil.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios", null, "Debe completar nombre, fecha de nacimiento y registro civil.");
            return;
        }

        Nino nino = new Nino(tfNombreCompleto.getText().trim(), tfFechaNacimiento.getText().trim(), tfRegistroCivil.getText().trim(),
                tfMarcaPanialIzq.getText().trim(), tfMarcaLecheIzq.getText().trim(), cbTipoInscripcion.getValue());
        Dieta dieta = new Dieta(taDietaInicial.getText());
        CarnetVacunas carnetVacunas = new CarnetVacunas("Carné registrado desde inscripción");
        nino.setDieta(dieta);
        nino.setCarnetVacunas(carnetVacunas);

        String lineaCSV = nino.getNombreCompleto() + ";" + nino.getFechaNacimiento() + ";" + nino.getRegistroCivil() + ";" +
                nino.getMarcaPanial() + ";" + nino.getMarcaLeche() + ";" + nino.getTipoInscripcion() + ";" + cbEtapaIzq.getValue() + ";" + tfOnzas.getText().trim();
        GestorArchivos.guardarNinoCSV(lineaCSV);

        ArrayList<Nino> listaNinos = Serializador.deserializarNinos();
        listaNinos.add(nino);
        Serializador.serializarNinos(listaNinos);

        mostrarAlerta(Alert.AlertType.INFORMATION, "Inscripción", "Inscripción completada",
                "Niño registrado correctamente.\n\nNombre: " + nino.getNombreCompleto() +
                        "\nFecha nacimiento: " + nino.getFechaNacimiento() +
                        "\nRegistro civil: " + nino.getRegistroCivil() +
                        "\nEtapa: " + cbEtapaIzq.getValue() +
                        "\nTipo inscripción: " + nino.getTipoInscripcion() +
                        "\n\nInformación guardada en ninos.csv\nSerialización: Sí");
    }

    @FXML
    private void cancelar() {
        tfNombreCompleto.clear(); tfFechaNacimiento.clear(); tfRegistroCivil.clear(); tfMarcaPanialIzq.clear(); tfMarcaPanialDer.clear();
        tfMarcaLecheIzq.clear(); tfMarcaLecheDer.clear(); tfOnzas.clear(); taDietaInicial.clear(); tfBusqueda.clear();
        cbTipoInscripcion.setValue("4 meses"); cbEtapaIzq.setValue("Acostaditos"); tfEtapaDer.setText("Acostaditos");
    }

    @FXML
    private void agregarListaEspera() {
        ArrayList<Nino> listaNinos = Serializador.deserializarNinos();
        GestorArchivos.guardarNinoCSV("LISTA_ESPERA;" + tfNombreCompleto.getText().trim() + ";" + tfRegistroCivil.getText().trim());
        mostrarAlerta(Alert.AlertType.INFORMATION, "Deserialización", "Niños cargados desde archivo .dat",
                "Total de niños serializados: " + listaNinos.size() + "\n\nArchivo leído: ninos_serializados.dat\nEl niño actual fue enviado a lista de espera.");
    }

    private void buscarNino(String textoBusqueda) {
        if (textoBusqueda == null || textoBusqueda.trim().length() < 3) return;
        String filtro = textoBusqueda.trim().toLowerCase();
        for (String linea : GestorArchivos.leerLineas("ninos.csv")) {
            String[] datos = linea.split(";");
            if (datos.length >= 6 && datos[0].toLowerCase().contains(filtro)) {
                tfNombreCompleto.setText(datos[0]);
                tfFechaNacimiento.setText(datos[1]);
                tfRegistroCivil.setText(datos[2]);
                tfMarcaPanialIzq.setText(datos[3]);
                tfMarcaLecheIzq.setText(datos[4]);
                cbTipoInscripcion.setValue(datos[5]);
                if (datos.length > 6) cbEtapaIzq.setValue(datos[6]);
                if (datos.length > 7) tfOnzas.setText(datos[7]);
                return;
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
