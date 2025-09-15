package com.csse3200.game.entities.configs;

import java.util.ArrayList;
import java.util.List;

/** Represents a single level with its array of waves. */
@SuppressWarnings("java:S1104") // Public fields are intentional for JSON deserialization
public class LevelConfig {
  /** The level number/identifier */
  public int levelNumber = 1;

  /** Array of waves for this level */
  public List<BaseWaveConfig> waves = new ArrayList<>();

  /** Default constructor. */
  public LevelConfig() {
    // Initialize with default values
  }

  /**
   * Constructor with level number.
   *
   * @param levelNumber the level number
   */
  public LevelConfig(int levelNumber) {
    this.levelNumber = levelNumber;
  }

  /**
   * Constructor with level number and waves.
   *
   * @param levelNumber the level number
   * @param waves the waves for this level
   */
  public LevelConfig(int levelNumber, List<BaseWaveConfig> waves) {
    this.levelNumber = levelNumber;
    this.waves = new ArrayList<>(waves);
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

  /**
   * Adds a wave to this level.
   *
   * @param wave the wave to add
   */
  public void addWave(BaseWaveConfig wave) {
    waves.add(wave);
  }

  /**
   * Gets the level number.
   *
   * @return the level number
   */
  public int getLevelNumber() {
    return levelNumber;
  }

  /**
   * Sets the level number.
   *
   * @param levelNumber the level number to set
   */
  public void setLevelNumber(int levelNumber) {
    this.levelNumber = levelNumber;
  }

  /**
   * Gets the waves list.
   *
   * @return the waves list
   */
  public List<BaseWaveConfig> getWaves() {
    return waves;
  }

  /**
   * Sets the waves list.
   *
   * @param waves the waves list to set
   */
  public void setWaves(List<BaseWaveConfig> waves) {
    this.waves = waves != null ? new ArrayList<>(waves) : new ArrayList<>();
  }
}
