package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.BaseSpawnConfig;
import java.util.*;

/** Computes how many enemies to spawn in the current wave and selects a type per spawn request. */
public class EntitySpawn {
  private WaveConfigProvider waveConfigProvider;

  private final int robotWeight;
  private int spawnCount = 0;
  private final Deque<String> spawnQueue = new ArrayDeque<>();

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
  public String getNextRobotType() {
    return spawnQueue.isEmpty() ? "standard" : spawnQueue.pollFirst();
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
   * Builds a spawn queue for the current wave using the new system:
   * - Each wave has a budget (waveWeight).
   * - Each enemy type consumes budget (cost).
   * - Enemies are picked randomly, weighted by 'chance'.
   * - Loop continues until budget runs out. The method uses a weight-accumulation system to
   * distribute spawn probabilities based on each enemy type’s defined spawn chance and cost.
   * The algorithm repeats until the wave budget is depleted or no affordable enemies remain.
   */
  public void spawnEnemiesFromConfig() {
    if (waveConfigProvider == null) {
      spawnCount = 0;
      spawnQueue.clear();
      return;
    }

    Map<String, BaseSpawnConfig> configs = waveConfigProvider.getEnemyConfigs();
    int waveWeight = waveConfigProvider.getWaveWeight();
    int minCount = waveConfigProvider.getMinZombiesSpawn();

    // Reset the spawn queue and count for the new wave
    spawnQueue.clear();
    spawnCount = 0;

    // If no enemy configurations exist, skip spawning
    if (configs == null || configs.isEmpty()) return;

    // Build ordered entries (stable by enemy name)
    List<TypeEntry> entries = new ArrayList<>();
    for (Map.Entry<String, BaseSpawnConfig> e : configs.entrySet()) {
      BaseSpawnConfig cfg = e.getValue();
      if (cfg == null) continue;

      int cost = cfg.getCost();
      if (cost <= 0) continue; // skip invalid/zero-cost to avoid infinite loops

      double w = Math.max(0d, cfg.getChance());
      entries.add(new TypeEntry(e.getKey(), cost, w));
    }

    // Stop if no valid enemy types are available
    if (entries.isEmpty()) return;

    // Keep spawn order stable by sorting alphabetically by name
    entries.sort(Comparator.comparing(te -> te.name));

    // Compute the cheapest cost and total positive weight
    int cheapest = Integer.MAX_VALUE;
    double totalWeight = 0d;
    for (TypeEntry te : entries) {
      cheapest = Math.min(cheapest, te.cost);
      totalWeight += te.weight;
    }

    // Terminate if no valid costs were found
    if (cheapest == Integer.MAX_VALUE) return;

    // If all chances are zero, assign equal weights to all types
    if (totalWeight <= 0d) {
      for (TypeEntry te : entries) te.weight = 1d;
      totalWeight = entries.size();
    }

    // Adjust budget to guarantee minimum spawns
    int budget = Math.max(waveWeight, minCount * cheapest);

    // Weighted spawn selection loop
    while (budget >= cheapest) {
      // Each zombie’s accumulator (acc) increases by its weight (chance) each cycle.
      for (TypeEntry te : entries) {
        te.acc += te.weight;
      }

      // Pick affordable enemy type with the highest accumulated weight
      TypeEntry best = null;
      for (TypeEntry te : entries) {
        if (te.cost <= budget && (best == null || te.acc > best.acc)) {
          best = te;
        }
      }

      // If no remaining enemies are affordable, stop spawning
      if (best == null) break;

      // Queue the selected enemy and update counters/budget
      spawnQueue.add(best.name);
      spawnCount++;
      budget -= best.cost;

      // Normalize accumulator by subtracting total weight (keeps fairness)
      best.acc -= totalWeight;
    }
  }
}
