package org.example.persistencia;

import org.example.modelo.Nino;

import java.io.*;
import java.util.ArrayList;

public class Serializador {

    private static final String ARCHIVO_NINOS = "ninos_serializados.dat";

    public static void serializarNinos(ArrayList<Nino> ninos) {

        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO_NINOS))) {

            salida.writeObject(ninos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Nino> deserializarNinos() {

        File archivo = new File(ARCHIVO_NINOS);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ARCHIVO_NINOS))) {

            return (ArrayList<Nino>) entrada.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}