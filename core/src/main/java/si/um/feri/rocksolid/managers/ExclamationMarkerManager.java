package si.um.feri.rocksolid.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import si.um.feri.rocksolid.data.ClimbingSpot;
import si.um.feri.rocksolid.utils.MapRasterTiles;

import java.util.HashMap;
import java.util.Map;

public class ExclamationMarkerManager {
    private ModelBatch modelBatch;
    private Model coneModel;
    private Model sphereModel;
    private Environment environment;
    private Camera camera;
    private ClimbingSpotManager climbingSpotManager;

    private final Map<ClimbingSpot, ExclamationMarker> markers = new HashMap<>();

    private static final float BASE_HEIGHT = 70f;
    private static final float CONE_HEIGHT = 25f;
    private static final float CONE_RADIUS = 5f;
    private static final float SPHERE_RADIUS = 5f;
    private static final float ANIMATION_SPEED = 2f;
    private static final float BOUNCE_AMPLITUDE = 10f;
    private static final float MIN_SPACING = 2f;
    private static final float MAX_SPACING = 6f;

    private float animationTime = 0f;

    public ExclamationMarkerManager(Camera camera, ClimbingSpotManager climbingSpotManager) {
        this.camera = camera;
        this.climbingSpotManager = climbingSpotManager;
        this.modelBatch = new ModelBatch();

        createModels();
        setupEnvironment();
    }

    private void createModels() {
        ModelBuilder modelBuilder = new ModelBuilder();

        Material coneMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));
        Material sphereMaterial = new Material(ColorAttribute.createDiffuse(Color.YELLOW));

        long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

        // Create inverted cone (point down) for the top part
        coneModel = modelBuilder.createCone(
            CONE_RADIUS * 2, CONE_HEIGHT, CONE_RADIUS * 2,
            16, coneMaterial, attributes
        );

        // Create sphere for the bottom dot
        sphereModel = modelBuilder.createSphere(
            SPHERE_RADIUS * 2, SPHERE_RADIUS * 2, SPHERE_RADIUS * 2,
            16, 16, sphereMaterial, attributes
        );
    }

    private void setupEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    public void update(float deltaTime) {
        animationTime += deltaTime * ANIMATION_SPEED;

        Array<ClimbingSpot> spots = climbingSpotManager.getClimbingSpots();

        // Add markers for spots with notifications
        for (ClimbingSpot spot : spots) {
            if (spot.messages != null && spot.messages.size > 0) {
                if (!markers.containsKey(spot)) {
                    markers.put(spot, new ExclamationMarker(spot));
                }
            } else {
                markers.remove(spot);
            }
        }

        // Remove markers for spots that no longer exist
        markers.keySet().removeIf(spot -> !spots.contains(spot, true));

        // Update marker positions
        for (ExclamationMarker marker : markers.values()) {
            marker.update(animationTime);
        }
    }

    public void render() {
        if (markers.isEmpty()) return;

        // Enable depth testing
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

        // Disable face culling to render both sides
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);

        // Enable blending for smooth colors
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        modelBatch.begin(camera);
        for (ExclamationMarker marker : markers.values()) {
            modelBatch.render(marker.coneInstance, environment);
            modelBatch.render(marker.sphereInstance, environment);
        }
        modelBatch.end();

        // Re-enable face culling for other rendering
//        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void dispose() {
        modelBatch.dispose();
        coneModel.dispose();
        sphereModel.dispose();
        markers.clear();
    }

    private class ExclamationMarker {
        ModelInstance coneInstance;
        ModelInstance sphereInstance;
        ClimbingSpot spot;
        float phaseOffset;

        ExclamationMarker(ClimbingSpot spot) {
            this.spot = spot;
            this.coneInstance = new ModelInstance(coneModel);
            this.sphereInstance = new ModelInstance(sphereModel);
            // Random phase offset for variety
            this.phaseOffset = (float) (Math.random() * Math.PI * 2);
        }

        void update(float time) {
            Vector2 pixelPos = MapRasterTiles.getPixelPosition(
                spot.location.lat,
                spot.location.lng,
                GameManager.INSTANCE.getBeginTile()
            );

            float phase = time + phaseOffset;
            // Sine wave for smooth bounce (0 to 1)
            float bounceNormalized = (float) (Math.sin(phase) + 1f) / 2f;
            float bounceOffset = bounceNormalized * BOUNCE_AMPLITUDE;

            // Spacing varies: larger when going up, smaller when going down
            float spacing = MIN_SPACING + bounceNormalized * (MAX_SPACING - MIN_SPACING);

            float baseZ = BASE_HEIGHT + bounceOffset;

            // Sphere position (bottom dot)
            float sphereZ = baseZ;
            sphereInstance.transform.setToTranslation(pixelPos.x, pixelPos.y, sphereZ);

            // Cone position (above sphere with variable spacing)
            // Rotate cone 180 degrees on X axis to point downward (inverted)
            float coneZ = sphereZ + SPHERE_RADIUS + spacing + CONE_HEIGHT / 2f;
            coneInstance.transform.setToTranslation(pixelPos.x, pixelPos.y, coneZ);
            coneInstance.transform.rotate(Vector3.X, -90f);


            // Color based on notification count
            Color markerColor = getColorForNotificationCount(spot.messages.size);
            setInstanceColor(coneInstance, markerColor);
            setInstanceColor(sphereInstance, markerColor);
        }

        private void setInstanceColor(ModelInstance instance, Color color) {
            for (com.badlogic.gdx.graphics.g3d.model.Node node : instance.nodes) {
                for (com.badlogic.gdx.graphics.g3d.model.NodePart part : node.parts) {
                    part.material.set(ColorAttribute.createDiffuse(color));
                }
            }
        }

        private Color getColorForNotificationCount(int count) { // TODO: add gradients
            if (count <= 2) {
                return new Color(Color.YELLOW); // Yellow
            } else if (count <= 5) {
                return new Color(Color.ORANGE); // Orange
            } else {
                return new Color(Color.RED); // Red
            }
        }
    }
}
