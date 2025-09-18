package com.csse3200.game.entities.configs;

import java.util.ArrayList;
import java.util.List;

/**
 * Simplified configuration class that directly maps to the JSON structure. Eliminates the confusion
 * between LevelConfig and LevelsConfig.
 */
@SuppressWarnings("java:S1104") // Public fields are intentional for JSON deserialization
public class GameConfig {
  /** List of all levels in the game */
  public List<LevelData> levels = new ArrayList<>();

  /** Default constructor */
  public GameConfig() {
    // Initialize with default values
  }

  /**
   * Gets a specific level by number.
   *
   * @param levelNumber the level number
   * @return the level data, or null if not found
   */
  public LevelData getLevel(int levelNumber) {
    for (LevelData level : levels) {
      if (level.levelNumber == levelNumber) {
        return level;
      }
    }
    return null;
  }

  /**
   * Gets a wave from a specific level and wave index.
   *
   * @param levelNumber the level number
   * @param waveIndex the wave index (0-based)
   * @return the wave config, or a default one if not found
   */
  public BaseWaveConfig getWave(int levelNumber, int waveIndex) {
    LevelData level = getLevel(levelNumber);
    if (level != null && waveIndex >= 0 && waveIndex < level.waves.size()) {
      return level.waves.get(waveIndex);
    }
    return new BaseWaveConfig();
  }

  /**
   * Gets the number of waves for a specific level.
   *
   * @param levelNumber the level number
   * @return the number of waves for that level, or 0 if level not found
   */
  public int getWaveCountForLevel(int levelNumber) {
    LevelData level = getLevel(levelNumber);
    return level != null ? level.waves.size() : 0;
  }

  /** Represents a single level with its waves. */
  @SuppressWarnings("java:S1104") // Public fields are intentional for JSON deserialization
  public static class LevelData {
    /** The level number/identifier */
    public int levelNumber = 1;

    /** Array of waves for this level */
    public List<BaseWaveConfig> waves = new ArrayList<>();

    /** Default constructor */
    public LevelData() {
      // Initialize with default values
    }

    /**
     * Constructor with level number.
     *
     * @param levelNumber the level number
     */
    public LevelData(int levelNumber) {
      this.levelNumber = levelNumber;
    }

    /**
     * Gets a wave by index (0-based).
     *
     * @param waveIndex the wave index
     * @return the wave config, or a default one if index is out of bounds
     */
    public BaseWaveConfig getWave(int waveIndex) {
      if (waveIndex >= 0 && waveIndex < waves.size()) {
        return waves.get(waveIndex);
      }
      return new BaseWaveConfig();
    }

    /**
     * Gets the number of waves in this level.
     *
     * @return the number of waves
     */
    public int getWaveCount() {
      return waves.size();
    }
  }
}
