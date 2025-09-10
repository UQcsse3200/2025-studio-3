package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;

public class EntitySpawn {
  private final WaveFactory waveFactory;

  // For now, only one robot type exists.
  // Set this to the weight defined for that robot.
  private final int robotWeight;
  private int spawnCount = 0;

  public EntitySpawn(int wave) {
    this(wave, /* robotWeight */ 2); // TODO: replace 2 with the actual robot weight
  }

  public EntitySpawn(int wave, int robotWeight) {
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
      waveWeight = robotSpawn * robotWeight;
    }
    spawnCount = robotSpawn;
  }

  /** Returns the robot type string for a given spawn index in this wave. */
  public String getRobotTypeForIndex(int index) {
    int r = Math.floorMod(index, 6);
    switch (r) {
      case 1:
      case 4:
        return "fast";
      case 5:
        return "tanky";
      default:
        return "standard";
    }
  }
}
