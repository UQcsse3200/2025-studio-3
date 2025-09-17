package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.EnemySpawnConfig;
import com.csse3200.game.entities.configs.GameConfig;
import com.csse3200.game.persistence.FileLoader;
import java.util.*;
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
public class WaveManager {
  private static final Logger logger = LoggerFactory.getLogger(WaveManager.class);

  private int currentWave = 0;
  private int currentLevel = 1;
  private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
  private int enemiesToSpawn = 0;
  private int currentEnemyPos;
  private int enemiesDisposed = 0;
  private float timeSinceLastSpawn;
  private boolean waveActive = false;

  // Preparation phase variables
  private boolean preparationPhaseActive = false;
  private float preparationPhaseDuration = 10.0f;
  private float preparationPhaseTimer = 0.0f;

  private final EntitySpawn entitySpawn;

  // Callback interface for spawning enemies
  public interface EnemySpawnCallback {
    void spawnEnemy(int col, int row, String robotType);
  }

  // Event listener interface for wave events
  public interface WaveEventListener {
    void onPreparationPhaseStarted(int waveNumber);

    void onWaveChanged(int waveNumber);

    void onWaveStarted(int waveNumber);
  }

  private EnemySpawnCallback enemySpawnCallback;
  private WaveEventListener waveEventListener;

  private List<Integer> waveLaneSequence;
  private int waveLanePointer;

  // Wave configuration management
  private static GameConfig gameConfig = null;
  private boolean levelComplete = false;

  public WaveManager() {
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn();
    this.entitySpawn.setWaveManager(this);
    this.preparationPhaseActive = false;
    this.preparationPhaseTimer = 0.0f;
    this.enemiesDisposed = 0;
    resetToInitialState();

    Collections.shuffle(laneOrder);
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
    this.entitySpawn.setWaveManager(this);
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
    // Don't start new waves if level is complete
    if (levelComplete) {
      logger.info("Level complete - no more waves will spawn");
      return;
    }

    setCurrentWave(currentWave + 1);
    waveActive = false; // Wave not active during preparation
    preparationPhaseActive = true;
    preparationPhaseTimer = 0.0f;
    currentEnemyPos = 0;
    enemiesDisposed = 0; // Reset disposed counter for new wave
    int maxLanes = Math.min(currentWave + 1, 5);

    entitySpawn.spawnEnemiesFromConfig(); // new method
    enemiesToSpawn = entitySpawn.getSpawnCount();
    waveLaneSequence = new ArrayList<>(laneOrder.subList(0, maxLanes));
    Collections.shuffle(waveLaneSequence);
    waveLanePointer = 0;

    if (waveEventListener != null) {
      waveEventListener.onPreparationPhaseStarted(currentWave);
      waveEventListener.onWaveChanged(currentWave);
    }
  }

  /**
   * Ends the current wave and immediately begins the next one. External systems listen to wave
   * change events for UI updates.
   */
  public void endWave() {
    waveActive = false;
    initialiseNewWave();
  }

  /** Starts the actual wave after preparation phase ends. */
  private void startWave() {
    waveActive = true;
    preparationPhaseActive = false;
    timeSinceLastSpawn = 0.0f; // Reset spawn timer for new wave

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

  /**
   * @return true if currently in preparation phase
   */
  public boolean isPreparationPhaseActive() {
    return preparationPhaseActive;
  }

  /**
   * @return remaining time in preparation phase (0 if not in preparation phase)
   */
  public float getPreparationPhaseRemainingTime() {
    if (!preparationPhaseActive) {
      return 0.0f;
    }
    return Math.max(0.0f, preparationPhaseDuration - preparationPhaseTimer);
  }

  /**
   * @return preparation phase duration in seconds
   */
  public float getPreparationPhaseDuration() {
    return preparationPhaseDuration;
  }

  /**
   * Called when an enemy is disposed/destroyed. Updates the disposed counter and checks if the wave
   * should end.
   */
  public void onEnemyDisposed() {
    enemiesDisposed++;
    logger.debug(
        "Enemy disposed. Count: {}/{} (spawned: {})",
        enemiesDisposed,
        enemiesToSpawn,
        currentEnemyPos);

    // Check if all enemies for this wave have been disposed
    // Also ensure all enemies have been spawned before completing the wave
    if (enemiesDisposed >= enemiesToSpawn && currentEnemyPos >= enemiesToSpawn && waveActive) {
      logger.info("Wave {} completed! All enemies spawned and disposed.", currentWave);

      // Check if this was the last wave for the current level
      int maxWaves = getWaveCountForLevel(currentLevel);
      if (currentWave >= maxWaves) {
        logger.info("All waves completed for level {}! Level complete!", currentLevel);
        levelComplete = true;
        waveActive = false; // Just stop the wave, don't call endWave()
        return; // Don't start a new wave
      }

      endWave(); // Only call endWave() for non-final waves
    }
  }

  /**
   * @return number of enemies disposed in current wave
   */
  public int getEnemiesDisposed() {
    return enemiesDisposed;
  }

  /**
   * @return number of enemies spawned in current wave
   */
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
    logger.info("Level reset - ready for new level");
  }

