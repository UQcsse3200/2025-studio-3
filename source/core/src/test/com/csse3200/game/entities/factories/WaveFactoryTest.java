package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.entities.WaveManager;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class WaveFactoryTest {

  @Test
  void defaultWave1Test() throws Exception {
    // Set current wave to 1 (first wave)
    Field f = WaveManager.class.getDeclaredField("currentWave");
    f.setAccessible(true);
    f.setInt(null, 1);

    // Create WaveFactory for level 1 (uses static config)
    WaveFactory wf = new WaveFactory(1);

    // Test that the factory returns values from the static config
    // The static config loads from level1.json which has waveWeight=20 for first wave
    assertEquals(20, wf.getWaveWeight());
    assertEquals(5, wf.getMinZombiesSpawn());
    assertEquals(10, wf.getExpGained());
  }
}
