package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.csse3200.game.entities.configs.WaveConfigs;

class WaveFactoryTest {

  @Test
  void defaultWave1Test() {
    // Provide configs directly to avoid LibGDX file IO during unit tests
    WaveConfigs configs = new WaveConfigs();
    // Set values directly on wave1 inside the container
    configs.getWave1().waveWeight = 10;
    configs.getWave1().minZombiesSpawn = 5;
    configs.getWave1().expGained = 10;
    WaveFactory wf = new WaveFactory(configs);
    assertEquals(10, wf.getWaveWeight());
    assertEquals(5, wf.getMinZombiesSpawn());
    assertEquals(10, wf.getExpGained());
  }
}


