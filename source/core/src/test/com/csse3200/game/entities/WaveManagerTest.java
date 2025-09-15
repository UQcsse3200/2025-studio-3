package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.entities.configs.LevelsConfig;
import com.csse3200.game.entities.factories.WaveFactory;
import org.junit.jupiter.api.Test;

class WaveManagerTest {

  @Test
  void initialiseNewWaveIncrementTest() {
    LevelsConfig levelsConfig = new LevelsConfig();
    EntitySpawn spawner = new EntitySpawn(new WaveFactory(levelsConfig), 2);
    WaveManager wm = new WaveManager(spawner);
    int before = WaveManager.getCurrentWave();
    wm.initialiseNewWave();
    assertEquals(before + 1, WaveManager.getCurrentWave());
  }

  @Test
  void getLaneTest() {
    LevelsConfig levelsConfig = new LevelsConfig();
    EntitySpawn spawner = new EntitySpawn(new WaveFactory(levelsConfig), 2);
    WaveManager wm = new WaveManager(spawner);
    wm.initialiseNewWave();
    int lane = wm.getLane();
    assertTrue(lane >= 0 && lane < 5);
  }
}
