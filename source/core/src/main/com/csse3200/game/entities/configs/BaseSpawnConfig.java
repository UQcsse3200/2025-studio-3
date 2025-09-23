package com.csse3200.game.entities.configs;

/** BaseSpawnConfig is a class that represents a spawn configuration. */
public class BaseSpawnConfig {
  /** Default enemy cost. */
  private int cost = 1;

  /** Relative spawn chance weight for this enemy. All chances are normalised before use. */
  private float chance = 1.0f;

  /** Default constructor. */
  public BaseSpawnConfig() {
    // Default constructor
  }

  /**
   * Gets the cost of this enemy type.
   *
   * @return the cost of this enemy type
   */
  public int getCost() {
    return cost;
  }

  /**
   * Gets the spawn chance weight for this enemy type.
   *
   * @return the spawn chance weight for this enemy type
   */
  public float getChance() {
    return chance;
  }
}
