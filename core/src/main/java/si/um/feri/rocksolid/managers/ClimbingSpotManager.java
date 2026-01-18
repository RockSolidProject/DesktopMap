package si.um.feri.rocksolid.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;
import si.um.feri.rocksolid.constants.Constants;
import si.um.feri.rocksolid.data.ClimbingSpot;
import si.um.feri.rocksolid.utils.Geolocation;
import si.um.feri.rocksolid.utils.MapRasterTiles;
import si.um.feri.rocksolid.network.ApiClient;

import java.io.IOException;
import java.util.List;

public class ClimbingSpotManager {
    private float peopleRefreshTimer = 0f;
    private final Array<ClimbingSpot> climbingSpots = new Array<>();

    public ClimbingSpotManager() {}

    public void loadFromApi(final String baseUrl, final String username, final String password,
                            final double latitude, final double longitude, final double distanceKm) {
        new Thread(() -> {
            ApiClient client = new ApiClient();
            try {
                String token = client.login(baseUrl, username, password);
                List<ClimbingSpot> list = client.fetchNearbyClimbingSpots(baseUrl, token, latitude, longitude, 20);
                final Array<ClimbingSpot> spots = new Array<>();
                spots.addAll(list.toArray(new ClimbingSpot[0]));

                if (Gdx.app != null) {
                    Gdx.app.postRunnable(() -> addClimbingSpots(spots));
                } else {
                    addClimbingSpots(spots);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "ClimbingSpot-API-Loader").start();
    }

    public void render(ShapeRenderer shapeRenderer, Matrix4 combinedMatrix) {
        shapeRenderer.setProjectionMatrix(combinedMatrix);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ClimbingSpot selectedSpot = GameManager.INSTANCE.getSelectedClimbingSpot();
        for (ClimbingSpot spot : climbingSpots) {
            Vector2 marker = MapRasterTiles.getPixelPosition(spot.location.lat, spot.location.lng, GameManager.INSTANCE.getBeginTile());
            if (spot != selectedSpot) {
                shapeRenderer.circle(marker.x, marker.y, 10);
            } else {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.circle(marker.x, marker.y, 12);
                shapeRenderer.setColor(Color.RED);
            }
        }
        shapeRenderer.end();
    }

    public void update(float deltaTime) {
        peopleRefreshTimer += deltaTime;
        if (peopleRefreshTimer >= Constants.ClimbingSpot.PEOPLE_RESET_TIME_S) {
            peopleRefreshTimer = 0f;
            for (ClimbingSpot spot : climbingSpots) {
                spot.refreshPeople();
            }
        }
    }

    public void handleInput(PerspectiveCamera camera) {
        ClimbingSpot selectedSpot = GameManager.INSTANCE.getSelectedClimbingSpot();
        if(isClickedOnPanel(selectedSpot, Gdx.input.getX(), Gdx.input.getY())) return;
        if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
            Geolocation clickedLocation = MapRasterTiles.getMouseCursorGeoLocation(camera);
            onRightClick(clickedLocation);
        } else if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            Geolocation clickedLocation = MapRasterTiles.getMouseCursorGeoLocation(camera);
            onLeftClick(clickedLocation);
        } else if (Gdx.input.isButtonJustPressed(Buttons.MIDDLE) && selectedSpot != null) {
            Geolocation clickedLocation = MapRasterTiles.getMouseCursorGeoLocation(camera);
            onMiddleClick(clickedLocation); // TODO this is for testing
        }
    }
    private boolean isClickedOnPanel(ClimbingSpot selectedSpot, float screenX, float screenY) {
        if(selectedSpot == null) return false;
        return InfoPanelManager.INSTANCE.isClickedOnPanel(screenX, screenY);
    }

    private void onMiddleClick(Geolocation clickedLocation) {
        System.out.println("sth");
        ClimbingSpot spot = getClimbingSportWithInDistance(clickedLocation, Constants.ClimbingSpot.PROXIMITY_DISTANCE_M);
        if (spot != null) {
            int random = java.util.concurrent.ThreadLocalRandom.current().nextInt();
            spot.addOrRefreshPerson(String.valueOf(random));
        }
    }

    private void onLeftClick(Geolocation clickedLocation) {
        ClimbingSpot spot = getClimbingSportWithInDistance(clickedLocation, Constants.ClimbingSpot.PROXIMITY_DISTANCE_M);
        if (spot == null) {
            GameManager.INSTANCE.deselectClimbingSpot();
        } else {
            GameManager.INSTANCE.selectClimbingSpot(spot);
        }
    }

    private void onRightClick(Geolocation clickedLocation) {
        addClimbingSpot(new ClimbingSpot(clickedLocation, "TEST"));
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

    public Array<ClimbingSpot> getClimbingSpots() {
        return climbingSpots;
    }
}
