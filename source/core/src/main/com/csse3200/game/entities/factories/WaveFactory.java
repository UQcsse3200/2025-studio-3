package com.csse3200.game.entities.factories;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.EnemySpawnConfig;
import com.csse3200.game.entities.configs.WaveConfigs;
import com.csse3200.game.persistence.FileLoader;
import java.util.Map;

/**
 * Lightweight facade over deserialized wave configuration.
 *
 * <p>Provides read-only accessors for values used by wave logic (weight, minimum spawns,
 * experience). This class isolates config loading and selection of the current wave's properties.
 */
public class WaveFactory {
  private final WaveConfigs configs;

  /** Default constructor. */
  public WaveFactory() {
    WaveConfigs.WaveConfigWrapper wrapper =
        FileLoader.readClass(WaveConfigs.WaveConfigWrapper.class, "configs/level1.json");
    this.configs = new WaveConfigs();
    if (wrapper != null) {
      this.configs.setConfig(wrapper.getConfig());
    }
  }

  /**
   * Test-friendly constructor allowing direct config injection to avoid LibGDX file IO in unit
   * tests.
   */
  public WaveFactory(WaveConfigs configs) {
    this.configs = configs;
  }

  /**
   * Constructor with game entity for UI integration
   *
   * @param gameEntity the main game entity for triggering UI events
   */
  public WaveFactory(Entity gameEntity) {
    WaveManager.setGameEntity(gameEntity);
    WaveConfigs.WaveConfigWrapper wrapper =
        FileLoader.readClass(WaveConfigs.WaveConfigWrapper.class, "configs/level1.json");
    this.configs = new WaveConfigs();
    if (wrapper != null) {
      this.configs.setConfig(wrapper.getConfig());
    }
  }

  /**
   * @return the configured weight/budget for the current wave
   */
  public int getWaveWeight() {
    return getWave().waveWeight;
  }

  /**
   * @return the minimum number of enemies to spawn for the current wave
   */
  public int getMinZombiesSpawn() {
    return getWave().minZombiesSpawn;
  }

  /**
   * @return the experience awarded for completing the current wave
   */
  public int getExpGained() {
    return getWave().expGained;
  }

  /**
   * @return the enemy spawn attributes (cost + chance) for the current wave.
   */
  public Map<String, EnemySpawnConfig> getEnemyConfigs() {
    BaseWaveConfig wave = getWave();
    Map<String, EnemySpawnConfig> configs = new java.util.HashMap<>();
    configs.put("standard", wave.standard);
    configs.put("fast", wave.fast);
    configs.put("tanky", wave.tanky);
    configs.put("bungee", wave.bungee);
    return configs;
  }

  /**
   * Helper function to allocate the wave number to the correct index in the config file
   *
   * @return base wave config with the corresponding wave number
   */
  private BaseWaveConfig getWave() {
    return switch (WaveManager.getCurrentWave()) {
      case 1 -> configs.getWave1();
      case 2 -> configs.getWave2();
      case 3 -> configs.getWave3();
      default -> configs.getWave1();
    };
  }
}
