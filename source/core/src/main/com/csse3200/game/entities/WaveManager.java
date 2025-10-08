package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.factories.BossFactory;
import com.csse3200.game.services.ServiceLocator;
import java.util.*;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the lifecycle of enemy waves and schedules spawns over time.
 *
 * <p>WaveManager is a lightweight coordinator:
 *
 * <ul>
 *   <li>Tracks the current wave number and whether a wave is active.
 *   <li>Determines spawn cadence and a fair, shuffled lane sequence.
 *   <li>Asks {@link EntitySpawn} to compute how many enemies a wave should produce and which type
 *       to spawn next.
 *   <li>Delegates the actual entity creation and placement via callback interface.
 * </ul>
 *
 * <p>This class does not construct enemies nor touch rendering; it only orchestrates when/where to
 * request a spawn.
 */
public class WaveManager implements WaveConfigProvider {
  private static final Logger logger = LoggerFactory.getLogger(WaveManager.class);

  private int currentWave = 0;
  private String currentLevelKey = "LevelOne";
  private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
  private int enemiesToSpawn = 0;
  private int currentEnemyPos;
  private int enemiesDisposed = 0;
  private float timeSinceLastSpawn;
  private boolean waveActive = false;

  private boolean preparationPhaseActive = false;
  private float preparationPhaseDuration = 10.0f;
  private float preparationPhaseTimer = 0.0f;
  private final EntitySpawn entitySpawn;

  public interface EnemySpawnCallback {
    void spawnEnemy(int col, int row, String robotType);

    void spawnBoss(int row, BossFactory.BossTypes bossType);
  }

  public interface WaveEventListener {
    void onPreparationPhaseStarted(int waveNumber);

    void onWaveChanged(int waveNumber);

    void onWaveStarted(int waveNumber);
  }

  private EnemySpawnCallback enemySpawnCallback;
  private WaveEventListener waveEventListener;

  private List<Integer> waveLaneSequence;
  private int waveLanePointer;

  private boolean levelComplete = false;
  private BaseLevelConfig levelConfig;

  private boolean bossActive = false;
  private final Queue<BossFactory.BossTypes> bossSpawnQueue = new LinkedList<>();

  public WaveManager(String levelKey) {
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn();
    this.entitySpawn.setWaveConfigProvider(this);
    this.preparationPhaseActive = false;
    this.preparationPhaseTimer = 0.0f;
    this.enemiesDisposed = 0;
    this.currentLevelKey = levelKey != null ? levelKey : "levelOne";
    resetToInitialState();
    Collections.shuffle(laneOrder);
    this.levelConfig = ServiceLocator.getConfigService().getLevelConfig(this.currentLevelKey);
    if (levelConfig == null) {
      logger.warn("Level config not found for level {}", this.currentLevelKey);
      this.levelConfig = ServiceLocator.getConfigService().getLevelConfig("levelOne");
    }
  }

  /**
   * Test-only constructor to inject a preconfigured {@link EntitySpawn}. Useful for unit tests that
   * avoid LibGDX file IO.
   *
   * @param entitySpawn spawn helper used by this manager
   */
  public WaveManager(EntitySpawn entitySpawn) {
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = entitySpawn;
    this.entitySpawn.setWaveConfigProvider(this);
    this.preparationPhaseActive = false;
    this.preparationPhaseTimer = 0.0f;
    this.enemiesDisposed = 0;

    Collections.shuffle(laneOrder);
  }

