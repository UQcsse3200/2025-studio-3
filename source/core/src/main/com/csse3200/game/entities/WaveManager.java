package com.csse3200.game.entities;

import com.badlogic.gdx.math.GridPoint2;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.services.GameTime;
import java.util.*;

public class WaveManager {

  private static int currentWave;
  private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
  private Entity[] enemies = {};
  private int currentEnemyPos;
  private float timeSinceLastSpawn;
  private boolean waveActive = false;

  private static Entity gameEntity;
  private final GameTime gameTime;
  private final EntitySpawn entitySpawn;
  private LevelGameArea levelGameArea;

  private List<Integer> waveLaneSequence;
  private int waveLanePointer;

  public WaveManager() {
    this.gameTime = new GameTime();
    currentWave = 0;
    this.timeSinceLastSpawn = 0f;
    this.waveLaneSequence = new ArrayList<>();
    this.waveLanePointer = 0;
    this.entitySpawn = new EntitySpawn(currentWave);

    Collections.shuffle(laneOrder);
  }

  public void initialiseNewWave() {
    currentWave++;
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

  public void endWave() {
    waveActive = false;
    initialiseNewWave();
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

  /**
   * Update function to be called by main game loop Checks if a time interval has passed to spawn
   * the next enemy
   */
  public void update() {
    timeSinceLastSpawn += gameTime.getDeltaTime();
    float spawnInterval = 5.0f;
    if (timeSinceLastSpawn >= spawnInterval) {
      if (waveActive) {
        spawnEnemy(getLane());
        timeSinceLastSpawn -= spawnInterval;
      }
    }
  }

  /**
   * Gets the next lane from a pre-shuffled sequence of available lanes This prevents long strings
   * of the same lane being chosen
   *
   * @return The next lane number to spawn an enemy in.
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

  private void getEnemies() {
    entitySpawn.spawnEnemies();
    enemies = entitySpawn.getEntities();
  }

  public void spawnEnemy(int laneNumber) {
    if (currentEnemyPos == enemies.length) {
      endWave();
      return;
    }
    levelGameArea.spawnRobotAtTile(new GridPoint2(9, laneNumber), true, true);
    currentEnemyPos++;
  }
}
