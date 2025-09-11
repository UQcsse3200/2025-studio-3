package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;

/** Computes how many enemies to spawn in the current wave and selects a type per spawn request. */
public class EntitySpawn {
  private final WaveFactory waveFactory;

  private final int robotWeight;
  private int spawnCount = 0;
  private final java.util.Random random = new java.util.Random();

  /** Creates a new instance with a default per-enemy weight cost. */
  public EntitySpawn() {
    this(2);
  }

  /**
   * Creates a new instance with a specified per-enemy weight cost.
   *
   * @param robotWeight weight cost of a single enemy used to derive spawn counts
   */
  public EntitySpawn(int robotWeight) {
    this(new WaveFactory(), robotWeight);
  }

  /**
   * Test-only constructor allowing injection of a prebuilt WaveFactory to avoid LibGDX file IO in
   * unit tests.
   *
   * @param waveFactory factory providing wave configuration
   * @param robotWeight weight cost per enemy
   */
  public EntitySpawn(WaveFactory waveFactory, int robotWeight) {
    this.waveFactory = waveFactory;
    this.robotWeight = robotWeight;
  }

  /**
   * @return computed spawn count for this wave.
   */
  public int getSpawnCount() {
    return spawnCount;
  }

  /**
   * Computes spawn count from the current wave's weight budget and minimum requirement configured
   * in JSON.
   */
  public void spawnEnemies() {
    int waveWeight = waveFactory.getWaveWeight();
    int minCount = waveFactory.getMinZombiesSpawn();

    if (robotWeight <= 0 || waveWeight <= 0) {
      spawnCount = 0;
      return;
    }

    // If not divisible, add 1 toe the waveWeight
    if (waveWeight % robotWeight != 0) {
      waveWeight += 1;
    }

    // If the exact robotSpawn is still below the minimum, we can also bump robotSpawn up
    int robotSpawn = waveWeight / robotWeight;
    if (robotSpawn < minCount) {
      robotSpawn = minCount;
    }
    spawnCount = robotSpawn;
  }

  /**
   * @return uniformly random enemy type among "standard", "fast", and "tanky".
   */
  public String getRandomRobotType() {
    int r = random.nextInt(3);
    return switch (r) {
      case 0 -> "standard";
      case 1 -> "fast";
      default -> "tanky";
    };
  }
}
