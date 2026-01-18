package si.um.feri.rocksolid.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;


import si.um.feri.rocksolid.data.ClimbingSpot;

public class InfoPanelManager {
    public static final float STARTING_HEIGHT = Gdx.graphics.getHeight();
    public static final float PANEL_PADDING_RELATIVE = 0.01f;
    public static final float TEXT_PADDING_RELATIVE = 0.01f;
    public static final float PANEL_WIDTH_RELATIVE = 0.3f;
    public static final float PANEL_HEIGHT_RELATIVE = 1f;

    private static final float LINE_HEIGHT_RELATIVE = 0.02f;
    public Rectangle panelBounds;
    public float fontScale = 1.0f;
    public float panelPadding = 10f;
    public float textPadding = 5f;

    private float lineHeight = 20f;



    private InfoPanelManager() {
        panelBounds = new Rectangle();
        resizePanelBounds();

    }
    public static final InfoPanelManager INSTANCE = new InfoPanelManager();

    private void resizePanelBounds() {
        float height = Gdx.graphics.getHeight();
        float width = Gdx.graphics.getWidth();
        panelPadding = PANEL_PADDING_RELATIVE * width;
        float panelWidth = PANEL_WIDTH_RELATIVE * width - 2 * panelPadding;
        float panelHeight = PANEL_HEIGHT_RELATIVE * height - 2 * panelPadding;
        float panelX = width - panelWidth - panelPadding;
        float panelY = panelPadding; // Bottom-left origin: start from bottom
        lineHeight = LINE_HEIGHT_RELATIVE * height;
        fontScale = height / STARTING_HEIGHT;
        textPadding = TEXT_PADDING_RELATIVE * height;

        panelBounds.set(panelX, panelY, panelWidth, panelHeight);
    }



    public boolean isClickedOnPanel(float screenX, float screenY) {
        resizePanelBounds();
        // Gdx.input uses top-left origin, rendering uses bottom-left
        float convertedY = Gdx.graphics.getHeight() - screenY;
        return panelBounds.contains(screenX, convertedY);
    }

    public void resize() {
        resizePanelBounds();
    }

    public void handleInput() {

        // TODO 1. klik samo zunaj panela deselect climbing spot
        // TODO 2. klik na climbing spot spremeni climbing spot
    }

    public void render(SpriteBatch batch, ShapeRenderer shape, BitmapFont font) {
        ClimbingSpot selectedSpot = GameManager.INSTANCE.getSelectedClimbingSpot();
        if (selectedSpot == null) return;

        resizePanelBounds();

        String text = "Climbing Spot Info\n\n" +
            "Name: " + selectedSpot.name + "\n" +
            "Location: " + "(" + round(selectedSpot.location.lat, 5) + ", " + round(selectedSpot.location.lng, 5) + ")" + "\n" +
            "Current People: " + selectedSpot.getNumberOfPeople();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Create screen projection matrix
        com.badlogic.gdx.math.Matrix4 screenProjection = new com.badlogic.gdx.math.Matrix4();
        screenProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw panel background
        shape.setProjectionMatrix(screenProjection);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(new Color(0.2f, 0.2f, 0.2f, 0.8f));
        shape.rect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        shape.end();

        // Draw text
        batch.setProjectionMatrix(screenProjection);
        batch.begin();
        font.getData().setScale(fontScale);
        font.setColor(Color.WHITE);

        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {
            font.draw(batch, lines[i], panelBounds.x + panelPadding,
                panelBounds.y + panelBounds.height - panelPadding - i * (lineHeight + textPadding));
        }
        batch.end();
        font.getData().setScale(1f);

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
