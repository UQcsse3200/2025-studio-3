package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;

/** Class that stores an array of tile entities. */
public class LevelGameGrid {
  private Entity[] grid_data;
  private final int rows;
  private final int cols;
  private final int num_tiles;

  /**
   * Class constructor for LevelGameGrid.
   *
   * @param rows number of rows in the level game grid.
   * @param cols number of columns in the level game grid.
   */
  public LevelGameGrid(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.num_tiles = rows * cols;
    this.grid_data = new Entity[num_tiles];
  }

  /**
   * Adds a tile to a given index.
   *
   * @param index Index of 1 dimensional array (NOT in rows in cols).
   * @param tile Tile entity that is to be stored at the index.
   */
  public void addTile(int index, Entity tile) {
    grid_data[index] = tile;
  }

  /**
   * Getter method for retrieving the tile entity instance at a given (row, col).
   *
   * @param row the grid's row that is storing the tile of interest
   * @param col the grid's column that is storing the tile of interest
   * @return the tile entity found at the given (row, col) position on the grid.
   */
  public Entity getTile(int row, int col) {
    return grid_data[col * cols + row];
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
    grid_data[col * cols + row] = tile;
  }
}
