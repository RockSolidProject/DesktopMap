package si.um.feri.rocksolid.managers.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class Button {
    private String text;
    private float relativeX, relativeY, relativeWidth, relativeHeight, relativePadding;
    private Rectangle bounds;
    private Color backgroundColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private Runnable onClick;
    private GlyphLayout layout;

    boolean isHovered = false;

    public Button(
        String text,
        float relativeX, float relativeY, float relativeWidth, float relativeHeight,
        float relativePadding,
        Color backgroundColor,
        Color textColor,
        Color hoverColor,
        Runnable onClick
    ) {
        this.text = text;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeWidth = relativeWidth;
        this.relativeHeight = relativeHeight;
        this.relativePadding = relativePadding;
        this.bounds = new Rectangle();
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.layout = new GlyphLayout();
        this.onClick = onClick;
        this.hoverColor = hoverColor;
    }

    public void updateBounds(float panelX, float panelY, float panelWidth, float panelHeight) {
        float x = panelX + relativeX * panelWidth;
        float y = panelY + relativeY * panelHeight;
        float width = relativeWidth * panelWidth;
        float height = relativeHeight * panelHeight;
        bounds.set(x, y, width, height);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void renderBackground(ShapeRenderer shape, Matrix4 projection) {
        if(!isHovered) shape.setColor(backgroundColor);
        else shape.setColor(hoverColor);
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, Matrix4 projection, float fontScale) {
        font.getData().setScale(fontScale);
        font.setColor(textColor);

        layout.setText(font, text);
        float textX = bounds.x + (bounds.width - layout.width) / 2f;
        float textY = bounds.y + (bounds.height + layout.height) / 2f;

        font.draw(batch, text, textX, textY);
        font.getData().setScale(1f);
    }
//    public void render(SpriteBatch batch, ShapeRenderer shape, BitmapFont font, Matrix4 projection, float fontScale) {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//
//        shape.setProjectionMatrix(projection);
//        shape.begin(ShapeRenderer.ShapeType.Filled);
//        shape.setColor(backgroundColor);
//        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
//        shape.end();
//
//        batch.setProjectionMatrix(projection);
//        batch.begin();
//        font.getData().setScale(fontScale);
//        font.setColor(textColor);
//
//        layout.setText(font, text);
//        float textX = bounds.x + (bounds.width - layout.width) / 2f;
//        float textY = bounds.y + (bounds.height + layout.height) / 2f;
//
//        font.draw(batch, text, textX, textY);
//        font.getData().setScale(1f);
//        batch.end();
//
//        Gdx.gl.glDisable(GL20.GL_BLEND);
//    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void handleInput(float screenX, float screenY, boolean isPressed) {
        if(contains(screenX, screenY)) {
            isHovered = true;
            if(isPressed) {
                onClick.run();
            }
        } else {
            isHovered = false;
        }
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public String getText() {
        return text;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
