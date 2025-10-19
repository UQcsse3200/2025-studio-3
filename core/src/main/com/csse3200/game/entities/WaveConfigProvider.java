package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.BaseSpawnConfig;
import java.util.Map;

/**
 * Interface for providing wave configuration data to avoid circular dependencies. This allows
 * EntitySpawn to depend on an abstraction rather than WaveService directly.
 */
public interface WaveConfigProvider {
  /**
   * Gets the configured weight/budget for the current wave.
   *
   * @return the wave weight budget
   */
  int getWaveWeight();

  /**
   * Gets the minimum number of zombies to spawn for the current wave.
   *
   * @return the minimum zombie spawn count
   */
  int getMinZombiesSpawn();

  /**
   * Gets the enemy configuration map for the current wave.
   *
   * @return map of enemy type to spawn configuration
   */
  Map<String, BaseSpawnConfig> getEnemyConfigs();

  int getTotalWaves();

  int getWaveWeight(int waveIndex);

  int getMinZombiesSpawn(int waveIndex);

  Map<String, BaseSpawnConfig> getEnemyConfigs(int waveIndex);
}
