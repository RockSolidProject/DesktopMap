package si.um.feri.rocksolid.network;

public class MqttConfig {
    public static final String MQTT_HOST = "ssl://" + System.getenv("BROKER") + ":8883";
    public static final String MQTT_USERNAME = System.getenv("USERNAME");
    public static final String MQTT_PASSWORD = System.getenv("PASSWORD");

    // topics
    public static final String TOPIC_MESSAGES = "messages";
    public static final String TOPIC_LOCATIONS = "sensors";

    // client ID
    public static final String CLIENT_ID_PREFIX = "DesktopMap_";
}
