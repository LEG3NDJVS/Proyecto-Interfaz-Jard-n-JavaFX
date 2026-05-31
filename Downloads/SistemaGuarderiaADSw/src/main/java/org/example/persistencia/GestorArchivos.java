package org.example.persistencia;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {

    public static void guardarNinoCSV(String linea) { guardarLinea("ninos.csv", linea); }
    public static void guardarPagoCSV(String linea) { guardarLinea("pagos.csv", linea); }
    public static void guardarAlimentacionCSV(String linea) { guardarLinea("alimentacion.csv", linea); }
    public static void guardarVacunacionCSV(String linea) { guardarLinea("vacunacion.csv", linea); }
    public static void guardarEmpleadoCSV(String linea) { guardarLinea("empleados.csv", linea); }
    public static void guardarEvaluacionCSV(String linea) { guardarLinea("evaluaciones.csv", linea); }
    public static void guardarActividadCSV(String linea) { guardarLinea("actividades.csv", linea); }

    private static void guardarLinea(String archivo, String linea) {
        try (FileWriter writer = new FileWriter(archivo, true)) {
            writer.write(linea + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> leerLineas(String archivo) {
        Path path = Path.of(archivo);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void guardarAlimentacionJSON(String nombreNino, String familia, String servicios) {
        String json = "{\n"
                + "  \"nombreNino\": \"" + limpiarJSON(nombreNino) + "\",\n"
                + "  \"familia\": \"" + limpiarJSON(familia) + "\",\n"
                + "  \"servicios\": \"" + limpiarJSON(servicios) + "\"\n"
                + "}";
        guardarLinea("alimentacion.json", json);
    }

    private static String limpiarJSON(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
