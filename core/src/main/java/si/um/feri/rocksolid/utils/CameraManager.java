package si.um.feri.rocksolid.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CameraManager {
    private final PerspectiveCamera camera;
    private float currentPitch;

    public CameraManager() {
        camera = new PerspectiveCamera(Constants.Camera.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, Constants.Camera.STARTING_HEIGHT);
        camera.lookAt(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.near = Constants.Camera.NEAR;
        camera.far = Constants.Camera.FAR;
        camera.rotate(camera.direction.cpy().crs(camera.up).nor(), Constants.Camera.STARTING_PITCH);
        currentPitch = Constants.Camera.STARTING_PITCH;
        camera.update();
    }

    public void update() {
        camera.update();
    }

    public Matrix4 getCombinedMatrix() {
        return camera.combined;
    }

    public void handleInput(float deltaTime) {
        float moveSpeed = Constants.Camera.MOVE_SPEED * deltaTime;
        float rotateSpeed = Constants.Camera.ROTATE_SPEED * deltaTime;

        Vector3 forwardNormalizedVector = new Vector3(camera.direction.x, camera.direction.y, 0).nor();
        Vector3 rightNormalizedVector = forwardNormalizedVector.cpy().crs(Vector3.Z).nor();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(forwardNormalizedVector.cpy().scl(moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(forwardNormalizedVector.cpy().scl(-moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.translate(rightNormalizedVector.cpy().scl(-moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(rightNormalizedVector.cpy().scl(moveSpeed));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            camera.translate(0, 0, -moveSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            camera.translate(0, 0, moveSpeed);
        }
        // Enforce max and min camera height
        camera.position.z = Math.max(Constants.Camera.MIN_HEIGHT, Math.min(Constants.Camera.MAX_HEIGHT, camera.position.z));

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.rotate(Vector3.Z, rotateSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.rotate(Vector3.Z, -rotateSpeed);
        }

        // Pitch control
        float pitchDelta = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            pitchDelta = rotateSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            pitchDelta = -rotateSpeed;
        }
        float newPitch = currentPitch + pitchDelta;
        if (newPitch >= Constants.Camera.MIN_PITCH && newPitch <= Constants.Camera.MAX_PITCH) {
            camera.rotate(camera.direction.cpy().crs(camera.up).nor(), pitchDelta);
            currentPitch = newPitch;
        }
    }
}
