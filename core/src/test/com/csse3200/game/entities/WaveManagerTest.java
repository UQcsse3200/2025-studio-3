package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.services.WaveService;
import org.junit.jupiter.api.Test;

class WaveServiceTest {

  @Test
  void initialiseNewWaveIncrementTest() {
    EntitySpawn spawner = new EntitySpawn(2);
    WaveService wm = new WaveService(spawner);
    int before = wm.getCurrentWave();
    wm.initialiseNewWave();
    assertEquals(before + 1, wm.getCurrentWave());
  }

  @Test
  void getLaneTest() {
    EntitySpawn spawner = new EntitySpawn(2);
    WaveService wm = new WaveService(spawner);
    wm.initialiseNewWave();
    int lane = wm.getLane();
    assertTrue(lane >= 0 && lane < 5);
  }
}
