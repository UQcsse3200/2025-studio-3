package com.csse3200.game.entities.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Container for managing multiple levels, each with their own waves. */
public class LevelsConfig {
  private Map<Integer, LevelConfig> levels = new HashMap<>();

  public LevelsConfig() {
    // Initialize with default level 1
    levels.put(1, new LevelConfig(1));
  }

  /**
   * Sets the levels map.
   *
   * @param levels the levels map
   */
  public void setLevels(Map<Integer, LevelConfig> levels) {
    this.levels = new HashMap<>(levels);
  }

  /**
   * Gets the levels map.
   *
   * @return the levels map
   */
  public Map<Integer, LevelConfig> getLevels() {
    return levels;
  }

  /**
   * Gets a specific level by number.
   *
   * @param levelNumber the level number
   * @return the level config, or a default one if not found
   */
  public LevelConfig getLevel(int levelNumber) {
    return levels.getOrDefault(levelNumber, new LevelConfig(levelNumber));
  }

  /**
   * Adds or updates a level.
   *
   * @param level the level to add/update
   */
  public void addLevel(LevelConfig level) {
    levels.put(level.getLevelNumber(), level);
  }

  /**
   * Gets the number of levels.
   *
   * @return the number of levels
   */
  public int getLevelCount() {
    return levels.size();
  }

  /**
   * Gets a wave from a specific level and wave index.
   *
   * @param levelNumber the level number
   * @param waveIndex the wave index (0-based)
   * @return the wave config, or a default one if not found
   */
  public BaseWaveConfig getWave(int levelNumber, int waveIndex) {
    return getLevel(levelNumber).getWave(waveIndex);
  }

  /** LevelsConfigWrapper is a wrapper class for JSON deserialization. */
  @SuppressWarnings("java:S1104") // Public fields are intentional for JSON deserialization
  public static class LevelsConfigWrapper {
    public List<LevelConfig> levels;

    /** Creates a new LevelsConfigWrapper. */
    public LevelsConfigWrapper() {
      this.levels = new ArrayList<>();
    }

    /**
     * Sets the levels list for the levels config.
     *
     * @param levels the levels list
     */
    public void setLevels(List<LevelConfig> levels) {
      this.levels = new ArrayList<>(levels);
    }

    /**
     * Gets the levels list for the levels config.
     *
     * @return the levels list
     */
    public List<LevelConfig> getLevels() {
      return levels;
    }
  }
}
