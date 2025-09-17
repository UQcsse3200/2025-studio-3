package com.csse3200.game.entities;

import com.csse3200.game.areas.EnemySpawner;
import com.csse3200.game.areas.GameArea;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
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
  private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
  private int enemiesToSpawn = 0;
  private int currentEnemyPos;
  private float timeSinceLastSpawn;
  private boolean waveActive = false;

  private static Entity gameEntity;
  private final GameTime gameTime;
  private final EntitySpawn entitySpawn;
  private EnemySpawner enemySpawner; // use the interface instead of a concrete class

  private List<Integer> waveLaneSequence;
  private int waveLanePointer;

  public WaveManager() {
    this.gameTime = new GameTime();
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn();

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

    Collections.shuffle(laneOrder);
  }

  /**
   * Advances to the next wave, resets internal state and lane sequence, and computes the number of
   * enemies to spawn for this wave.
   */
  public void initialiseNewWave() {
    setCurrentWave(currentWave + 1);
    waveActive = true;
    currentEnemyPos = 0;
    int maxLanes = Math.min(currentWave + 1, 5);

    getEnemies();
    waveLaneSequence = new ArrayList<>(laneOrder.subList(0, maxLanes));
    Collections.shuffle(waveLaneSequence);
    waveLanePointer = 0;

    if (gameEntity != null && gameEntity.getEvents() != null) {
      gameEntity.getEvents().trigger("newWaveStarted", currentWave);
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

  /** Set the game area used for spawning enemies. Must implement EnemySpawner. */
  public void setGameArea(GameArea area) {
    if (area instanceof EnemySpawner spawner) {
      this.enemySpawner = spawner;
    } else {
      throw new IllegalArgumentException(
          "Provided GameArea does not implement EnemySpawner: " + area.getClass().getName());
    }
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
   * Update function to be called by main game loop Checks if a time interval has passed to spawn
   * the next enemy
   */
  public void update() {
    timeSinceLastSpawn += gameTime.getDeltaTime();
    float spawnInterval = 5.0f;
    if (timeSinceLastSpawn >= spawnInterval && waveActive) {
      spawnEnemy(getLane());
      timeSinceLastSpawn -= spawnInterval;
    }
  }

  /**
   * Returns the next lane index from a pre-shuffled sequence, reshuffling when the sequence is
   * exhausted to avoid long runs on the same lane.
   *
   * @return lane index in [0, 5]
   */
  public int getLane() {
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
    if (currentEnemyPos == enemiesToSpawn) {
      endWave();
      return;
    }
    RobotType robotType = entitySpawn.getRandomRobotType();
    enemySpawner.spawnRobot(9, laneNumber, robotType);
    currentEnemyPos++;
  }
}
