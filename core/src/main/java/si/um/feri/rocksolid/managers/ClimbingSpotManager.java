package si.um.feri.rocksolid.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;
import si.um.feri.rocksolid.constants.Constants;
import si.um.feri.rocksolid.data.ClimbingSpot;
import si.um.feri.rocksolid.utils.Geolocation;
import si.um.feri.rocksolid.utils.MapRasterTiles;

public class ClimbingSpotManager {
    // TODO handle number of people and their timers REFRESH

    private final Array<ClimbingSpot> climbingSpots = new Array<>();

    public ClimbingSpotManager() {}

    public void render(ShapeRenderer shapeRenderer, Matrix4 combinedMatrix) {
        shapeRenderer.setProjectionMatrix(combinedMatrix);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ClimbingSpot spot : climbingSpots) {
            Vector2 marker = MapRasterTiles.getPixelPosition(spot.location.lat, spot.location.lng, GameManager.INSTANCE.getBeginTile());
            shapeRenderer.circle(marker.x, marker.y, 10);
        }
        shapeRenderer.end();
    }

    public void onClick(float x, float y) {
        // TODO
    }

    public void addClimbingSpots(Array<ClimbingSpot> spots) {
        this.climbingSpots.addAll(spots);
    }

    public void addClimbingSpot(ClimbingSpot spot) {
        climbingSpots.add(spot);
    }

    public void addPerson(Geolocation location, String personName) {
        ClimbingSpot spot = getClimbingSportWithInDistance(location, Constants.ClimbingSpot.PROXIMITY_DISTANCE_M);
        if (spot != null) {
            spot.addOrRefreshPerson(personName);
        }
    }

    public void addMessage(Geolocation location, String message) {
        ClimbingSpot spot = getClimbingSportWithInDistance(location, Constants.ClimbingSpot.PROXIMITY_DISTANCE_M);
        if (spot != null) {
            // TODO
            // THIS WILL HANDLE NOTIFICATIONS INCLUDING PEOPLE FALLING OR NORMAL MESSAGES
        }
    }

    @Nullable
    public ClimbingSpot getClosestClimbingSpot(Geolocation location) {
        if (climbingSpots.size == 0) return null;
        ClimbingSpot closestClimbingSpot = climbingSpots.get(0);
        int distance = closestClimbingSpot.location.getDistanceMeters(location);

        for (int i = 1; i < climbingSpots.size; i++) {
            ClimbingSpot checkingClimbingSpot = climbingSpots.get(i);
            if (checkingClimbingSpot.location.getDistanceMeters(location) < distance) {
                closestClimbingSpot = checkingClimbingSpot;
                distance = closestClimbingSpot.location.getDistanceMeters(location);
            }
        }
        return closestClimbingSpot;
    }

    @Nullable
    public ClimbingSpot getClimbingSportWithInDistance(Geolocation location, int distanceMeters) {
        ClimbingSpot closestClimbingSpot = getClosestClimbingSpot(location);
        if (closestClimbingSpot != null && closestClimbingSpot.location.getDistanceMeters(location) <= distanceMeters) {
            return closestClimbingSpot;
        }
        return null;
    }
}
