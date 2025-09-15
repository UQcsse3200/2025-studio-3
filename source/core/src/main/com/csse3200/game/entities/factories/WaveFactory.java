package com.csse3200.game.entities.factories;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.EnemySpawnConfig;
import com.csse3200.game.entities.configs.LevelConfig;
import com.csse3200.game.entities.configs.LevelsConfig;
import com.csse3200.game.persistence.FileLoader;
import java.util.Map;

/**
 * Lightweight facade over deserialized wave configuration.
 *
 * <p>Provides read-only accessors for values used by wave logic (weight, minimum spawns,
 * experience). This class isolates config loading and selection of the current wave's properties.
 */
public class WaveFactory {
  private final LevelsConfig levelsConfig;
  private int currentLevel = 1;

  /** Default constructor. */
  public WaveFactory() {
    LevelsConfig.LevelsConfigWrapper wrapper =
        FileLoader.readClass(LevelsConfig.LevelsConfigWrapper.class, "configs/level3.json");

    this.levelsConfig = new LevelsConfig();
    if (wrapper != null) {
      // Convert list to map for easier access
      Map<Integer, LevelConfig> levelMap = new java.util.HashMap<>();
      for (LevelConfig level : wrapper.getLevels()) {
        levelMap.put(level.getLevelNumber(), level);
      }
      this.levelsConfig.setLevels(levelMap);

      // Set current level to the first level found in the JSON
      if (!levelMap.isEmpty()) {
        this.currentLevel = levelMap.keySet().iterator().next();
      }
    }
  }

  /**
   * Test-friendly constructor allowing direct config injection to avoid LibGDX file IO in unit
   * tests.
   */
  public WaveFactory(LevelsConfig levelsConfig) {
    this.levelsConfig = levelsConfig;
  }

  /**
   * Constructor with game entity for UI integration
   *
   * @param gameEntity the main game entity for triggering UI events
   */
  public WaveFactory(Entity gameEntity) {
    WaveManager.setGameEntity(gameEntity);
    LevelsConfig.LevelsConfigWrapper wrapper =
        FileLoader.readClass(LevelsConfig.LevelsConfigWrapper.class, "configs/level3.json");
    this.levelsConfig = new LevelsConfig();
    if (wrapper != null) {
      // Convert list to map for easier access
      Map<Integer, LevelConfig> levelMap = new java.util.HashMap<>();
      for (LevelConfig level : wrapper.getLevels()) {
        levelMap.put(level.getLevelNumber(), level);
      }
      this.levelsConfig.setLevels(levelMap);

      // Set current level to the first level found in the JSON
      if (!levelMap.isEmpty()) {
        this.currentLevel = levelMap.keySet().iterator().next();
      }
    }
  }

  /**
   * Sets the current level.
   *
   * @param level the level number
   */
  public void setCurrentLevel(int level) {
    this.currentLevel = level;
  }

  /**
   * Gets the current level.
   *
   * @return the current level number
   */
  public int getCurrentLevel() {
    return currentLevel;
  }

  /**
   * @return the configured weight/budget for the current wave
   */
  public int getWaveWeight() {
    return getWave().waveWeight;
  }

  /**
   * @return the minimum number of enemies to spawn for the current wave
   */
  public int getMinZombiesSpawn() {
    return getWave().minZombiesSpawn;
  }

  /**
   * @return the experience awarded for completing the current wave
   */
  public int getExpGained() {
    return getWave().expGained;
  }

  /**
   * @return the enemy spawn attributes (cost + chance) for the current wave.
   */
  public Map<String, EnemySpawnConfig> getEnemyConfigs() {
    BaseWaveConfig wave = getWave();
    Map<String, EnemySpawnConfig> configs = new java.util.HashMap<>();
    configs.put("standard", wave.standard);
    configs.put("fast", wave.fast);
    configs.put("tanky", wave.tanky);
    configs.put("bungee", wave.bungee);
    return configs;
  }

  /**
   * Helper function to get the current wave config based on current level and wave number.
   *
   * @return base wave config with the corresponding wave number
   */
  private BaseWaveConfig getWave() {
    int waveIndex = WaveManager.getCurrentWave() - 1; // Convert to 0-based index
    return levelsConfig.getWave(currentLevel, waveIndex);
  }
}
