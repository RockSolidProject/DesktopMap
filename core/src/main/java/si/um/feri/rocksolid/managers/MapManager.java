package si.um.feri.rocksolid.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import si.um.feri.rocksolid.constants.Constants;
import si.um.feri.rocksolid.utils.MapRasterTiles;

import java.io.IOException;

public class MapManager {
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private Texture[] mapTiles;

    public MapManager() throws IOException {
        mapTiles = MapRasterTiles.getRasterTileZone(GameManager.INSTANCE.getCenterTile(), Constants.Map.NUM_TILES);

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(Constants.Map.NUM_TILES, Constants.Map.NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = Constants.Map.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < Constants.Map.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public void render(Matrix4 combinedMatrix) {
        tiledMapRenderer.setView(combinedMatrix, 0, 0, Constants.Map.MAP_WIDTH, Constants.Map.MAP_HEIGHT);
        tiledMapRenderer.render();
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (mapTiles != null) {
            for (Texture tile : mapTiles) {
                if (tile != null) {
                    tile.dispose();
                }
            }
        }
    }
}
