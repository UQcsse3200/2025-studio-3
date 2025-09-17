package com.csse3200.game.entities.factories;

import com.csse3200.game.areas.AreaAPI;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.components.tile.TileInputComponent;
import com.csse3200.game.components.tile.TileStorageComponent;
import com.csse3200.game.entities.Entity;

/** This factory creates the grid and the tiles in the grid. */
public class GridFactory {

  /**
   * Creates a tile entity
   *
   * @param scale the scale for the size of the tile
   * @param x the x value for the new tile
   * @param y the y value for the new tile
   * @return the newly created tile
   */
  public static Entity createTile(float scale, float x, float y, AreaAPI area) {

    // need to figure out how to get min and max x and y values of the tile

    // creates the new tile entity
    Entity tile =
        new Entity()
            .addComponent(new TileHitboxComponent(x + scale, y + scale, x, y))
            .addComponent(new TileStorageComponent(area))
            .addComponent(new TileInputComponent(area));

    // scales the tile to fit in the map
    tile.scaleHeight(scale);
    return tile;
  }

  private GridFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
