package com.csse3200.game.entities.configs;

/** Defines a basic set of properties stored in wave config files to be loaded by Wave Factories. */
@SuppressWarnings("java:S1104") // Public fields are intentional for JSON deserialization
public class BaseWaveConfig {
  /** Creates a new BaseWaveConfig with default values. */
  public BaseWaveConfig() {
    // Default constructor with default field values
  }

  /** Weight/difficulty of this wave */
  public int waveWeight = 10;

  /** Experience points gained from this wave */
  public int expGained = 10;

  /** Minimum number of zombies to spawn in this wave */
  public int minZombiesSpawn = 10;
}
