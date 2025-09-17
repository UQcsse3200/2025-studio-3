package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.entities.configs.EnemySpawnConfig;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
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
          public Map<String, EnemySpawnConfig> getEnemyConfigs() {
            Map<String, EnemySpawnConfig> configs = new HashMap<>();
            configs.put("standard", new EnemySpawnConfig(2, 1.0f));
            configs.put("fast", new EnemySpawnConfig(2, 0.0f));
            configs.put("tanky", new EnemySpawnConfig(2, 0.0f));
            configs.put("bungee", new EnemySpawnConfig(2, 0.0f));
            return configs;
          }
        };

    EntitySpawn spawner = new EntitySpawn(mockWaveManager, 2); // robotWeight = 2
    // Use the new method that works with the level-based system
    spawner.spawnEnemiesFromConfig();
    assertEquals(5, spawner.getSpawnCount());
  }

  @Test
  void randomTypeAll() {
    EntitySpawn spawner = new EntitySpawn(2);
    boolean s = false, f = false, t = false;
    for (int i = 0; i < 100; i++) {
      RobotType type = spawner.getRandomRobotType();
      if (RobotType.STANDARD.equals(type)) s = true;
      if (RobotType.FAST.equals(type)) f = true;
      if (RobotType.TANKY.equals(type)) t = true;
      if (s && f && t) break;
    }
    assertTrue(s && f && t, "Expected all three types to appear across trials");
  }
}
