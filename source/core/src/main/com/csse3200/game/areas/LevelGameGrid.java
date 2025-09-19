package com.csse3200.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.entities.Entity;

/** Class that stores an array of tile entities. */
public class LevelGameGrid {
  private final Entity[] gridData;
  private final int cols;
  private final int rows;

  /**
   * Class constructor for LevelGameGrid.
   *
   * @param rows number of rows in the level game grid.
   * @param cols number of columns in the level game grid.
   */
  public LevelGameGrid(int rows, int cols) {
    this.cols = cols;
    this.rows = rows;
    int numTiles = rows * cols;
    this.gridData = new Entity[numTiles];
  }

  /**
   * Adds a tile to a given index.
   *
   * @param index Index of 1 dimensional array (NOT in rows in cols).
   * @param tile Tile entity that is to be stored at the index.
   */
  public void addTile(int index, Entity tile) {
    gridData[index] = tile;
  }

  /**
   * Getter method for retrieving the tile entity instance at a given (row, col).
   *
   * @param row the grid's row that is storing the tile of interest
   * @param col the grid's column that is storing the tile of interest
   * @return the tile entity found at the given (row, col) position on the grid.
   */
  public Entity getTile(int row, int col) {
    return gridData[col * cols + row];
  }

  /**
   * Getter method for retrieving the tile entity instance at a given index.
   *
   * @param index the tile to get from a linear index
   * @return the tile
   */
  public Entity getTile(int index) {
    return gridData[index];
  }

  /**
   * Sets a specified (row, col) position on the grid to a provided tile entity. setTile differs
   * from addTile as setTile determines the index using the row and col values.
   *
   * @param row the grid's row that is to be replaced with the provided tile entity.
   * @param col the grid's column that is to be replaced with the provided tile entity.
   * @param tile the tile entity that is to be placed at the (row, col) position.
   */
  public void setTile(int row, int col, Entity tile) {
    gridData[col * cols + row] = tile;
  }

  public Entity getTileFromXY(float x, float y) {
    for (Entity tile : gridData) {
      boolean status =
          tile.getComponent(TileHitboxComponent.class)
              .inTileHitbox(new GridPoint2((int) x, (int) y));
      if (status) {
        return tile;
      }
    }
    return null;
  }
}
