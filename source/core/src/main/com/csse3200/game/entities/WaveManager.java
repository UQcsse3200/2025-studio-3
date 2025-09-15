package com.csse3200.game.entities;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.factories.WaveFactory;
import com.csse3200.game.services.GameTime;
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
 *   <li>Delegates the actual entity creation and placement to {@link LevelGameArea}.
 * </ul>
 *
 * <p>This class does not construct enemies nor touch rendering; it only orchestrates when/where to
 * request a spawn.
 */
public class WaveManager {
  private static final Logger logger = LoggerFactory.getLogger(WaveManager.class);

  private static int currentWave = 0;
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

  private static Entity gameEntity;
  private final GameTime gameTime;
  private final EntitySpawn entitySpawn;
  private LevelGameArea levelGameArea;
  private WaveFactory waveFactory;

  private List<Integer> waveLaneSequence;
  private int waveLanePointer;
  private boolean levelComplete = false;

  public WaveManager() {
    this.gameTime = new GameTime();
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn();
    this.preparationPhaseActive = false;
    this.preparationPhaseTimer = 0.0f;
    this.enemiesDisposed = 0;
    this.waveFactory = new WaveFactory(currentLevel);

    Collections.shuffle(laneOrder);
  }

  /**
   * Test-only constructor to inject a preconfigured {@link EntitySpawn}. Useful for unit tests that
   * avoid LibGDX file IO.
   *
   * @param entitySpawn spawn helper used by this manager
   */
  public WaveManager(EntitySpawn entitySpawn) {
    this.gameTime = new GameTime();
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = entitySpawn;
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

    if (gameEntity != null && gameEntity.getEvents() != null) {
      gameEntity.getEvents().trigger("preparationPhaseStarted", currentWave);
      gameEntity.getEvents().trigger("waveChanged", currentWave);
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

    if (gameEntity != null && gameEntity.getEvents() != null) {
      gameEntity.getEvents().trigger("waveStarted", currentWave);
    }
  }

  public void setGameArea(LevelGameArea levelGameArea) {
    this.levelGameArea = levelGameArea;
  }

  public static void setGameEntity(Entity gameEntity) {
    WaveManager.gameEntity = gameEntity;
  }

  public static int getCurrentWave() {
    return currentWave;
  }

  private static void setCurrentWave(int wave) {
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
    logger.debug("Enemy disposed. Count: {}/{}", enemiesDisposed, enemiesToSpawn);

    // Check if all enemies for this wave have been disposed
    if (enemiesDisposed >= enemiesToSpawn && waveActive) {
      logger.info("Wave {} completed! All enemies disposed.", currentWave);

      // Check if this was the last wave for the current level
      int maxWaves = waveFactory.getWaveCountForLevel(currentLevel);
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
    // Update waveFactory for the new level
    waveFactory = new WaveFactory(level);
    // Reset level state when switching levels
    resetLevel();
    logger.info("Level set to {}", level);
  }

  /**
   * Update function to be called by main game loop. Handles preparation phase timer and enemy
   * spawning.
   */
  public void update() {
    // Handle preparation phase
    if (preparationPhaseActive) {
      preparationPhaseTimer += gameTime.getDeltaTime();
      if (preparationPhaseTimer >= preparationPhaseDuration) {
        startWave();
      }
      return; // Don't spawn enemies during preparation phase
    }

    // Handle wave spawning
    if (waveActive) {
      timeSinceLastSpawn += gameTime.getDeltaTime();
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
    String robotType = entitySpawn.getNextRobotType(); // new method
    levelGameArea.spawnRobot(9, laneNumber, robotType);
    currentEnemyPos++;
  }
}
