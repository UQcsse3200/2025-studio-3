package com.csse3200.game.components.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * The {@code InventoryDisplay} class is a UI component responsible for
 * displaying the player's inventory bar on the game screen.
 * <p>
 * It creates a horizontal row of inventory slots using textures,
 * which are rendered at the top of the screen. Each slot is displayed
 * as an image loaded from the game's resource service.
 */
public class InventoryDisplay extends UIComponent {
    /** Table layout used to hold and align inventory slot images. */
    private Table table;

    /** File paths for the inventory slot textures. */
    private static final String[] inventoryTextures = {
        "images/inventory.png",
    };

    /**
     * Called when the component is created. Sets up the inventory display
     * by adding the inventory slot actors to the stage.
     */
    @Override
    public void create() {
        super.create();
        addActors();
    }

    /**
     * Adds the inventory slot images to the stage.
     * <p>
     * A {@link Table} is created, anchored to the top of the screen,
     * and each inventory texture is added as an {@link Image} actor.
     */
    private void addActors() {
        table = new Table();
        table.top();
        table.setFillParent(true);

        // Load inventory slot textures
        for (String texturePath : inventoryTextures) {
            Texture texture = ServiceLocator.getResourceService().getAsset(texturePath, Texture.class);
            Image slotImage = new Image(new TextureRegionDrawable(texture));
            table.add(slotImage).pad(5);
        }

        stage.addActor(table);
    }

    /**
     * Called during the render cycle. The inventory display relies on
     * the stage to render its actors, so this method does not contain
     * custom drawing logic.
     *
     * @param batch the {@link SpriteBatch} used for rendering
     */
    @Override
    public void draw(SpriteBatch batch) {
        // Drawing is handled by the stage actors
    }

    /**
     * Cleans up resources when the inventory display is removed.
     * Clears the table of slot actors to release references.
     */
    @Override
    public void dispose() {
        super.dispose();
        table.clear();
    }
}
