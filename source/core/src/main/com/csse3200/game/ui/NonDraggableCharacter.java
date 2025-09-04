package com.csse3200.game.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.services.ServiceLocator;
import com.badlogic.gdx.Gdx;


public class NonDraggableCharacter extends UIComponent {

    private Image image; // The image representing the character

    // Default values for the customisable parameters
    private String texturePath = "images/box_boy_leaf.png"; // Default texture path
    private float offsetX = 0f; // Default x position
    private float offsetY = 0f; // Default y position
    private float scale = 0.15f; // Scale factor for the image size

    /**
     * Initializes the character by adding the image actor to the stage.
     */
    @Override
    public void create() {
        super.create();
        addActors();
    }

    /**
     * Adds the character image actor to the stage
     * with the specified image, position, and scale.
     */
    private void addActors() {
        image = new Image(ServiceLocator.getResourceService().getAsset(texturePath, Texture.class));
        image.setSize(image.getWidth() * scale, image.getHeight() * scale);
        image.setPosition(offsetX, offsetY);
        stage.addActor(image);

    }

    /**
     * Disposes of the character image.
     */
    @Override
    public void dispose() {
        super.dispose();
        image.remove();
    }

    /**
     * Draws the character image at the specified offsets.
     * @param batch The SpriteBatch used for drawing.
     */
    @Override
    protected void draw(SpriteBatch batch) {
        image.setPosition(offsetX, offsetY);
    }


    /**
     * Gets the current texture path of the character.
     * @return The texture path as a String.
     */
    public String getTexturePath() {
        return this.texturePath;
    }

    /**
     * Gets the current X offset of the character image.
     * @return The X offset as a float.
     */
    public float getOffsetX() {
        return this.offsetX;
    }

    /**
     * Gets the current Y offset of the character image.
     * @return The Y offset as a float.
     */
    public float getOffsetY() {
        return this.offsetY;
    }

    /**
     * Gets the current scale of the character image.
     * @return The scale as a float.
     */
    public float getScale() {
        return this.scale;
    }


    /**
     * A function that checks if the 'R' key is pressed while the mouse is over the character image.
     * If so, it removes the image from the stage and disposes of the entity.
     */
    @Override
    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (x >= image.getX() && x <= image.getX() + image.getWidth()
                    && y >= image.getY() && y <= image.getY() + image.getHeight()) {

                Gdx.app.postRunnable(() -> {
                    image.remove();
                    entity.dispose();
                });
            }
        }
    }

    // Setters for the customisable parameters
    public void setTexture(String path) { this.texturePath = path; }
    public void setOffsets(float x, float y) { this.offsetX = x; this.offsetY = y; }
    public void setScale(float scale) { this.scale = scale; }
}