package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseLevelConfig {
  private int levelNumber;
  private String mapFile;
  private int rows;
  private int cols;
  private List<BaseWaveConfig> waves;
  private String nextLevel;
  private boolean isSlotMachine;

  /**
   * Get the is slot machine
   *
   * @return the is slot machine
   */
  public boolean isSlotMachine() {
    return isSlotMachine;
  }

  /** Default constructor */
  public BaseLevelConfig() {
    // Default constructor
  }

  /**
   * Get the level number
   *
   * @return the level number
   */
  public int getLevelNumber() {
    return levelNumber;
  }

  /**
   * Get the map file
   *
   * @return the map file
   */
  public String getMapFile() {
    return mapFile;
  }

  /**
   * Get the number of rows
   *
   * @return the number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Get the number of columns
   *
   * @return the number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get the waves
   *
   * @return the waves
   */
  public List<BaseWaveConfig> getWaves() {
    return waves;
  }

  /**
   * Get the next level
   *
   * @return the next level
   */
  public String getNextLevel() {
    return nextLevel;
  }

  /** DeserializedLevelConfig is a wrapper class for the BaseLevelConfig class. */
  public static class DeserializedLevelConfig {
    private HashMap<String, BaseLevelConfig> config;

    /** Creates a new DeserializedLevelConfig. */
    public DeserializedLevelConfig() {
      this.config = new HashMap<>();
    }

    /**
     * Set the config
     *
     * @param config the config
     */
    public void setConfig(Map<String, BaseLevelConfig> config) {
      this.config = config != null ? new HashMap<>(config) : new HashMap<>();
    }

    /**
     * Get the config
     *
     * @return the config
     */
    public Map<String, BaseLevelConfig> getConfig() {
      return config;
    }
  }
}
