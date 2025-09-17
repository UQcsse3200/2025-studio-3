package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.EnemySpawnConfig;
import java.util.Map;

/**
 * Interface for providing wave configuration data to avoid circular dependencies. This allows
 * EntitySpawn to depend on an abstraction rather than WaveManager directly.
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
   * Gets the experience points gained for completing the current wave.
   *
   * @return the experience points
   */
  int getExpGained();

  /**
   * Gets the enemy configuration map for the current wave.
   *
   * @return map of enemy type to spawn configuration
   */
  Map<String, EnemySpawnConfig> getEnemyConfigs();
}
