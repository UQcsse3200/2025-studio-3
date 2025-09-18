package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WaveManagerTest {

  @Test
  void initialiseNewWaveIncrementTest() {
    EntitySpawn spawner = new EntitySpawn(2);
    WaveManager wm = new WaveManager(spawner);
    int before = wm.getCurrentWave();
    wm.initialiseNewWave();
    assertEquals(before + 1, wm.getCurrentWave());
  }

  @Test
  void getLaneTest() {
    EntitySpawn spawner = new EntitySpawn(2);
    WaveManager wm = new WaveManager(spawner);
    wm.initialiseNewWave();
    int lane = wm.getLane();
    assertTrue(lane >= 0 && lane < 5);
  }
}
