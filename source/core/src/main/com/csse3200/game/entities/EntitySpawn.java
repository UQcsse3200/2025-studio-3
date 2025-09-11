package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;

public class EntitySpawn {
  private final WaveFactory waveFactory;

  // For now, only one robot type exists.
  // Set this to the weight defined for that robot.
  private final int robotWeight;
  private int spawnCount = 0;
  private final java.util.Random random = new java.util.Random();

  public EntitySpawn() {
    this(2);
  }

  public EntitySpawn(int robotWeight) {
    this.waveFactory = new WaveFactory();
    this.robotWeight = robotWeight;
  }

  /** Returns the number of enemies to spawn this wave. */
  public int getSpawnCount() {
    return spawnCount;
  }

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

  /** Randomly selects an enemy type for this spawn. */
  public String getRandomRobotType() {
    int r = random.nextInt(3);
    return switch (r) {
      case 0 -> "standard";
      case 1 -> "fast";
      default -> "tanky";
    };
  }
}
