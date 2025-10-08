package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/** BaseWaveConfig is a class that represents a wave configuration. */
public class BaseWaveConfig {
  private int waveWeight;
  private int minZombiesSpawn;
  private HashMap<String, BaseSpawnConfig> spawnConfigs;

  /** Creates a new BaseWaveConfig with default values. */
  public BaseWaveConfig() {
    this.spawnConfigs = new HashMap<>();
  }

  /**
   * Get the wave weight
   *
   * @return the wave weight
   */
  public int getWaveWeight() {
    return waveWeight;
  }

  /**
   * Get the minimum number of zombies to spawn
   *
   * @return the minimum number of zombies to spawn
   */
  public int getMinZombiesSpawn() {
    return minZombiesSpawn;
  }

  /**
   * Get the spawn configurations
   *
   * @return the spawn configurations
   */
  public Map<String, BaseSpawnConfig> getSpawnConfigs() {
    return spawnConfigs;
  }

  /**
   * Get the spawn configuration for a given enemy type
   *
   * @param enemyType the enemy type
   * @return the spawn configuration for the given enemy type
   */
  public BaseSpawnConfig getSpawnConfig(String enemyType) {
    return spawnConfigs != null ? spawnConfigs.get(enemyType) : null;
  }
}
