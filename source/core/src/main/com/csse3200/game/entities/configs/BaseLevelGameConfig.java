package com.csse3200.game.entities.configs;

/**
 * Container for deserialized level information. Currently stores the following information: Integer
 * representation of the level number, File path to the map's image source, Number of rows in the
 * grid, Number of columns in the grid.
 */
public class BaseLevelGameConfig {
  private int levelNum;
  private String mapFilePath;
  private int rows;
  private int cols;

  /** Creates a config with the level 1 configurations as default values. */
  public BaseLevelGameConfig() {
    levelNum = 1;
    mapFilePath = "images/level-1-map-v2.png";
    rows = 5;
    cols = 10;
  }

  /**
   * Retrieve the level number
   *
   * @return An integer representation of the level
   */
  public int getLevelNum() {
    return levelNum;
  }

  /**
   * Retrieve the file path to the map image file
   *
   * @return String representing the file path to the level's map image file
   */
  public String getMapFilePath() {
    return mapFilePath;
  }

  /**
   * @return the number of rows in the grid of the level
   */
  public int getRows() {
    return rows;
  }

  /**
   * @return the number of columns in the grid of the level
   */
  public int getCols() {
    return cols;
  }
}
