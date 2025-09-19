package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.entities.configs.BaseSpawnConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntitySpawnTest {

  @Test
  void spawnEnemiesCheckWeightAndMinimum() {
    // Create a mock WaveManager for testing
    WaveManager mockWaveManager =
        new WaveManager() {
          @Override
          public int getWaveWeight() {
            return 10;
          }

          @Override
          public int getMinZombiesSpawn() {
            return 5;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> configs = new HashMap<>();
            configs.put("standard", new BaseSpawnConfig(2, 1.0f));
            configs.put("fast", new BaseSpawnConfig(2, 0.0f));
            configs.put("tanky", new BaseSpawnConfig(2, 0.0f));
            configs.put("bungee", new BaseSpawnConfig(2, 0.0f));
            return configs;
          }
        };

    EntitySpawn spawner = new EntitySpawn(mockWaveManager, 2); // robotWeight = 2
    // Use the new method that works with the level-based system
    spawner.spawnEnemiesFromConfig();
    assertEquals(5, spawner.getSpawnCount());
  }

  // Removed random type test: spawning is now fully budget-driven via configs.
}
