package si.um.feri.rocksolid.managers;

import org.jetbrains.annotations.Nullable;
import si.um.feri.rocksolid.constants.Constants;
import si.um.feri.rocksolid.data.ClimbingSpot;
import si.um.feri.rocksolid.utils.MapRasterTiles;
import si.um.feri.rocksolid.utils.ZoomXY;

public class GameManager {
    public static final GameManager INSTANCE = new GameManager();
    private GameManager() {}

    @Nullable
    private ClimbingSpot selectedClimbingSpot = null;
    private ZoomXY centerTile;
    private ZoomXY beginTile;

    public void init() {
        centerTile = MapRasterTiles.getTileNumber(Constants.CENTER_GEOLOCATION.lat, Constants.CENTER_GEOLOCATION.lng, Constants.Map.ZOOM);
        beginTile = new ZoomXY(Constants.Map.ZOOM, centerTile.x - ((Constants.Map.NUM_TILES - 1) / 2), centerTile.y - ((Constants.Map.NUM_TILES - 1) / 2));
    }

    public ZoomXY getCenterTile() {
        return centerTile;
    }

    public ZoomXY getBeginTile() {
        return beginTile;
    }

    public void selectClimbingSpot(ClimbingSpot spot) {
        selectedClimbingSpot = spot;
    }

    @Nullable
    public ClimbingSpot getSelectedClimbingSpot() {
        return selectedClimbingSpot;
    }

    public void deselectClimbingSpot() {
        selectedClimbingSpot = null;
    }
}
