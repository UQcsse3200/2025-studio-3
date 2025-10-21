package com.csse3200.game.services;

import com.csse3200.game.entities.EntitySpawn;
import com.csse3200.game.entities.WaveConfigProvider;
import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.factories.BossFactory;
import com.csse3200.game.entities.factories.RobotFactory;
import java.util.*;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaveService implements WaveConfigProvider {

  private static final Logger logger = LoggerFactory.getLogger(WaveService.class);

  private int currentWave = 0;
  private String currentLevelKey;
  private final List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
  private int enemiesToSpawn = 0;
  private int currentEnemyPos;
  private int enemiesDisposed = 0;
  private float timeSinceLastSpawn;
  private boolean waveActive = false;

  private boolean preparationPhaseActive = false;
  private static final float PREPARATION_PHASE_DURATION = 5.0f;
  private float preparationPhaseTimer = 0.0f;
  private final EntitySpawn entitySpawn;

  public interface EnemySpawnCallback {
    void spawnEnemy(int col, int row, RobotFactory.RobotType robotType);

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

  /** Creates new WaveService that creates its own WaveService */
  public WaveService() {
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn();
    this.entitySpawn.setWaveConfigProvider(this);
    this.preparationPhaseActive = false;
    this.preparationPhaseTimer = 0.0f;
    this.enemiesDisposed = 0;
    resetToInitialState();
    Collections.shuffle(laneOrder);
    logger.debug("[WaveService] Wave service created.");
  }

  public WaveService(EntitySpawn entitySpawn) {
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
   * Update function to be called by main game loop. Handles preparation phase timer and enemy
   * spawning.
   *
   * @param deltaTime time elapsed since last update in seconds
   */
  public void update(float deltaTime) {
    if (!bossSpawnQueue.isEmpty()) {
      BossFactory.BossTypes bossToSpawn = bossSpawnQueue.poll();
      if (enemySpawnCallback != null) {
        enemySpawnCallback.spawnBoss(2, bossToSpawn);
      }
    }
    if (preparationPhaseActive) {
      preparationPhaseTimer += deltaTime;
      if (preparationPhaseTimer >= PREPARATION_PHASE_DURATION) {
        startWave();
      }
      return;
    }

    if (waveActive) {
      timeSinceLastSpawn += deltaTime;
      double spawnInterval = 8.0f / Math.pow(currentWave, 1.5f);
      if (timeSinceLastSpawn >= spawnInterval) {
        spawnNextEnemy(getLane());
        timeSinceLastSpawn -= (float) spawnInterval;
      }
    }
  }

  /**
   * @return The current wave being spawned by WaveService
   */
  public int getCurrentWave() {
    return currentWave;
  }

  /**
   * Set the current wave in WaveService to the provided one
   *
   * @param wave the wave to set
   */
  public void setCurrentWave(int wave) {
    currentWave = wave;
  }

  public void debugSetCurrentWave(int wave) {
    currentWave = wave - 1;
    initialiseNewWave();
  }

  /** Tells WaveService to start spawning the next wave */
  public void initialiseNewWave() {
    // Don't start new waves if level is complete
    if (levelComplete) {
      logger.info("Level complete - no more waves will spawn");
    }

    if (Objects.equals(getCurrentLevelKey(), "levelTwo")
        && currentWave + 1 == getCurrentLevelWaveCount()) {
      logger.info("Queuing boss spawn for level 2: SCRAP_TITAN");
      bossSpawnQueue.add(BossFactory.BossTypes.SCRAP_TITAN);
      bossActive = true;
    } else if (Objects.equals(getCurrentLevelKey(), "levelFour")
        && currentWave + 1 == getCurrentLevelWaveCount()) {
      logger.info("Queuing boss spawn for level 4: SAMURAI_BOT");
      bossSpawnQueue.add(BossFactory.BossTypes.SAMURAI_BOT);
      bossActive = true;
    } else if (Objects.equals(getCurrentLevelKey(), "levelFive")
        && currentWave + 1 == getCurrentLevelWaveCount()) {
      logger.info("Queuing boss spawn for level 5: GUN_BOT");
      bossSpawnQueue.add(BossFactory.BossTypes.GUN_BOT);
      bossActive = true;
    }

    setCurrentWave(currentWave + 1);
    waveActive = false;
    preparationPhaseActive = true;
    preparationPhaseTimer = 0.0f;
    currentEnemyPos = 0;
    enemiesDisposed = 0;
    int maxLanes = Math.min(currentWave + 1, 5);

    if (!bossActive) {
      entitySpawn.spawnEnemiesFromConfig();
      enemiesToSpawn = entitySpawn.getSpawnCount();
    } else {
      enemiesToSpawn = 0;
    }

    waveLaneSequence = new ArrayList<>(laneOrder.subList(0, maxLanes));
    Collections.shuffle(waveLaneSequence);
    waveLanePointer = 0;
    if (waveEventListener != null) {
      waveEventListener.onPreparationPhaseStarted(currentWave);
      waveEventListener.onWaveChanged(currentWave);
    }
  }

  public void setEnemySpawnCallback(EnemySpawnCallback callback) {
    this.enemySpawnCallback = callback;
  }

  public void setWaveEventListener(WaveEventListener listener) {
    this.waveEventListener = listener;
  }

  /** Method to be called when an enemy dies, to track wave end conditions */
  public void onEnemyDispose() {
    enemiesDisposed++;
    logger.debug(
        "Enemy disposed. Count: {}/{} (spawned: {})",
        enemiesDisposed,
        enemiesToSpawn,
        currentEnemyPos);

    if (enemiesDisposed >= enemiesToSpawn && currentEnemyPos >= enemiesToSpawn && waveActive) {
      logger.info("Wave {} completed! All enemies spawned and disposed.", currentWave);
      endWave();
      int maxWaves = getCurrentLevelWaveCount();
      if (currentWave >= maxWaves && !bossActive) {
        logger.info("All waves completed for level {}! Level complete!", currentLevelKey);
        levelComplete = true;
        waveActive = false;
      }
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
    }

    endWave();
  }

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

  public boolean isPreparationPhaseActive() {
    return preparationPhaseActive;
  }

  public float getPreparationPhaseRemainingTime() {
    if (!preparationPhaseActive) {
      return 0.0f;
    }
    return Math.max(0.0f, PREPARATION_PHASE_DURATION - preparationPhaseTimer);
  }

  public float getPreparationPhaseDuration() {
    return PREPARATION_PHASE_DURATION;
  }

  public int getEnemiesDisposed() {
    return enemiesDisposed;
  }

  public int getEnemiesSpawned() {
    return currentEnemyPos;
  }

  /**
   * @return true if all waves for the current level have been completed
   */
  public boolean isLevelComplete() {
    return levelComplete;
  }

  /**
   * Resets the level state for starting a new level. This should be called when switching to a
   * different level.
   */
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
    logger.info("WaveService reset to initial state - ready for new game");
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

  public void spawnNextEnemy(int laneNumber) {
    if (currentEnemyPos >= enemiesToSpawn) {
      return;
    }
    if (enemySpawnCallback == null) {
      logger.warn("No enemy spawn callback set - cannot spawn enemy");
      return;
    }
    RobotFactory.RobotType robotType = entitySpawn.getNextRobotType();
    enemySpawnCallback.spawnEnemy(9, laneNumber, robotType);
    currentEnemyPos++;
  }

  /**
   * A function to allow debug commands to spawn a specific enemy type Unlike the regular spawnEnemy
   * function, this does not update the position in the wave
   *
   * @param laneNumber The lane to spawn the enemy in
   * @param robotType The robot type to spawn
   */
  public void spawnEnemyDebug(int laneNumber, RobotFactory.RobotType robotType) {
    if (enemySpawnCallback == null) {
      logger.warn("No enemy spawn callback set - cannot spawn {}", robotType.get());
      return;
    }

    enemySpawnCallback.spawnEnemy(9, laneNumber, robotType);
  }

  public int getWaveCountForLevel(String levelKey) {
    BaseLevelConfig config = ServiceLocator.getConfigService().getLevelConfig(levelKey);
    return config != null ? config.getWaves().size() : 0;
  }

  public int getCurrentLevelWaveCount() {
    return levelConfig != null ? levelConfig.getWaves().size() : 0;
  }

  /**
   * Returns the configured weight/budget for the current wave.
   *
   * @return the configured weight/budget for the current wave
   */
  public int getWaveWeight() {
    BaseWaveConfig waveConfig = getCurrentWaveConfig();
    return waveConfig != null ? waveConfig.getWaveWeight() : 20; // Default fallback
  }

  /**
   * Returns the minimum number of enemies to spawn for the current wave.
   *
   * @return the minimum number of enemies to spawn for the current wave
   */
  public int getMinZombiesSpawn() {
    BaseWaveConfig waveConfig = getCurrentWaveConfig();
    return waveConfig != null ? waveConfig.getMinZombiesSpawn() : 5; // Default fallback
  }

  /**
   * Returns the enemy spawn attributes (cost + chance) for the current wave.
   *
   * @return the enemy spawn attributes (cost + chance) for the current wave.
   */
  public Map<String, BaseSpawnConfig> getEnemyConfigs() {
    BaseWaveConfig wave = getCurrentWaveConfig();

    if (wave == null || wave.getSpawnConfigs() == null) {
      return new java.util.HashMap<>();
    }

    return wave.getSpawnConfigs();
  }

  @Override
  public int getTotalWaves() {
    if (levelConfig != null && levelConfig.getWaves() != null) {
      return levelConfig.getWaves().size();
    }
    return 0;
  }

  @Override
  public int getWaveWeight(int waveIndex) {
    if (levelConfig != null
        && levelConfig.getWaves() != null
        && waveIndex >= 0
        && waveIndex < levelConfig.getWaves().size()) {
      return levelConfig.getWaves().get(waveIndex).getWaveWeight();
    }
    // Fallback default
    return 20;
  }

  @Override
  public int getMinZombiesSpawn(int waveIndex) {
    if (levelConfig != null
        && levelConfig.getWaves() != null
        && waveIndex >= 0
        && waveIndex < levelConfig.getWaves().size()) {
      return levelConfig.getWaves().get(waveIndex).getMinZombiesSpawn();
    }
    // Fallback default
    return 5;
  }

  @Override
  public Map<String, BaseSpawnConfig> getEnemyConfigs(int waveIndex) {
    if (levelConfig != null
        && levelConfig.getWaves() != null
        && waveIndex >= 0
        && waveIndex < levelConfig.getWaves().size()) {
      Map<String, BaseSpawnConfig> configs =
          levelConfig.getWaves().get(waveIndex).getSpawnConfigs();
      if (configs != null) {
        return configs;
      }
    }
    // Safe empty map if missing
    return new HashMap<>();
  }

  private BaseWaveConfig getCurrentWaveConfig() {
    int waveNumber = getCurrentWave();
    int waveIndex = waveNumber - 1; // Convert to 0-based index

    if (levelConfig != null
        && levelConfig.getWaves() != null
        && waveIndex >= 0
        && waveIndex < levelConfig.getWaves().size()) {
      return levelConfig.getWaves().get(waveIndex);
    }

    return new BaseWaveConfig();
  }
}
