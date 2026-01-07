package si.um.feri.rocksolid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import si.um.feri.rocksolid.managers.CameraManager;
import si.um.feri.rocksolid.managers.GameManager;
import si.um.feri.rocksolid.managers.MapManager;

import java.io.IOException;

public class RasterMap extends ApplicationAdapter implements GestureDetector.GestureListener {
    private ShapeRenderer shapeRenderer;
    private CameraManager cameraManager;
    private MapManager mapManager;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        try {
            GameManager.INSTANCE.init();
            mapManager = new MapManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cameraManager = new CameraManager();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        cameraManager.handleInput(deltaTime);

        cameraManager.update();

        ScreenUtils.clear(0, 0, 0, 1);
        mapManager.render(cameraManager.getCombinedMatrix());
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        mapManager.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }
}
