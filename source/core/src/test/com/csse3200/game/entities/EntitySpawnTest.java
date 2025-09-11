package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.entities.configs.WaveConfigs;
import com.csse3200.game.entities.factories.WaveFactory;
import org.junit.jupiter.api.Test;

class EntitySpawnTest {

  @Test
  void spawnEnemiesCheckWeightAndMinimum() {
    WaveConfigs configs = new WaveConfigs();
    configs.getWave1().waveWeight = 10;
    configs.getWave1().minZombiesSpawn = 5;
    configs.getWave1().expGained = 10;
    EntitySpawn spawner = new EntitySpawn(new WaveFactory(configs), 2); // robotWeight = 2
    // Default wave1: weight=10, min=5 -> 10/2=5 -> count=5
    spawner.spawnEnemies();
    assertEquals(5, spawner.getSpawnCount());
  }

  @Test
  void randomTypeAll() {
    WaveConfigs configs = new WaveConfigs();
    EntitySpawn spawner = new EntitySpawn(new WaveFactory(configs), 2);
    boolean s = false, f = false, t = false;
    for (int i = 0; i < 100; i++) {
      String type = spawner.getRandomRobotType();
      if ("standard".equals(type)) s = true;
      if ("fast".equals(type)) f = true;
      if ("tanky".equals(type)) t = true;
      if (s && f && t) break;
    }
    assertTrue(s && f && t, "Expected all three types to appear across trials");
  }
}
