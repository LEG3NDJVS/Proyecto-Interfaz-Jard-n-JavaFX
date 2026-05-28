package org.example;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import java.util.Scanner;

public class Configurador {

    // Direccion del broker MQTT (localhost = misma maquina)
    static final String BROKER        = "tcp://localhost:1883";
    // Identificador unico de este cliente en el broker
    static final String CLIENT_ID     = "configurador01";
    // Topico MQTT donde se enviara la configuracion al monitor
    static final String TOPICO_CONFIG = "cripto/config/monitor01";

    static MqttClient mqttClient;
    // Scanner para leer lo que escribe el usuario en consola
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        // Conectar al broker al iniciar la aplicacion
        conectar();
        System.out.println("Configurador conectado al broker: " + BROKER);
        System.out.println("------------------------------------------");

        // Mostrar menu hasta que el usuario elija salir
        boolean salir = false;
        while (!salir) {
            System.out.println("\nMENU:");
            System.out.println("1. Configurar criptomonedas de los botones");
            System.out.println("2. Configurar umbral de alarma");
            System.out.println("3. Configurar duracion de alarma");
            System.out.println("4. Enviar configuracion completa");
            System.out.println("5. Salir");
            System.out.print("Opcion: ");

            String opcion = scanner.nextLine().trim();

            // Ejecutar la funcion segun la opcion elegida
            switch (opcion) {
                case "1" -> configurarMonedas();
                case "2" -> configurarUmbral();
                case "3" -> configurarDuracion();
                case "4" -> enviarConfiguracion();
                case "5" -> salir = true;
                default  -> System.out.println("Opcion invalida.");
            }
        }

        // Desconectar del broker al cerrar
        mqttClient.disconnect();
        System.out.println("Configurador cerrado.");
    }

    // Valores actuales de configuracion con sus valores por defecto
    static String moneda1  = "BTC";    // Moneda asignada al boton 1
    static String moneda2  = "SOL";    // Moneda asignada al boton 2
    static double umbral1  = 100000;   // Precio limite de alarma para moneda 1
    static double umbral2  = 200;      // Precio limite de alarma para moneda 2
    static int    duracion = 5;        // Duracion de la alarma sonora en segundos

    // Permite al usuario cambiar las monedas asignadas a cada boton
    static void configurarMonedas() {
        System.out.println("\nMonedas disponibles: BTC, SOL");

        // Leer nueva moneda para boton 1 (si no escribe nada, conserva la actual)
        System.out.print("Moneda para Boton 1 (actual: " + moneda1 + "): ");
        String m1 = scanner.nextLine().trim().toUpperCase();
        if (!m1.isEmpty()) moneda1 = m1;

        // Leer nueva moneda para boton 2
        System.out.print("Moneda para Boton 2 (actual: " + moneda2 + "): ");
        String m2 = scanner.nextLine().trim().toUpperCase();
        if (!m2.isEmpty()) moneda2 = m2;

        System.out.println("Monedas configuradas: Boton1=" + moneda1 + " | Boton2=" + moneda2);
    }

    // Permite al usuario definir el precio limite que activara la alarma
    static void configurarUmbral() {
        // Leer umbral para la primera moneda
        System.out.print("\nUmbral de alarma para " + moneda1 + " (actual: $" + umbral1 + "): ");
        try { umbral1 = Double.parseDouble(scanner.nextLine().trim()); } catch (Exception ignored) {}

        // Leer umbral para la segunda moneda
        System.out.print("Umbral de alarma para " + moneda2 + " (actual: $" + umbral2 + "): ");
        try { umbral2 = Double.parseDouble(scanner.nextLine().trim()); } catch (Exception ignored) {}

        System.out.println("Umbrales configurados: " + moneda1 + "=$" + umbral1 + " | " + moneda2 + "=$" + umbral2);
    }

    // Permite definir cuantos segundos sonara el buzzer cuando se active la alarma
    static void configurarDuracion() {
        System.out.print("\nDuracion de la alarma en segundos (actual: " + duracion + "): ");
        try { duracion = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
        System.out.println("Duracion configurada: " + duracion + " segundos");
    }

    // Empaqueta toda la configuracion en JSON y la envia al monitor por MQTT
    static void enviarConfiguracion() throws Exception {
        // Reconectar si se perdio la conexion
        if (!mqttClient.isConnected()) conectar();

        // Crear objeto JSON con todos los parametros de configuracion
        JSONObject config = new JSONObject();
        config.put("btn1",            moneda1);   // Moneda del boton 1
        config.put("btn2",            moneda2);   // Moneda del boton 2
        config.put("umbral_btn1",     umbral1);   // Umbral de alarma boton 1
        config.put("umbral_btn2",     umbral2);   // Umbral de alarma boton 2
        config.put("duracion_alarma", duracion);  // Duracion del buzzer

        // Crear mensaje MQTT con QoS 1 (garantiza entrega al menos una vez)
        // y retained=true (el broker guarda el mensaje para nuevos suscriptores)
        MqttMessage mensaje = new MqttMessage(config.toString().getBytes());
        mensaje.setQos(1);
        mensaje.setRetained(true);
        mqttClient.publish(TOPICO_CONFIG, mensaje);

        System.out.println("\nConfiguracion enviada al monitor:");
        System.out.println(config.toString(2));
    }

    // Establece la conexion con el broker MQTT
    static void conectar() throws Exception {
        mqttClient = new MqttClient(BROKER, CLIENT_ID);
        MqttConnectOptions opciones = new MqttConnectOptions();
        opciones.setCleanSession(true);       // Inicia sesion limpia sin mensajes anteriores
        opciones.setAutomaticReconnect(true); // Reconecta automaticamente si se cae la conexion
        mqttClient.connect(opciones);
    }
}