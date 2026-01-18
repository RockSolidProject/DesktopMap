package si.um.feri.rocksolid.network;

import com.badlogic.gdx.Gdx;
import org.eclipse.paho.client.mqttv3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class MqttListener implements MqttCallback {

    private MqttClient client;
    private MqttHandler mqttHandler;
    private boolean isConnected = false;

    public MqttListener(MqttHandler mqttHandler) {
        this.mqttHandler = mqttHandler;
    }

    public void startListening() {
        try {
            String clientId = MqttConfig.CLIENT_ID_PREFIX + System.currentTimeMillis();
            client = new MqttClient(MqttConfig.MQTT_HOST, clientId);

            MqttConnectOptions options = new MqttConnectOptions();

            if (MqttConfig.MQTT_USERNAME == null || MqttConfig.MQTT_PASSWORD == null) {
                throw new RuntimeException("MQTT credentials not set! Check environment variables.");
            }

            options.setUserName(MqttConfig.MQTT_USERNAME);
            options.setPassword(MqttConfig.MQTT_PASSWORD.toCharArray());
            options.setCleanSession(true);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);

            options.setSocketFactory(createTrustAllSSLContext().getSocketFactory());

            client.setCallback(this);

            System.out.println("Connecting to MQTT broker:  " + MqttConfig.MQTT_HOST);
            client.connect(options);
            client.subscribe(MqttConfig.TOPIC_MESSAGES);
            client.subscribe(MqttConfig.TOPIC_LOCATIONS);
        } catch (Exception e) {
            System.err.println("MQTT connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processMessage(String topic, String payload) {
        switch (topic) {
            case MqttConfig.TOPIC_MESSAGES:
                mqttHandler.onAndroidMessageReceived(payload);
                break;
            case MqttConfig.TOPIC_LOCATIONS:
                mqttHandler.onLocationReceived(payload);
                break;
            default:
                System.out.println("Unknown topic: " + topic);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        isConnected = false;

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                startListening();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        System.out.println("MQTT Message received on topic '" + topic + "': " + payload);

        if (Gdx.app != null) {
            Gdx.app.postRunnable(() -> processMessage(topic, payload));
        } else {
            processMessage(topic, payload);
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // not used for receiving
    }

    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
                isConnected = false;
                System.out.println("MQTT disconnected");
            }
        } catch (MqttException e) {
            System.err.println("Error disconnecting MQTT: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected && client != null && client.isConnected();
    }

    private SSLContext createTrustAllSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        return sc;
    }
}
