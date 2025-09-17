package com.csse3200.game.entities.configs;

public class BaseLevelGameConfig {
  private int levelNum;
  private String mapFilePath;
  private int rows;
  private int cols;

  public BaseLevelGameConfig() {
    levelNum = 1;
    mapFilePath = "images/level-1-map-v2.png";
    rows = 5;
    cols = 10;
  }

  public int getLevelNum() {
    return levelNum;
  }

  public String getMapFilePath() {
    return mapFilePath;
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return cols;
  }
}
