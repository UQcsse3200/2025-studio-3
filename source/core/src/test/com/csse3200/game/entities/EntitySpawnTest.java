package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.LevelConfig;
import com.csse3200.game.entities.configs.LevelsConfig;
import com.csse3200.game.entities.factories.WaveFactory;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntitySpawnTest {

  @Test
  void spawnEnemiesCheckWeightAndMinimum() throws Exception {
    // Set current wave to 1 (first wave)
    java.lang.reflect.Field f = WaveManager.class.getDeclaredField("currentWave");
    f.setAccessible(true);
    f.setInt(null, 1);

    // Create test level config
    LevelConfig level1 = new LevelConfig(1);
    BaseWaveConfig wave1 = new BaseWaveConfig();
    wave1.waveWeight = 10;
    wave1.minZombiesSpawn = 5;
    wave1.expGained = 10;

    // Set up enemy configs with cost 2 (so 10/2 = 5 enemies)
    wave1.standard.cost = 2;
    wave1.standard.chance = 1.0f;
    wave1.fast.cost = 2;
    wave1.fast.chance = 0.0f;
    wave1.tanky.cost = 2;
    wave1.tanky.chance = 0.0f;
    wave1.bungee.cost = 2;
    wave1.bungee.chance = 0.0f;

    level1.addWave(wave1);

    // Create levels config
    LevelsConfig levelsConfig = new LevelsConfig();
    Map<Integer, LevelConfig> levelMap = new HashMap<>();
    levelMap.put(1, level1);
    levelsConfig.setLevels(levelMap);

    EntitySpawn spawner = new EntitySpawn(new WaveFactory(levelsConfig), 2); // robotWeight = 2
    // Use the new method that works with the level-based system
    spawner.spawnEnemiesFromConfig();
    assertEquals(5, spawner.getSpawnCount());
  }

  @Test
  void randomTypeAll() {
    LevelsConfig levelsConfig = new LevelsConfig();
    EntitySpawn spawner = new EntitySpawn(new WaveFactory(levelsConfig), 2);
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
