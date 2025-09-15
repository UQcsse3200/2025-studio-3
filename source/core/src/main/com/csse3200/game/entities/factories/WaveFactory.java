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
  private int currentLevel = 1;

  /**
   * Loads levels configuration data from JSON. The configs object is populated at class-load time.
   * If the file is missing or deserialization fails, this will be null.
   */
  private static final LevelsConfig levelsConfig = loadLevelsConfig();

  /** Default constructor */
  public WaveFactory() {
    this(1); // Load level 1 by default
  }

  /**
   * Constructor that loads a specific level from the main levels configuration file.
   *
   * @param levelNumber the level number to load (1, 2, 3, etc.)
   */
  public WaveFactory(int levelNumber) {
    this.currentLevel = levelNumber;
  }

  /**
   * Test-friendly constructor allowing direct config injection to avoid LibGDX file IO in unit
   * tests.
   */
  public WaveFactory(LevelsConfig levelsConfig) {
    // This constructor is for testing only - levelsConfig is ignored since we use static loading
  }

  /**
   * Constructor with game entity for UI integration. Loads level 1 by default.
   *
   * @param gameEntity the main game entity for triggering UI events
   */
  public WaveFactory(Entity gameEntity) {
    this(gameEntity, 1); // Load level 1 by default
  }

  /**
   * Constructor with game entity and specific level.
   *
   * @param gameEntity the main game entity for triggering UI events
   * @param levelNumber the level number to load
   */
  public WaveFactory(Entity gameEntity, int levelNumber) {
    WaveManager.setGameEntity(gameEntity);
    this.currentLevel = levelNumber;
  }

  /**
   * Sets the current level and reloads the configuration.
   *
   * @param level the level number
   */
  public void setCurrentLevel(int level) {
    this.currentLevel = level;
    // Note: This doesn't reload the config since levelsConfig is final
    // For dynamic level switching, use changeLevel() instead
  }

  /**
   * Changes to a different level. Since all levels are loaded statically, this just creates a new
   * WaveFactory instance with the specified level.
   *
   * @param levelNumber the level number to switch to
   * @return a new WaveFactory instance for the specified level
   */
  public static WaveFactory changeLevel(int levelNumber) {
    return new WaveFactory(levelNumber);
  }

  /**
   * Changes to a different level with game entity integration.
   *
   * @param gameEntity the main game entity for triggering UI events
   * @param levelNumber the level number to switch to
   * @return a new WaveFactory instance for the specified level
   */
  public static WaveFactory changeLevel(Entity gameEntity, int levelNumber) {
    return new WaveFactory(gameEntity, levelNumber);
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

    if (wave == null) {
      return new java.util.HashMap<>();
    }

    // Construct Map from individual fields for backward compatibility
    Map<String, EnemySpawnConfig> configs = new java.util.HashMap<>();
    configs.put("standard", wave.standard);
    configs.put("fast", wave.fast);
    configs.put("tanky", wave.tanky);
    configs.put("bungee", wave.bungee);

    return configs;
  }

  private static LevelsConfig loadLevelsConfig() {
    LevelsConfig.LevelsConfigWrapper wrapper =
        FileLoader.readClass(LevelsConfig.LevelsConfigWrapper.class, "configs/level1.json");

    LevelsConfig config = new LevelsConfig();
    if (wrapper != null && wrapper.getLevels() != null) {
      // Convert list to map for easier access
      Map<Integer, LevelConfig> levelMap = new java.util.HashMap<>();
      for (LevelConfig level : wrapper.getLevels()) {
        levelMap.put(level.getLevelNumber(), level);
      }
      config.setLevels(levelMap);
    }

    return config;
  }

  /**
   * Helper function to get the current wave config based on current level and wave number.
   *
   * @return base wave config with the corresponding wave number
   */
  private BaseWaveConfig getWave() {
    int currentWave = WaveManager.getCurrentWave();
    int waveIndex = currentWave - 1; // Convert to 0-based index

    return levelsConfig.getWave(currentLevel, waveIndex);
  }
}
