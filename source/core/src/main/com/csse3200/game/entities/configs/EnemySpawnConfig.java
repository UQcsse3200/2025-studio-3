package com.csse3200.game.entities.configs;


/** Defines the spawn parameters for a specific enemy type inside a wave from JSON*/
public class EnemySpawnConfig {
    /** Default enemy cost. */
    public int cost = 1;

    /**
     * Relative spawn chance weight for this enemy.
     * All chances are normalised before use.
     */
    public float chance = 1.0f;

    /** Default constructor. */
    public EnemySpawnConfig() {
        // Default constructor with default field values
    }

    /** Constructor with parameters. */
    public EnemySpawnConfig(int cost, float chance) {
        this.cost = cost;
        this.chance = chance;
    }
}
