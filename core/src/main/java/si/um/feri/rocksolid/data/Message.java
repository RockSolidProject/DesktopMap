package si.um.feri. rocksolid.data;

public class Message {
    public final String content;
    public final String type;
    public final String timestamp;
    public final double latitude;
    public final double longitude;
    public final String sender;

    public Message(String content, String type, String timestamp, double latitude, double longitude, String sender) {
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sender = sender;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", type, sender, content);
    }
}
