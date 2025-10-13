package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import java.util.*;

/** Computes how many enemies to spawn in the current wave and selects a type per spawn request. */
public class EntitySpawn {
  private WaveConfigProvider waveConfigProvider;

  private final int robotWeight;
  private int spawnCount = 0;
  private final Deque<RobotType> spawnQueue = new ArrayDeque<>();

  /** Creates a new instance with a default per-enemy weight cost. */
  public EntitySpawn() {
    this(2);
  }

  /**
   * Creates a new instance with a specified per-enemy weight cost. Note: WaveService must be set
   * separately using setWaveService().
   *
   * @param robotWeight weight cost of a single enemy used to derive spawn counts
   */
  public EntitySpawn(int robotWeight) {
    this.robotWeight = robotWeight;
  }

  /**
   * Test-only constructor allowing injection of a prebuilt WaveConfigProvider to avoid LibGDX file
   * IO in unit tests.
   *
   * @param waveConfigProvider provider of wave configuration
   * @param robotWeight weight cost per enemy
   */
  public EntitySpawn(WaveConfigProvider waveConfigProvider, int robotWeight) {
    this.waveConfigProvider = waveConfigProvider;
    this.robotWeight = robotWeight;
  }

  /**
   * Sets the WaveConfigProvider instance for this EntitySpawn.
   *
   * @param waveConfigProvider the WaveConfigProvider instance to use
   */
  public void setWaveConfigProvider(WaveConfigProvider waveConfigProvider) {
    this.waveConfigProvider = waveConfigProvider;
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
    if (waveConfigProvider == null) {
      spawnCount = 0;
      return;
    }
    int waveWeight = waveConfigProvider.getWaveWeight();
    int minCount = waveConfigProvider.getMinZombiesSpawn();

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
   * @return next enemy type from the spawn queue, or "standard" if empty.
   */
  public RobotType getNextRobotType() {
    return spawnQueue.isEmpty() ? RobotType.STANDARD : spawnQueue.pollFirst();
  }

  /** Deterministic expansion: build a fixed pattern from 'chance' weights. */
  private List<String> buildPattern(Map<String, BaseSpawnConfig> configs) {
    List<String> pattern = new ArrayList<>();
    // Sort keys so order is stable (alphabetical)
    List<String> types = new ArrayList<>(configs.keySet());
    Collections.sort(types);
    for (String type : types) {
      int repeat = Math.max(1, Math.round(configs.get(type).getChance()));
      for (int i = 0; i < repeat; i++) {
        pattern.add(type);
      }
    }
    return pattern;
  }

  /**
   * Represents an enemy type's spawn configuration entry used for deterministic weighted selection.
   * Used only by EntitySpawn's weighted round-robin algorithm.
   */
  private static final class TypeEntry {
    final String name;
    final int cost;
    double weight; // chance
    double acc; // accumulator for smooth weighted RR

    TypeEntry(String name, int cost, double weight) {
      this.name = name;
      this.cost = cost;
      this.weight = weight;
      this.acc = 0d;
    }
  }

  /**
   * Builds a spawn queue for the current wave using the new system: - Each wave has a budget
   * (waveWeight). - Each enemy type consumes budget (cost). - Enemies are picked using weighted
   * fairness by 'chance'. - Loop continues until budget runs out or no affordable enemies remain.
   */
  public void spawnEnemiesFromConfig() {
    if (waveConfigProvider == null) {
      spawnCount = 0;
      spawnQueue.clear();
      return;
    }

    List<RobotType> result =
        generateSpawnList(waveConfigProvider.getWaveWeight(), waveConfigProvider.getEnemyConfigs());

    spawnQueue.clear();
    spawnQueue.addAll(result);
    spawnCount = result.size();
  }

  /**
   * Generates a weighted, fair enemy list for a wave using the algorithm from the main branch,
   * refactored into a reusable function.
   */
  private List<RobotType> generateSpawnList(int waveWeight, Map<String, BaseSpawnConfig> configs) {
    if (configs == null || configs.isEmpty() || waveWeight <= 0) {
      return Collections.emptyList();
    }

    // Build ordered entries
    List<TypeEntry> entries = new ArrayList<>();
    for (Map.Entry<String, BaseSpawnConfig> e : configs.entrySet()) {
      BaseSpawnConfig cfg = e.getValue();
      if (cfg == null) continue;

      int cost = cfg.getCost();
      if (cost <= 0) continue;

      double w = Math.max(0d, cfg.getChance());
      entries.add(new TypeEntry(e.getKey(), cost, w));
    }

    if (entries.isEmpty()) {
      return Collections.emptyList();
    }

    // Keep spawn order stable
    entries.sort(Comparator.comparing(te -> te.name));

    // Compute cheapest cost and total weight
    int cheapest = Integer.MAX_VALUE;
    double totalWeight = 0d;
    for (TypeEntry te : entries) {
      cheapest = Math.min(cheapest, te.cost);
      totalWeight += te.weight;
    }

    if (cheapest == Integer.MAX_VALUE) return Collections.emptyList();

    // Assign equal weights if all are zero
    if (totalWeight <= 0d) {
      for (TypeEntry te : entries) te.weight = 1d;
      totalWeight = entries.size();
    }

    // Enforce minimum budget if applicable
    int minCount = waveConfigProvider != null ? waveConfigProvider.getMinZombiesSpawn() : 0;
    int budget = Math.max(waveWeight, minCount * cheapest);

    List<RobotType> result = new ArrayList<>();

    // Weighted smooth-round-robin loop
    while (budget >= cheapest) {
      // Increase each accumulator by its weight
      for (TypeEntry te : entries) {
        te.acc += te.weight;
      }

      // Pick affordable enemy with highest accumulator
      TypeEntry best = null;
      for (TypeEntry te : entries) {
        if (te.cost <= budget && (best == null || te.acc > best.acc)) {
          best = te;
        }
      }

      if (best == null) break;

      result.add(RobotType.fromString(best.name));
      budget -= best.cost;

      // Normalize accumulator
      best.acc -= totalWeight;
    }

    return result;
  }

  /**
   * @return preview list for the current wave
   */
  public List<RobotType> previewEnemiesForCurrentWave() {
    if (waveConfigProvider == null) return Collections.emptyList();
    return generateSpawnList(
        waveConfigProvider.getWaveWeight(), waveConfigProvider.getEnemyConfigs());
  }

  /** Preview all waves for the level. */
  public Map<Integer, List<RobotType>> previewAllWaves() {
    if (waveConfigProvider == null) {
      return Collections.emptyMap();
    }

    int total = waveConfigProvider.getTotalWaves();
    Map<Integer, List<RobotType>> plan = new LinkedHashMap<>();

    for (int w = 0; w < total; w++) {
      int budget = waveConfigProvider.getWaveWeight(w);
      Map<String, BaseSpawnConfig> configs = waveConfigProvider.getEnemyConfigs(w);
      List<RobotType> list = generateSpawnList(budget, configs);
      plan.put(w + 1, list);
    }

    return plan;
  }
}
