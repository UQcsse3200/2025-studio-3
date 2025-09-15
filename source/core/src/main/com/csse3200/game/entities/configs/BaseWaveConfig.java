package com.csse3200.game.entities.configs;

/** Defines a basic set of properties stored in wave config files to be loaded by Wave Factories. */
@SuppressWarnings("java:S1104") // Public fields are intentional for JSON deserialization
public class BaseWaveConfig {
  /** Creates a new BaseWaveConfig with default values. */
  public BaseWaveConfig() {
    // Default constructor with default field values
  }

  /**
   * Constructor with parameters
   *
   * @param waveWeight the weight budget for this wave
   * @param expGained experience gained for completing this wave
   * @param minZombiesSpawn minimum number of enemies to spawn
   */
  public BaseWaveConfig(int waveWeight, int expGained, int minZombiesSpawn) {
    this.waveWeight = waveWeight;
    this.expGained = expGained;
    this.minZombiesSpawn = minZombiesSpawn;
  }

  /** Weight/difficulty of this wave */
  public int waveWeight = 10;

  /** Experience points gained from this wave */
  public int expGained = 10;

  /** Minimum number of zombies to spawn in this wave */
  public int minZombiesSpawn = 10;

  /** Enemy definitions - individual fields for JSON compatibility */
  public EnemySpawnConfig standard = new EnemySpawnConfig();

  public EnemySpawnConfig fast = new EnemySpawnConfig();
  public EnemySpawnConfig tanky = new EnemySpawnConfig();
  public EnemySpawnConfig bungee = new EnemySpawnConfig();
}
