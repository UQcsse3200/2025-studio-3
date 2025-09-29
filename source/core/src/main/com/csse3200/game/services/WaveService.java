package com.csse3200.game.services;

import com.csse3200.game.entities.WaveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaveService {

  private static final Logger logger = LoggerFactory.getLogger(WaveService.class);
  private final WaveManager waveManager;

  /** Creates new WaveService that creates its own WaveManager */
  public WaveService() {
    waveManager = new WaveManager("levelOne");
    logger.debug("[WaveService] Wave service created.");
  }

  /**
   * Update function to be called by main game loop. Handles preparation phase timer and enemy
   * spawning.
   *
   * @param delta time elapsed since last update in seconds
   */
  public void update(float delta) {
    waveManager.update(delta);
  }

  /**
   * @return The current wave being spawned by WaveManager
   */
  public int getCurrentWave() {
    return waveManager.getCurrentWave();
  }

  /**
   * Set the current wave in WaveManager to the provided one
   *
   * @param wave the wave to set
   */
  public void setCurrentWave(int wave) {
    waveManager.setCurrentWave(wave);
  }

  /** Tells WaveManager to start spawning the next wave */
  public void initialiseNewWave() {
    waveManager.initialiseNewWave();
  }

  public void setEnemySpawnCallback(WaveManager.EnemySpawnCallback callback) {
    waveManager.setEnemySpawnCallback(callback);
  }

  public void setWaveEventListener(WaveManager.WaveEventListener listener) {
    waveManager.setWaveEventListener(listener);
  }

  /** Method to be called when an enemy dies, to track wave end conditions */
  public void onEnemyDispose() {
    waveManager.onEnemyDisposed();
  }
}
