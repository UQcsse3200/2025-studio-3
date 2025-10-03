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
   * Builds a spawn queue for the current wave using the new system: - Each wave has a budget
   * (waveWeight). - Each enemy type consumes budget (cost). - Enemies are picked randomly, weighted
   * by 'chance'. - Loop continues until budget runs out.
   */
  public void spawnEnemiesFromConfig() {
    if (waveConfigProvider == null) {
      spawnCount = 0;
      return;
    }

    List<String> result =
        generateSpawnList(waveConfigProvider.getWaveWeight(), waveConfigProvider.getEnemyConfigs());
    spawnQueue.clear();
    spawnQueue.addAll(result);
    spawnCount = result.size();
  }

  /**
   * @param budget
   * @param configs
   * @return
   */
  private List<String> generateSpawnList(int budget, Map<String, BaseSpawnConfig> configs) {
    if (configs == null || configs.isEmpty() || budget <= 0) {
      return Collections.emptyList();
    }

    List<String> pattern = buildPattern(configs);
    List<String> list = new ArrayList<>();

    int i = 0;
    while (budget > 0) {
      String enemy = pattern.get(i % pattern.size());
      BaseSpawnConfig cfg = configs.get(enemy);
      if (cfg.getCost() > budget) break;
      list.add(enemy);
      budget -= cfg.getCost();
      i++;
    }
    return list;
  }

  /**
   * @return
   */
  public List<String> previewEnemiesForCurrentWave() {
    if (waveConfigProvider == null) return Collections.emptyList();
    return generateSpawnList(
        waveConfigProvider.getWaveWeight(), waveConfigProvider.getEnemyConfigs());
  }

  /**
   * Preview the spawn plan for all waves in the current level. Uses the same deterministic
   * generation logic as spawnEnemiesFromConfig.
   *
   * @return a map from wave index (1-based) to list of enemy types for that wave
   */
  public Map<Integer, List<String>> previewAllWaves() {
    if (waveConfigProvider == null) {
      return Collections.emptyMap();
    }

    int total = waveConfigProvider.getTotalWaves();
    Map<Integer, List<String>> plan = new LinkedHashMap<>();

    for (int w = 0; w < total; w++) {
      int budget = waveConfigProvider.getWaveWeight(w);
      Map<String, BaseSpawnConfig> configs = waveConfigProvider.getEnemyConfigs(w);
      List<String> list = generateSpawnList(budget, configs);
      plan.put(w + 1, list); // 1-based wave index
    }

    return plan;
  }
}
