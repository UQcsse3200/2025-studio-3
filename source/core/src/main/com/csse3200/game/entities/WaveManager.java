package com.csse3200.game.entities;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.services.GameTime;
import java.util.*;

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

  private static int currentWave = 0;
  private static int currentLevel = 1;
  private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
  private int enemiesToSpawn = 0;
  private int currentEnemyPos;
  private int enemiesDisposed = 0;
  private float timeSinceLastSpawn;
  private boolean waveActive = false;
  private boolean levelCompleted = false;
  
  // Preparation phase variables
  private boolean preparationPhaseActive = false;
  private float preparationPhaseDuration = 10.0f;
  private float preparationPhaseTimer = 0.0f;

  private static Entity gameEntity;
  private final GameTime gameTime;
  private final EntitySpawn entitySpawn;
  private LevelGameArea levelGameArea;

  private List<Integer> waveLaneSequence;
  private int waveLanePointer;

  public WaveManager() {
    this.gameTime = new GameTime();
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn();
    this.preparationPhaseActive = false;
    this.preparationPhaseTimer = 0.0f;
    this.enemiesDisposed = 0;

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
    // Check if level is completed
    if (levelCompleted) {
      System.out.println("Level " + currentLevel + " completed! Moving to next level...");
      startNextLevel();
      return;
    }
    
    setCurrentWave(currentWave + 1);
    waveActive = false; // Wave not active during preparation
    preparationPhaseActive = true;
    preparationPhaseTimer = 0.0f;
    currentEnemyPos = 0;
    enemiesDisposed = 0; // Reset disposed counter for new wave
    int maxLanes = Math.min(currentWave + 1, 5);

    //    getEnemies();
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

  /**
   * Starts the actual wave after preparation phase ends.
   */
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
   * Called when an enemy is disposed/destroyed. Updates the disposed counter
   * and checks if the wave should end.
   */
  public void onEnemyDisposed() {
    enemiesDisposed++;
    System.out.println("Enemy disposed. Count: " + enemiesDisposed + "/" + enemiesToSpawn);
    
    // Check if all enemies for this wave have been disposed
    if (enemiesDisposed >= enemiesToSpawn && waveActive) {
      System.out.println("Wave " + currentWave + " completed! All enemies disposed.");
      
      // Check if this was the last wave of the level
      if (currentWave >= 3) { // Level 1 has 3 waves
        System.out.println("All waves completed for level " + currentLevel + "!");
        levelCompleted = true;
        endWave();
      } else {
        endWave();
      }
    }
  }

  /**
   * @return number of enemies disposed in current wave
   */
  public int getEnemiesDisposed() {
    return enemiesDisposed;
  }

  /**
   * @return number of enemies remaining in current wave
   */
  public int getEnemiesRemaining() {
    return Math.max(0, enemiesToSpawn - enemiesDisposed);
  }

  /**
   * Starts the next level
   */
  private void startNextLevel() {
    currentLevel++;
    currentWave = 0;
    levelCompleted = false;
    System.out.println("Starting Level " + currentLevel);
    
    if (gameEntity != null && gameEntity.getEvents() != null) {
      gameEntity.getEvents().trigger("levelChanged", currentLevel);
    }
    
    // Start the first wave of the new level
    initialiseNewWave();
  }

  /**
   * @return current level number
   */
  public static int getCurrentLevel() {
    return currentLevel;
  }

  /**
   * @return true if the current level is completed
   */
  public boolean isLevelCompleted() {
    return levelCompleted;
  }

  /**
   * Update function to be called by main game loop. Handles preparation phase timer and enemy spawning.
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

  /** Computes how many enemies this wave should spawn via EntitySpawn. */
  private void getEnemies() {
    entitySpawn.spawnEnemies();
    enemiesToSpawn = entitySpawn.getSpawnCount();
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
    //    String robotType = entitySpawn.getRandomRobotType();
    String robotType = entitySpawn.getNextRobotType(); // new method
    levelGameArea.spawnRobot(9, laneNumber, robotType);
    currentEnemyPos++;
  }
}
