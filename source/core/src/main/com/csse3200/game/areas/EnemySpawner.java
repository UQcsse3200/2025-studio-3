package com.csse3200.game.areas;

/**
 * Minimal interface for spawning enemies. WaveManager depends on this interface so it doesn't need
 * to know the concrete GameArea type.
 */
public interface EnemySpawner {
  /**
   * Spawn a robot at the specified grid location.
   *
   * @param col grid column
   * @param row grid row
   * @param robotType robot type identifier
   */
  void spawnRobot(int col, int row, String robotType);
}