  /**
   * Advances to the next wave, resets internal state and lane sequence, and computes the number of
   * enemies to spawn for this wave. Starts with a preparation phase.
   */
  public void initialiseNewWave() {
    if (levelComplete) {
      logger.info("Level complete - no more waves will spawn");
      return;
    }

    setCurrentWave(currentWave + 1);

    if (currentWave == 1) {
      logger.info("Queuing boss spawn for wave 1: SCRAP_TITAN");
      bossSpawnQueue.add(BossFactory.BossTypes.SCRAP_TITAN);
      bossActive = true;
    } else if (currentWave == 2) {
      logger.info("Queuing boss spawn for wave 2: SAMURAI_BOT");
      bossSpawnQueue.add(BossFactory.BossTypes.SAMURAI_BOT);
      bossActive = true;
    } else if (currentWave == 3) {
      logger.info("Queuing boss spawn for wave 3: GUN_BOT");
      bossSpawnQueue.add(BossFactory.BossTypes.GUN_BOT);
      bossActive = true;
    }

    waveActive = false;
    preparationPhaseActive = true;
    preparationPhaseTimer = 0.0f;
    currentEnemyPos = 0;
    enemiesDisposed = 0;
    int maxLanes = Math.min(currentWave + 1, 5);

    entitySpawn.spawnEnemiesFromConfig();
    enemiesToSpawn = entitySpawn.getSpawnCount();
    waveLaneSequence = new ArrayList<>(laneOrder.subList(0, maxLanes));
    Collections.shuffle(waveLaneSequence);
    waveLanePointer = 0;
    if (waveEventListener != null) {
      waveEventListener.onPreparationPhaseStarted(currentWave);
      waveEventListener.onWaveChanged(currentWave);
    }
  }

  /** Ends the current wave and immediately begins the next one. */
  public void endWave() {
    waveActive = false;
    initialiseNewWave();
  }

  private void startWave() {
    waveActive = true;
    preparationPhaseActive = false;
    timeSinceLastSpawn = 0.0f;

    if (waveEventListener != null) {
      waveEventListener.onWaveStarted(currentWave);
    }
  }

  public void setEnemySpawnCallback(EnemySpawnCallback callback) {
    this.enemySpawnCallback = callback;
  }

  public void setWaveEventListener(WaveEventListener listener) {
    this.waveEventListener = listener;
  }

  public int getCurrentWave() {
    return currentWave;
  }

  private void setCurrentWave(int wave) {
    currentWave = wave;
  }

  public boolean isPreparationPhaseActive() {
    return preparationPhaseActive;
  }

  public float getPreparationPhaseRemainingTime() {
    if (!preparationPhaseActive) {
      return 0.0f;
    }
    return Math.max(0.0f, preparationPhaseDuration - preparationPhaseTimer);
  }

  public float getPreparationPhaseDuration() {
    return preparationPhaseDuration;
  }

  public void onEnemyDisposed() {
    enemiesDisposed++;
    logger.debug(
        "Enemy disposed. Count: {}/{} (spawned: {})",
        enemiesDisposed,
        enemiesToSpawn,
        currentEnemyPos);

    if (enemiesDisposed >= enemiesToSpawn
        && currentEnemyPos >= enemiesToSpawn
        && waveActive
        && !bossActive) {
      logger.info("Wave {} completed! All MINIONS spawned and disposed.", currentWave);
    }
  }

  /** Called when a boss is defeated. This immediately ends the current wave and starts the next. */
  public void onBossDefeated() {
    logger.info("Boss defeated! Wave {} is now complete.", currentWave);
    bossActive = false;

    int maxWaves = getCurrentLevelWaveCount();
    if (currentWave >= maxWaves) {
      logger.info("Final boss defeated! Level complete!");
      levelComplete = true;
      waveActive = false;
      return;
    }

    endWave();
  }

  public int getEnemiesDisposed() {
    return enemiesDisposed;
  }

  public int getEnemiesSpawned() {
    return currentEnemyPos;
  }

  public boolean isLevelComplete() {
    return levelComplete;
  }

  public void resetLevel() {
    levelComplete = false;
    setCurrentWave(0);
    enemiesDisposed = 0;
    waveActive = false;
    preparationPhaseActive = false;
    preparationPhaseTimer = 0.0f;
    currentEnemyPos = 0;
    waveLaneSequence.clear();
    waveLanePointer = 0;
    bossActive = false;
    bossSpawnQueue.clear();
    logger.info("Level reset - ready for new level");
  }

  public void resetToInitialState() {
    currentWave = 0;
    levelComplete = false;
    enemiesDisposed = 0;
    waveActive = false;
    preparationPhaseActive = false;
    preparationPhaseTimer = 0.0f;
    currentEnemyPos = 0;
    enemiesToSpawn = 0;
    timeSinceLastSpawn = 0f;
    waveLaneSequence.clear();
    waveLanePointer = 0;
    Collections.shuffle(laneOrder);
    bossActive = false;
    bossSpawnQueue.clear();
    logger.info("WaveManager reset to initial state - ready for new game");
  }

