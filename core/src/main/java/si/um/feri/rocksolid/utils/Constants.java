package si.um.feri.rocksolid.utils;

import com.badlogic.gdx.Gdx;

public class Constants {
    public static final int NUM_TILES = 3;
    public static final int ZOOM = 15;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int HUD_WIDTH = Gdx.graphics.getWidth();
    public static final int HUD_HEIGHT = Gdx.graphics.getHeight();

    public static class Camera {
        public static final float FOV = 70f;
        public static final float STARTING_HEIGHT = 400f;
        public static final float MIN_HEIGHT = 50f;
        public static final float MAX_HEIGHT = 2000f;
        public static final float STARTING_PITCH = 25f;
        public static final float MIN_PITCH = 5f;
        public static final float MAX_PITCH = 90f;
        public static final float MOVE_SPEED = 250f;
        public static final float ROTATE_SPEED = 100f;
        public static final float NEAR = 1f;
        public static final float FAR = 3000f;
    }
}
