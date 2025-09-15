package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.LevelConfig;
import com.csse3200.game.entities.configs.LevelsConfig;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WaveFactoryTest {

  @Test
  void defaultWave1Test() throws Exception {
    // Set current wave to 1 (first wave)
    Field f = WaveManager.class.getDeclaredField("currentWave");
    f.setAccessible(true);
    f.setInt(null, 1);

    // Create test level config
    LevelConfig level1 = new LevelConfig(1);
    BaseWaveConfig wave1 = new BaseWaveConfig();
    wave1.waveWeight = 10;
    wave1.minZombiesSpawn = 5;
    wave1.expGained = 10;
    level1.addWave(wave1);

    // Create levels config
    LevelsConfig levelsConfig = new LevelsConfig();
    Map<Integer, LevelConfig> levelMap = new HashMap<>();
    levelMap.put(1, level1);
    levelsConfig.setLevels(levelMap);

    WaveFactory wf = new WaveFactory(levelsConfig);
    assertEquals(10, wf.getWaveWeight());
    assertEquals(5, wf.getMinZombiesSpawn());
    assertEquals(10, wf.getExpGained());
  }
}
