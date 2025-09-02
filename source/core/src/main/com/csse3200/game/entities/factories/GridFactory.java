package com.csse3200.game.entities.factories;

import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;

/**
 * This factory creates the grid and the tiles in the grid.
 */
public class GridFactory {


    /**
     * Creates a tile entity
     * @param imageStatus used to determine what image to use for the tile
     * @param scale the scale for the size of the tile
     * @param x the x value for the new tile
     * @param y the y value for the new tile
     * @return the newly created tile
     */
    public static Entity createTile(int imageStatus, float scale, float x, float y) {
        String image_path = "images/olive_tile.png";
        if (imageStatus == 1) {
            image_path = "images/green_tile.png";
        }
        // need to figure out how to get min and max x and y values of the tile
        Entity tile =
                new Entity()
                        .addComponent(new TextureRenderComponent(image_path))
                        .addComponent(new TileHitboxComponent(x, y, x, y))
                        .addComponent(new TileStorageComponent());

        tile.getComponent(TextureRenderComponent.class).scaleEntity();
        tile.scaleHeight(scale);
        return tile;
    }

    private GridFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}
