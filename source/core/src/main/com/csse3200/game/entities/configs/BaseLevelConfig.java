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
   * DeserializedLevelConfig is a wrapper class for the BaseLevelConfig class.
   */
  public static class DeserializedLevelConfig {
    private Map<String, BaseLevelConfig> config;

    /**
     * Creates a new DeserializedLevelConfig.
     */
    public DeserializedLevelConfig() {
      this.config = new HashMap<>();
    }

    /**
     * Set the config
     * 
     * @param config the config
     */
    public void setConfig(Map<String, BaseLevelConfig> config) {
      this.config = new HashMap<>(config);
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
