package si.um.feri.rocksolid.data;
import com.badlogic.gdx.utils.Array;
import si.um.feri.rocksolid.utils.Geolocation;

import java.util.HashSet;
import java.util.Set;


public class ClimbingSpot {
    public final Geolocation location;
    public final String name;

    // For counting people
    private Set<String> currentPeopleSet = new HashSet<>();
    private Set<String> previousPeopleSet = new HashSet<>();

    private Array<Message> messages = new Array<>();

    public ClimbingSpot(Geolocation location, String name) {
        this.location = location;
        this.name = name;
    }

    public String toString() {
        return name + " at " + location.toString() + " with " + getNumberOfPeople() + " people.";
    }

    public void print() {
        System.out.println(this.toString());
    }

    public void refreshPeople() {
        previousPeopleSet = currentPeopleSet;
        currentPeopleSet = new HashSet<>();
    }

    public void addOrRefreshPerson(String personName) {
        // CAN ALSO BE USED FOR PERSON IDS DOESN'T MATTER
        previousPeopleSet.remove(personName);
        currentPeopleSet.add(personName);
    }

    public int getNumberOfPeople() {
        return currentPeopleSet.size() + previousPeopleSet.size();
    }

    public void addMessage(Message message) {
        messages.add(message);
        System.out.println("[" + name + "] Novo sporočilo: " + message);
    }

    public Array<Message> getMessages() {
        return new Array<>(messages);
    }

    public int getMessageCount() {
        return messages.size;
    }
}
