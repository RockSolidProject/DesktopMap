package si.um.feri.rocksolid.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


import si.um.feri.rocksolid.data.ClimbingSpot;
import si.um.feri.rocksolid.managers.helpers.Button;

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

    private final int notificationsAtOnce = 5;

    Matrix4 screenProjection = new Matrix4();
    Array<Button> buttons = new Array<>();
    Array<Button> notificationButtons = new Array<>();
    boolean buttonsSetup = false;

    private int currentNotificationOffset = 0;
    ClimbingSpot previouslySelectedSpot = null;

    private InfoPanelManager() {
        panelBounds = new Rectangle();
        resizeBounds();
        initializeButtons();

    }
    public static final InfoPanelManager INSTANCE = new InfoPanelManager();

    private void initializeButtons() {
        buttons.add(new Button(
            "Close",
            0.7f, 0.9f, 0.25f, 0.08f,
            0.01f,
            new Color(0.5f, 0.1f, 0.1f, 1f),
            new Color(Color.WHITE),
            new Color(0.7f, 0.2f, 0.2f, 1f),
            GameManager.INSTANCE::deselectClimbingSpot
        ));

        buttons.add(new Button(
            "Previous",
            0.15f, 0.05f, 0.25f, 0.08f,
            0.01f,
            new Color(0.1f, 0.1f, 0.5f, 1f),
            new Color(Color.WHITE),
            new Color(0.2f, 0.2f, 0.7f, 1f),
            ()->{
                if(currentNotificationOffset > notificationsAtOnce -1) {
                    currentNotificationOffset-= notificationsAtOnce;
                    updateNotificationButtons();
                }
            }

        ));
        buttons.add(new Button(
            "Next",
            0.6f, 0.05f, 0.25f, 0.08f,
            0.01f,
            new Color(0.1f, 0.5f, 0.1f, 1f),
            new Color(Color.WHITE),
            new Color(0.2f, 0.7f, 0.2f, 1f),

            ()->{
                ClimbingSpot selectedSpot = GameManager.INSTANCE.getSelectedClimbingSpot();
                if(selectedSpot == null) return;
                int maxOffset = Math.max(0, selectedSpot.getMessages().size - notificationsAtOnce);
                if(currentNotificationOffset < maxOffset) {
                    currentNotificationOffset += notificationsAtOnce;
                    updateNotificationButtons();
                }
            }
        ));
    }

    private void updateNotificationButtons() {
        notificationButtons.clear();
        ClimbingSpot selectedSpot = GameManager.INSTANCE.getSelectedClimbingSpot();
        if (selectedSpot == null) return;

        int notificationsToShow = 5;
        for(int i=0; i<notificationsToShow; i++) {
            int notificationIndex = currentNotificationOffset + i;
            if(notificationIndex >= selectedSpot.getMessages().size) break;
            String notificationText = selectedSpot.messages.get(notificationIndex).type.toUpperCase() + ": " + selectedSpot.messages.get(notificationIndex).content;

            Button notificationButton = new Button(
                notificationText,
                0.05f, 0.6f - i * 0.1f, 0.9f, 0.08f,
                0.005f,
                new Color(Color.DARK_GRAY),
                new Color(Color.WHITE),
                new Color(Color.GRAY),
                ()->{}
            );
            notificationButton.setOnClick(()->{
                selectedSpot.messages.removeIndex(notificationIndex);
                updateNotificationButtons();
            });
            notificationButtons.add(notificationButton);
        }
    }

    private void resizeBounds() {
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

        for(Button button: buttons) {
            button.updateBounds(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        }
        for(Button notification: notificationButtons) {
            notification.updateBounds(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        }
    }



    public boolean isClickedOnPanel(float screenX, float screenY) {
        resizeBounds();
        // Gdx.input uses top-left origin, rendering uses bottom-left
        float convertedY = Gdx.graphics.getHeight() - screenY;
        return panelBounds.contains(screenX, convertedY);
    }

    public void resize() {
        resizeBounds();
    }

    public void handleInput() {
        boolean buttonClicked = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        for(Button button: buttons) {
            button.handleInput(mouseX, mouseY, buttonClicked);
        }
        for(Button notification: notificationButtons) {
            notification.handleInput(mouseX, mouseY, buttonClicked);
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shape, BitmapFont font) {
        ClimbingSpot selectedSpot = GameManager.INSTANCE.getSelectedClimbingSpot();
        if (selectedSpot == null) {
            buttonsSetup = false;
            return;
        }
        if(!buttonsSetup || previouslySelectedSpot != selectedSpot) {
            updateNotificationButtons();
            buttonsSetup = true;
            previouslySelectedSpot = selectedSpot;
            currentNotificationOffset = 0;
        }
        resizeBounds();

        String text = "Climbing Spot Info\n\n" +
            "Name: " + selectedSpot.name + "\n" +
            "Location: " + "(" + round(selectedSpot.location.lat, 5) + ", " + round(selectedSpot.location.lng, 5) + ")" + "\n" +
            "Current People: " + selectedSpot.getNumberOfPeople();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Create screen projection matrix

        screenProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw panel background
        shape.setProjectionMatrix(screenProjection);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(new Color(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.8f));
        shape.rect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height);
        for(Button button: buttons) {
            button.renderBackground(shape, screenProjection);
        }
        for(Button notification: notificationButtons) {
            notification.renderBackground(shape, screenProjection);
        }
        shape.end();

        // Draw text
        batch.setProjectionMatrix(screenProjection);
        batch.begin();
        font.getData().setScale(fontScale);
        font.setColor(Color.WHITE);

        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if(i == 0) font.getData().setScale(1.7f*fontScale);
            font.draw(batch, lines[i], panelBounds.x + panelPadding,
                panelBounds.y + panelBounds.height - panelPadding - i * (lineHeight + textPadding));
            if(i == 0) font.getData().setScale(fontScale);
        }
        for(Button button: buttons) {
            button.renderText(batch, font, screenProjection, fontScale);
        }
        for(Button notification: notificationButtons) {
            notification.renderText(batch, font, screenProjection, fontScale);
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
