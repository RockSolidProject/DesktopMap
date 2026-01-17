package si.um. feri.rocksolid.managers;

import com.badlogic. gdx. Gdx;
import com.badlogic.gdx.graphics.Camera;
import com. badlogic.gdx.graphics.Color;
import com. badlogic.gdx.graphics.GL20;
import com.badlogic. gdx.graphics. Texture;
import com.badlogic.gdx.graphics.g2d. TextureRegion;
import com.badlogic. gdx.graphics. g3d.decals.CameraGroupStrategy;
import com. badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals. DecalBatch;
import com.badlogic. gdx.math.Vector2;
import com.badlogic.gdx. utils.Array;
import si.um.feri.rocksolid. data.ClimbingSpot;
import si.um.feri.rocksolid. utils.MapRasterTiles;

import java.util.HashMap;
import java. util.Map;

public class BillboardMarkerManager {
    private DecalBatch decalBatch;
    private Camera camera;

    private Texture singlePersonTexture;  // 1-10 ljudi
    private Texture groupTexture;          // 11+ ljudi

    private final Map<ClimbingSpot, Decal> spotDecals = new HashMap<>();

    private ClimbingSpotManager climbingSpotManager;

    private static final float BASE_SIZE = 30f;
    private static final float MIN_SIZE = 20f;
    private static final float MAX_SIZE = 150f;
    private static final float Z_HEIGHT = 5f;
    private static final int GROUP_THRESHOLD = 10;

    public BillboardMarkerManager(Camera camera, ClimbingSpotManager climbingSpotManager) {
        this. camera = camera;
        this.climbingSpotManager = climbingSpotManager;
        this.decalBatch = new DecalBatch(new CameraGroupStrategy(camera));

        this.singlePersonTexture = new Texture(Gdx. files.internal("user.png"));
        this.groupTexture = new Texture(Gdx. files.internal("group.png"));
    }

    public void update(Camera camera) {
        Array<ClimbingSpot> spots = climbingSpotManager.getClimbingSpots();

        for (ClimbingSpot spot : spots) {
            int peopleCount = spot. getNumberOfPeople();

            if (peopleCount > 0) {
                Decal decal = spotDecals.get(spot);

                if (decal == null) {
                    decal = createDecal(spot, peopleCount);
                    spotDecals.put(spot, decal);
                    System.out.println("[Marker] Ustvarjen marker za:  " + spot.name + " (" + peopleCount + " ljudi)");
                }

                updateDecal(decal, spot, peopleCount);

            } else {
                if (spotDecals.containsKey(spot)) {
                    spotDecals.remove(spot);
                    System.out.println("[Marker] Odstranjen marker za:  " + spot.name);
                }
            }
        }
        spotDecals.keySet().removeIf(spot -> ! spots.contains(spot, true));
    }

    private Decal createDecal(ClimbingSpot spot, int peopleCount) {
        float size = calculateSize(peopleCount);
        Texture texture = (peopleCount > GROUP_THRESHOLD) ? groupTexture : singlePersonTexture;

        Decal decal = Decal.newDecal(size, size, new TextureRegion(texture), true);
        decal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        return decal;
    }

    private void updateDecal(Decal decal, ClimbingSpot spot, int peopleCount) {
        Texture targetTexture = (peopleCount > GROUP_THRESHOLD) ? groupTexture : singlePersonTexture;
        if (decal.getTextureRegion().getTexture() != targetTexture) {
            decal. setTextureRegion(new TextureRegion(targetTexture));
        }

        decal.setColor(getColorForPeopleCount(peopleCount));

        float size = calculateSize(peopleCount);
        decal.setDimensions(size, size);

        Vector2 pixelPos = MapRasterTiles. getPixelPosition(
            spot.location. lat,
            spot.location.lng,
            GameManager.INSTANCE.getBeginTile()
        );
        decal.setPosition(pixelPos. x, pixelPos.y, Z_HEIGHT + size / 2f);

        decal. lookAt(camera.position, camera.up);
    }

    private Color getColorForPeopleCount(int peopleCount) {
        if (peopleCount <= 5) {
            return new Color(0.2f, 0.9f, 0.2f, 1f); // zelena
        } else if (peopleCount <= 10) {
            return new Color(1f, 0.9f, 0.1f, 1f); // rumena
        } else if (peopleCount <= 25) {
            return new Color(1f, 0.5f, 0.1f, 1f); // oranžna
        } else {
            return new Color(0.9f, 0.1f, 0.1f, 1f); // rdeča
        }
    }

    private float calculateSize(int personCount) {
        float scaleFactor = 1f + (float) Math.log10(Math.max(1, personCount))*((float) 2 /3);
        float size = BASE_SIZE * scaleFactor;
        return Math. max(MIN_SIZE, Math.min(MAX_SIZE, size));
    }

    public void render() {
        for (Decal decal :  spotDecals.values()) {
            decalBatch.add(decal);
        }
        decalBatch.flush();
    }

    public void dispose() {
        decalBatch.dispose();
        singlePersonTexture. dispose();
        groupTexture.dispose();
        spotDecals.clear();
    }

    public int getMarkerCount() {
        return spotDecals.size();
    }
}
