package si.um.feri.rocksolid.network;

import com.badlogic.gdx. Gdx;
import si.um.feri.rocksolid. managers.ClimbingSpotManager;
import si. um.feri. rocksolid.utils.Geolocation;

public class MqttHandler {

    private final ClimbingSpotManager climbingSpotManager;

    public MqttHandler(ClimbingSpotManager climbingSpotManager) {
        this. climbingSpotManager = climbingSpotManager;
    }

    public void onPersonLocationReceived(double latitude, double longitude, String personId) {
        Geolocation location = new Geolocation(latitude, longitude);

        Gdx.app.postRunnable(() -> {
            climbingSpotManager.addPerson(location, personId);
        });
    }

    public void onMessageReceived(double latitude, double longitude, String message) {
        Geolocation location = new Geolocation(latitude, longitude);

        Gdx.app. postRunnable(() -> {
            climbingSpotManager. addMessage(location, message);
        });
    }
}
