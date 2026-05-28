package org.example;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject; //Lectura en formato JSON
import java.net.URI; // Manejar la dirección web de API
import java.net.http.*;//Solicitudes http
import java.time.Duration; //iempos de espera

public class SensorMonedas {

    // Direccion del broker MQTT
    static final String BROKER    = "tcp://localhost:1883";
    // Identificador unico de este cliente en el broker
    static final String CLIENT_ID = "sensorMonedas01";
    // Segundos entre cada consulta a la API de Binance
    static final int    INTERVALO = 10;

    static MqttClient mqttClient;

    public static void main(String[] args) throws Exception {
        // Conectar al broker al iniciar
        conectar();
        System.out.println("Conectado al broker MQTT: " + BROKER);

        // Bucle infinito: consultar y publicar precios cada INTERVALO segundos
        while (true) {
            try {
                // Publicar precio de Bitcoin en su topico MQTT
                publicarPrecio("BTCUSDT", "cripto/precios/btc");
                // Publicar precio de Solana en su topico MQTT
                publicarPrecio("SOLUSDT", "cripto/precios/sol");
            } catch (Exception e) {
                // Si falla, mostrar error e intentar reconectar
                System.out.println("Error: " + e.getMessage());
                System.out.println("Reconectando...");
                conectar();
            }
            // Esperar el tiempo configurado antes de la siguiente consulta
            Thread.sleep(INTERVALO * 1000L);
        }
    }

    // Establece la conexion con el broker MQTT
    static void conectar() throws Exception {
        mqttClient = new MqttClient(BROKER, CLIENT_ID);
        MqttConnectOptions opciones = new MqttConnectOptions();
        opciones.setCleanSession(true);       // Sesion limpia al conectar
        opciones.setAutomaticReconnect(true); // Reconexion automatica si se pierde
        mqttClient.connect(opciones);
    }

    // Consulta el precio de una criptomoneda en Binance y lo publica en MQTT
    static void publicarPrecio(String simbolo, String topico) throws Exception {
        // Reconectar si es necesario antes de publicar
        if (!mqttClient.isConnected()) {
            conectar();
        }

        // Construir la URL de la API de Binance para el simbolo indicado
        String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + simbolo;

        // Crear cliente HTTP con timeout de 5 segundos
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // Construir la peticion GET a la API de Binance
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Ejecutar la peticion y obtener la respuesta como texto
        HttpResponse<String> response = http.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Parsear el JSON de respuesta y extraer el campo "price"
        // Ejemplo de respuesta: {"symbol":"BTCUSDT","price":"76943.70"}
        JSONObject json = new JSONObject(response.body());
        String precio = json.getString("price");

        // Crear mensaje MQTT con el precio como texto plano
        MqttMessage mensaje = new MqttMessage(precio.getBytes());
        mensaje.setQos(0);           // QoS 0: enviar sin confirmacion (suficiente para precios)
        mensaje.setRetained(true);   // El broker guarda el ultimo precio para nuevos suscriptores
        mqttClient.publish(topico, mensaje);

        System.out.println("Publicado en " + topico + " -> $" + precio);
    }
}