package com.csse3200.game.components.proximityinfodisplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.components.player.PlayerActions;
import com.csse3200.game.data.MenuSpriteData;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.BodyUserData;
import com.csse3200.game.ui.UIComponent;

/**
 *  A class for implementing a popup display with level information.
 */
public class LevelPopupDisplay extends UIComponent {
    /*popup window*/
    private Window popupDisplay;
    /*popup window's visibility*/
    private boolean isDisplayed = false;
    private MenuSpriteData spriteData;

    /**
     * Default constructor for when no arguments are passed.
     */
    public LevelPopupDisplay() {
    }

    /**
     * Constructor for when level sprite data argument is passed to the class.
     * @param spriteData level sprite data.
     */
    public LevelPopupDisplay(MenuSpriteData spriteData) {
        this.spriteData = spriteData;
    }

    /**
     *  Creation of the popup display and collision event.
     */
    @Override
    public void create() {
        super.create();

        // Listen to collisions with entity
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);

        // Creates popup display.
        popupDisplay = new Window (setLevelTitle(spriteData), skin);
        popupDisplay.setMovable(false);
        popupDisplay.setSize(500, 500);
        popupDisplay.setPosition(
                (Gdx.graphics.getWidth() - popupDisplay.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - popupDisplay.getHeight()) / 2f
        );

        // Adds text in the popup display.
        Label message = new Label(setLevelDescription(spriteData), skin);
        popupDisplay.add(message).pad(10).row();

        // Sets popup display to false when created.
        popupDisplay.setVisible(false);
        stage.addActor(popupDisplay);
    }

    /**
     * Checks the status of the popup display
     */
    @Override
    public void update() {

        // Checks if popup display has been activated.
        if (!isDisplayed) {
            return;
        }

        // Press 'E' to close the popup display.
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            popupDisplay.setVisible(false);
            isDisplayed = false;
        }

        // Functionality and Interaction Handling
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (spriteData == null) {
                popupDisplay.setVisible(false);
                isDisplayed = false;
                return;
            }
            LevelInteractionHandler.handleInteraction(spriteData);

            popupDisplay.setVisible(false);
            isDisplayed = false;

            // Functionality and Interaction Handling
        }
    }

    /**
     * Activates the popup display when the player collides with the entity.
     * @param entity is the entity that the player collides with to activate the popup display.
     * @param other is the player.
     */
    private void onCollisionStart(Fixture entity, Fixture other) {
        Entity otherEntity = ((BodyUserData) other.getBody().getUserData()).entity;
        // Checks 'other' is the player by checking a component unique to the player.
        if (otherEntity.getComponent(PlayerActions.class) != null) {
            popupDisplay.setVisible(true);
            isDisplayed = true;
        }
    }

    /**
     * Gets the level's title.
     * @return level's title
     */
    private String setLevelTitle(MenuSpriteData spriteData){
        if (spriteData == null) {
            return "Title";
        }
        return spriteData.getName();
    }

    /**
     *  Gets the level's title.
     * @return level's title
     */
    private String setLevelDescription(MenuSpriteData spriteData) {
        if (spriteData == null) {
            return "Description\nDescription\nPress 'E' to enter level.";
        }
        return spriteData.getDescription();
    }

    /**
     * Frees the memory.
     */
    @Override
    public void dispose() {
        if (popupDisplay != null) {
            popupDisplay.remove();
        }

        super.dispose();
    }

    /**
     * Draws a sprite batch.
     * @param batch Batch to render to.
     */
    @Override
    protected void draw(SpriteBatch batch) {

    }
}
