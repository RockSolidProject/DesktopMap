package si.um.feri.rocksolid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import si.um.feri.rocksolid.managers.CameraManager;
import si.um.feri.rocksolid.managers.ClimbingSpotManager;
import si.um.feri.rocksolid.managers.BillboardMarkerManager;
import si.um.feri.rocksolid.managers.GameManager;
import si.um.feri.rocksolid.managers.InfoPanelManager;
import si.um.feri.rocksolid.managers.MapManager;
import si.um.feri.rocksolid.constants.Constants;
import si.um.feri.rocksolid.network.MqttHandler;


import java.io.IOException;

import static si.um.feri.rocksolid.utils.Keys.BACKEND_PASSWORD;
import static si.um.feri.rocksolid.utils.Keys.BACKEND_USERNAME;

public class RasterMap extends ApplicationAdapter implements GestureDetector.GestureListener {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;
    private CameraManager cameraManager;
    private MapManager mapManager;
    private ClimbingSpotManager climbingSpotManager;
    private BillboardMarkerManager billboardMarkerManager;
    private InfoPanelManager infoPanelManager;
    private MqttHandler mqttHandler;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        spriteBatch = new SpriteBatch();
        mqttHandler = new MqttHandler(climbingSpotManager);

        try {
            GameManager.INSTANCE.init();
            mapManager = new MapManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cameraManager = new CameraManager();
        climbingSpotManager = new ClimbingSpotManager();

        billboardMarkerManager = new BillboardMarkerManager(
            cameraManager.getCamera(),
            climbingSpotManager
        );

        mqttHandler = new MqttHandler(climbingSpotManager);
        mqttHandler.startListening();
        infoPanelManager = InfoPanelManager.INSTANCE;

        final String baseUrl = "http://localhost:3001";
        climbingSpotManager.loadFromApi(baseUrl, BACKEND_USERNAME, BACKEND_PASSWORD,
                Constants.CENTER_GEOLOCATION.lat, Constants.CENTER_GEOLOCATION.lng, 5.0);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        cameraManager.handleInput(deltaTime);
        infoPanelManager.handleInput();
        climbingSpotManager.handleInput(cameraManager.getCamera());

        cameraManager.update();
        climbingSpotManager.update(deltaTime);

        billboardMarkerManager.update(cameraManager.getCamera());

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        mapManager.render(cameraManager.getCombinedMatrix());
        climbingSpotManager.render(shapeRenderer, cameraManager.getCombinedMatrix());

        billboardMarkerManager.render();
        infoPanelManager.render(spriteBatch, shapeRenderer, bitmapFont);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        mapManager.dispose();
        if (mqttHandler != null) {
            mqttHandler.stopListening();
        }
    }

    @Override
    public void resize(int width, int height) {
        infoPanelManager.resize();
    }

    public MqttHandler getMqttHandler() {
        return mqttHandler;
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