  /**
   * Resets the WaveManager to its initial state for a fresh game start. This should be called when
   * starting a new game session.
   */
  public void resetToInitialState() {
    currentWave = 0;
    currentLevel = 1;
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
    logger.info("WaveManager reset to initial state - ready for new game");
  }

  /**
   * @return number of enemies remaining in current wave
   */
  public int getEnemiesRemaining() {
    return Math.max(0, enemiesToSpawn - enemiesDisposed);
  }

  /**
   * @return current level number
   */
  public int getCurrentLevel() {
    return currentLevel;
  }

  /**
   * Sets the current level. This should be called when loading a specific level.
   *
   * @param level the level number to set
   */
  public void setCurrentLevel(int level) {
    this.currentLevel = level;
    // Reset level state when switching levels
    resetLevel();
    logger.info("Level set to {}", level);
  }

  /**
   * Update function to be called by main game loop. Handles preparation phase timer and enemy
   * spawning.
   *
   * @param deltaTime time elapsed since last update in seconds
   */
  public void update(float deltaTime) {
    // Handle preparation phase
    if (preparationPhaseActive) {
      preparationPhaseTimer += deltaTime;
      if (preparationPhaseTimer >= preparationPhaseDuration) {
        startWave();
      }
      return; // Don't spawn enemies during preparation phase
    }

    // Handle wave spawning
    if (waveActive) {
      timeSinceLastSpawn += deltaTime;
      float spawnInterval = 5.0f;
      if (timeSinceLastSpawn >= spawnInterval) {
        spawnEnemy(getLane());
        timeSinceLastSpawn -= spawnInterval;
      }
    }
  }

  /**
   * Returns the next lane index from a pre-shuffled sequence, reshuffling when the sequence is
   * exhausted to avoid long runs on the same lane.
   *
   * @return lane index in [0, 5]
   */
  public int getLane() {
    // If waveLaneSequence is empty, initialize it with default lanes
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

  /**
   * Spawns a single enemy of the next type in the provided lane. Ends the wave once the configured
   * number of spawns has been reached.
   *
   * @param laneNumber lane index to spawn into
   */
  public void spawnEnemy(int laneNumber) {
    if (currentEnemyPos >= enemiesToSpawn) {
      // All enemies for this wave have been spawned, but wave continues until all are disposed
      return;
    }
    if (enemySpawnCallback == null) {
      logger.warn("No enemy spawn callback set - cannot spawn enemy");
      return;
    }
    String robotType = entitySpawn.getNextRobotType(); // new method
    enemySpawnCallback.spawnEnemy(9, laneNumber, robotType);
    currentEnemyPos++;
  }

  // Wave configuration methods (moved from WaveFactory)

  /** Gets the game configuration, loading it lazily if needed. */
  private static GameConfig getGameConfig() {
    if (gameConfig == null) {
      gameConfig = loadGameConfig();
    }
    return gameConfig;
  }

  /** Loads game configuration data from JSON. */
  private static GameConfig loadGameConfig() {
    return FileLoader.readClass(GameConfig.class, "configs/level1.json");
  }

  /**
   * Gets the number of waves configured for a specific level.
   *
   * @param levelNumber the level number to check
   * @return the number of waves for that level
   */
  public int getWaveCountForLevel(int levelNumber) {
    GameConfig config = getGameConfig();
    return config.getWaveCountForLevel(levelNumber);
  }

  /**
   * @return the configured weight/budget for the current wave
   */
  public int getWaveWeight() {
    return getCurrentWaveConfig().waveWeight;
  }

  /**
   * @return the minimum number of enemies to spawn for the current wave
   */
  public int getMinZombiesSpawn() {
    return getCurrentWaveConfig().minZombiesSpawn;
  }

  /**
   * @return the experience awarded for completing the current wave
   */
  public int getExpGained() {
    return getCurrentWaveConfig().expGained;
  }

  /**
   * @return the enemy spawn attributes (cost + chance) for the current wave.
   */
  public Map<String, EnemySpawnConfig> getEnemyConfigs() {
    BaseWaveConfig wave = getCurrentWaveConfig();

    if (wave == null) {
      return new java.util.HashMap<>();
    }

    // Construct Map from individual fields for backward compatibility
    Map<String, EnemySpawnConfig> configs = new java.util.HashMap<>();
    configs.put("standard", wave.standard);
    configs.put("fast", wave.fast);
    configs.put("tanky", wave.tanky);
    configs.put("bungee", wave.bungee);

    return configs;
  }

  /**
   * Helper function to get the current wave config based on current level and wave number.
   *
   * @return base wave config with the corresponding wave number
   */
  private BaseWaveConfig getCurrentWaveConfig() {
    int waveNumber = getCurrentWave();
    int waveIndex = waveNumber - 1; // Convert to 0-based index

    GameConfig config = getGameConfig();
    return config.getWave(currentLevel, waveIndex);
  }
}
