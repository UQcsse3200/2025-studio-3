package com.csse3200.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.components.tile.TileHitboxComponent;
import com.csse3200.game.entities.Entity;

/** Class that stores an array of tile entities. */
public class LevelGameGrid {
  private final Entity[] gridData;
  private final int cols;
  private final int rows;
  private final Entity[] occupants;

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
    this.occupants = new Entity[numTiles];
  }

  /**
   * Getter for cols
   *
   * @return the number of cols in the grid
   */
  public int getCols() {
    return cols;
  }

  /**
   * Getter for rows
   *
   * @return the number of cols in the grid
   */
  public int getRows() {
    return rows;
  }

  /**
   * Helper to convert a row and column value in the grid to the linear array index
   *
   * @param row the row
   * @param col the column
   * @return the array index
   */
  private int idx(int row, int col) {
    return row * cols + col;
  }

  // -------- OCCUPANCY API --------
  public boolean isOccupied(int row, int col) {
    return occupants[idx(row, col)] != null;
  }

  public boolean isOccupiedIndex(int index) {
    return occupants[index] != null;
  }

  public Entity getOccupant(int row, int col) {
    return occupants[idx(row, col)];
  }

  public Entity getOccupantIndex(int index) {
    return occupants[index];
  }

  /**
   * Place a unit if the tile is empty from its x, y grid position
   *
   * @param row the row for placement
   * @param col the col for placement
   * @param unit the unit to place
   * @return true if successful otherwise false
   */
  public boolean placeOccupant(int row, int col, Entity unit) {
    int i = idx(row, col);
    if (occupants[i] != null) return false;
    occupants[i] = unit;
    return true;
  }

  /**
   * Place a unit if the tile is empty from array index
   *
   * @param index the spot in array
   * @param unit the unit to place
   * @return true if successful otherwise false
   */
  public boolean placeOccupant(int index, Entity unit) {
    if (occupants[index] != null) return false;
    occupants[index] = unit;
    return true;
  }

  /**
   * Removes an occupant from the grid if it exists This is now Null pointer safe
   *
   * @param row the row for removal
   * @param col the col for removal
   * @param unit the unit to remove
   * @return true if successful, otherwise false
   */
  public boolean removeOccupantIfMatch(int row, int col, Entity unit) {
    int i = idx(row, col);
    if (occupants[i] == unit) {
      occupants[i] = null;
      return true;
    }
    return false;
  }

  /**
   * Clears a grid tile without a check from tile x, y
   *
   * @param row the row for removal
   * @param col the col for removal
   */
  public void clearOccupant(int row, int col) {
    occupants[idx(row, col)] = null;
  }

  /**
   * Clears a grid tile without a check from tile x, y
   *
   * @param index the array position for removal
   */
  public void clearOccupantIndex(int index) {
    occupants[index] = null;
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
