package si.um.feri.rocksolid.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class CameraManager {
    private PerspectiveCamera camera;

    public CameraManager() {
        camera = new PerspectiveCamera(Constants.Camera.CAMERA_FOW, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 500);
        camera.lookAt(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.near = 1f;
        camera.far = 3000f;
        camera.update();
    }

    public void update() {
        camera.update();
    }

    public Matrix4 getCombinedMatrix() {
        return camera.combined;
    }

    public void handleInput(float deltaTime) {
        float moveSpeed = 250f * deltaTime;
        float rotateSpeed = 100f * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 500);
            camera.direction.set(0, 0, -1);
            camera.up.set(0, 1, 0);
            camera.lookAt(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
            camera.update();
            return;
        }

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
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.rotate(Vector3.Z, rotateSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.rotate(Vector3.Z, -rotateSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.rotate(camera.direction.cpy().crs(camera.up).nor(), rotateSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.rotate(camera.direction.cpy().crs(camera.up).nor(), -rotateSpeed);
        }
    }
}
