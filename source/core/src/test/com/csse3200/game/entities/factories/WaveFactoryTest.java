package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.WaveConfigs;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class WaveFactoryTest {

  @Test
  void defaultWave1Test() throws Exception {
    Field f = WaveManager.class.getDeclaredField("currentWave");
    f.setAccessible(true);
    f.setInt(null, 0);
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
