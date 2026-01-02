package si.um.feri.rocksolid.constants;

import si.um.feri.rocksolid.utils.Geolocation;
import si.um.feri.rocksolid.utils.MapRasterTiles;

public class Constants {
    public static final Geolocation CENTER_GEOLOCATION = new Geolocation(46.5583797, 15.6390876);

    public static class Map {
        public static final int NUM_TILES = 6;
        public static final int ZOOM = 17;
        public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
        public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    }

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
