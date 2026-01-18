package si.um.feri.rocksolid.network;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import si.um.feri.rocksolid.managers.ClimbingSpotManager;
import si.um.feri.rocksolid.utils.Geolocation;

public class MqttHandler {
    private final ClimbingSpotManager climbingSpotManager;
    private final Gson gson = new Gson();
    private MqttListener mqttListener;

    public MqttHandler(ClimbingSpotManager climbingSpotManager) {
        this.climbingSpotManager = climbingSpotManager;
        this.mqttListener = new MqttListener(this);
    }

    public void startListening() {
        mqttListener.startListening();
    }

    public void stopListening() {
        mqttListener.disconnect();
    }

    public void onAndroidMessageReceived(String jsonMessage) {
        try {
            JsonObject messageObj = gson.fromJson(jsonMessage, JsonObject.class);

            double latitude = messageObj.get("latitude").getAsDouble();
            double longitude = messageObj.get("longitude").getAsDouble();
            String content = messageObj.get("message").getAsString();
            String type = messageObj.get("type").getAsString();
            String timestamp = messageObj.get("timestamp").getAsString();
            String sender = messageObj.has("sender") ? messageObj.get("sender").getAsString() : "AndroidUser";

            System.out.println("Message received [" + type + "]: " + content);

            Gdx.app. postRunnable(() -> {
                if (climbingSpotManager != null) {
                    climbingSpotManager.addMessage(latitude, longitude, content, type, timestamp, sender);
                } else {
                    System.err.println("ClimbingSpotManager is not initialized yet!");
                }
            });

        } catch (Exception e) {
            System.err.println("Napaka pri parsiranju Android sporočila: " + e. getMessage());
            e.printStackTrace();
        }
    }

    public void onLocationReceived(String jsonLocation) {
        try {
            JsonObject locationObj = gson.fromJson(jsonLocation, JsonObject.class);

            double latitude = locationObj.get("latitude").getAsDouble();
            double longitude = locationObj.get("longitude").getAsDouble();
            String username = locationObj.get("username").getAsString();
            String timestamp = locationObj.get("timestamp").getAsString();
            String acceleration = locationObj.get("acceleration").getAsString();

            System.out.println("Location received:  " + username + " at (" + latitude + ", " + longitude + ")");

            Gdx.app.postRunnable(() -> {
                Geolocation location = new Geolocation(latitude, longitude);
                climbingSpotManager.addPerson(location, username);
            });

        } catch (Exception e) {
            System.err.println("Napaka pri parsiranju lokacijskih podatkov: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return mqttListener.isConnected();
    }
}
