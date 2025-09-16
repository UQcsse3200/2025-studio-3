package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.EnemySpawnConfig;
import java.util.*;

/** Computes how many enemies to spawn in the current wave and selects a type per spawn request. */
public class EntitySpawn {
  private WaveManager waveManager;

  private final int robotWeight;
  private int spawnCount = 0;
  private final java.util.Random random = new java.util.Random();
  private final Deque<String> spawnQueue = new ArrayDeque<>();

  /** Creates a new instance with a default per-enemy weight cost. */
  public EntitySpawn() {
    this(2);
  }

  /**
   * Creates a new instance with a specified per-enemy weight cost. Note: WaveManager must be set
   * separately using setWaveManager().
   *
   * @param robotWeight weight cost of a single enemy used to derive spawn counts
   */
  public EntitySpawn(int robotWeight) {
    this.robotWeight = robotWeight;
  }

  /**
   * Test-only constructor allowing injection of a prebuilt WaveManager to avoid LibGDX file IO in
   * unit tests.
   *
   * @param waveManager manager providing wave configuration
   * @param robotWeight weight cost per enemy
   */
  public EntitySpawn(WaveManager waveManager, int robotWeight) {
    this.waveManager = waveManager;
    this.robotWeight = robotWeight;
  }

  /**
   * Sets the WaveManager instance for this EntitySpawn.
   *
   * @param waveManager the WaveManager instance to use
   */
  public void setWaveManager(WaveManager waveManager) {
    this.waveManager = waveManager;
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
    if (waveManager == null) {
      spawnCount = 0;
      return;
    }
    int waveWeight = waveManager.getWaveWeight();
    int minCount = waveManager.getMinZombiesSpawn();

    if (robotWeight <= 0 || waveWeight <= 0) {
      spawnCount = 0;
      return;
    }

    // If not divisible, add 1 to the waveWeight
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

  /**
   * @return next enemy type from the spawn queue, or "standard" if empty.
   */
  public String getNextRobotType() {
    return spawnQueue.isEmpty() ? "standard" : spawnQueue.pollFirst();
  }

  /** Deterministic expansion: build a fixed pattern from 'chance' weights. */
  private List<String> buildPattern(Map<String, EnemySpawnConfig> configs) {
    List<String> pattern = new ArrayList<>();
    // Sort keys so order is stable (alphabetical)
    List<String> types = new ArrayList<>(configs.keySet());
    Collections.sort(types);
    for (String type : types) {
      int repeat = Math.max(1, Math.round(configs.get(type).chance));
      for (int i = 0; i < repeat; i++) {
        pattern.add(type);
      }
    }
    return pattern;
  }

  /**
   * Builds a spawn queue for the current wave using the new system: - Each wave has a budget
   * (waveWeight). - Each enemy type consumes budget (cost). - Enemies are picked randomly, weighted
   * by 'chance'. - Loop continues until budget runs out.
   */
  public void spawnEnemiesFromConfig() {
    if (waveManager == null) {
      spawnCount = 0;
      return;
    }
    int budget = waveManager.getWaveWeight();
    Map<String, EnemySpawnConfig> configs = waveManager.getEnemyConfigs();

    spawnQueue.clear();
    spawnCount = 0;

    if (configs == null || configs.isEmpty() || budget <= 0) return;

    List<String> pattern = buildPattern(configs);

    int i = 0;
    while (budget > 0) {
      String enemy = pattern.get(i % pattern.size());
      EnemySpawnConfig cfg = configs.get(enemy);
      if (cfg.cost > budget) break;
      spawnQueue.add(enemy);
      spawnCount++;
      budget -= cfg.cost;
      i++;
    }
  }
}
