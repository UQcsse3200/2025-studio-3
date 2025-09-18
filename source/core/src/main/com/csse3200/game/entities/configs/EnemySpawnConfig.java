package com.csse3200.game.entities.configs;

/** Defines the spawn parameters for a specific enemy type inside a wave from JSON */
public class EnemySpawnConfig {
  /** Default enemy cost. */
  private int cost = 1;

  /** Relative spawn chance weight for this enemy. All chances are normalised before use. */
  private float chance = 1.0f;

  /** Default constructor. */
  public EnemySpawnConfig() {
    // Default constructor with default field values
  }

  /** Constructor with parameters. */
  public EnemySpawnConfig(int cost, float chance) {
    this.cost = cost;
    this.chance = chance;
  }

  /** Gets the cost of this enemy type. */
  public int getCost() {
    return cost;
  }

  /** Gets the spawn chance weight for this enemy type. */
  public float getChance() {
    return chance;
  }
}
