package si.um.feri.rocksolid.data;

import si.um.feri.rocksolid.utils.Geolocation;

import java.util.HashSet;
import java.util.Set;

public class ClimbingSpot {
    public final Geolocation location;
    public final String name;

    // For counting people
    private Set<String> currentPeopleSet = new HashSet<>();
    private Set<String> previousPeopleSet = new HashSet<>();

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

    // TODO this must be called for evey climbing spot when the timer for removing old expires
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
}