  public int getEnemiesRemaining() {
    return Math.max(0, enemiesToSpawn - enemiesDisposed);
  }

  public String getCurrentLevelKey() {
    return currentLevelKey;
  }

  public void setCurrentLevel(String levelKey) {
    this.currentLevelKey = levelKey;
    this.levelConfig = ServiceLocator.getConfigService().getLevelConfig(this.currentLevelKey);
    if (levelConfig == null) {
      logger.warn("Level config not found for level {}", this.currentLevelKey);
      this.levelConfig = ServiceLocator.getConfigService().getLevelConfig("LevelOne");
    }
    resetLevel();
    logger.info("Level set to {}", levelKey);
  }

  public void update(float deltaTime) {
    if (!bossSpawnQueue.isEmpty()) {
      BossFactory.BossTypes bossToSpawn = bossSpawnQueue.poll();
      if (enemySpawnCallback != null) {
        enemySpawnCallback.spawnBoss(2, bossToSpawn);
      }
    }

    if (preparationPhaseActive) {
      preparationPhaseTimer += deltaTime;
      if (preparationPhaseTimer >= preparationPhaseDuration) {
        startWave();
      }
      return;
    }

    if (waveActive) {
      timeSinceLastSpawn += deltaTime;
      float spawnInterval = 5.0f;
      if (timeSinceLastSpawn >= spawnInterval) {
        spawnEnemy(getLane());
        timeSinceLastSpawn -= spawnInterval;
      }
    }
  }

  public int getLane() {
    if (waveLaneSequence.isEmpty()) {
      waveLaneSequence = new ArrayList<>(List.of(0, 1, 2, 3, 4));
    }

    if (waveLanePointer >= waveLaneSequence.size()) {
      Collections.shuffle(waveLaneSequence);
      waveLanePointer = 0;
    }
    int lane = waveLaneSequence.get(waveLanePointer);
    waveLanePointer++;
    return lane;
  }

  public void spawnEnemy(int laneNumber) {
    if (currentEnemyPos >= enemiesToSpawn) {
      return;
    }
    if (enemySpawnCallback == null) {
      logger.warn("No enemy spawn callback set - cannot spawn enemy");
      return;
    }
    String robotType = entitySpawn.getNextRobotType();
    enemySpawnCallback.spawnEnemy(9, laneNumber, robotType);
    currentEnemyPos++;
  }

  public int getWaveCountForLevel(String levelKey) {
    BaseLevelConfig config = ServiceLocator.getConfigService().getLevelConfig(levelKey);
    return config != null ? config.getWaves().size() : 0;
  }

  public int getCurrentLevelWaveCount() {
    return levelConfig != null ? levelConfig.getWaves().size() : 0;
  }

  public int getWaveWeight() {
    BaseWaveConfig waveConfig = getCurrentWaveConfig();
    return waveConfig != null ? waveConfig.getWaveWeight() : 20;
  }

  public int getMinZombiesSpawn() {
    BaseWaveConfig waveConfig = getCurrentWaveConfig();
    return waveConfig != null ? waveConfig.getMinZombiesSpawn() : 5;
  }

  public Map<String, BaseSpawnConfig> getEnemyConfigs() {
    BaseWaveConfig wave = getCurrentWaveConfig();

    if (wave == null || wave.getSpawnConfigs() == null) {
      return new java.util.HashMap<>();
    }
    return wave.getSpawnConfigs();
  }

  private BaseWaveConfig getCurrentWaveConfig() {
    int waveNumber = getCurrentWave();
    int waveIndex = waveNumber - 1;

    if (levelConfig != null
        && levelConfig.getWaves() != null
        && waveIndex >= 0
        && waveIndex < levelConfig.getWaves().size()) {
      return levelConfig.getWaves().get(waveIndex);
    }
    return new BaseWaveConfig();
  }
}
