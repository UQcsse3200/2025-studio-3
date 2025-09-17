package com.csse3200.game.entities.configs;

/**
 * An abstract class representing the stats defining an enemy, including combat stats, movement
 * speed,
 */
public class BaseEnemyConfig extends BaseEntityConfig {
  /** Creates a new BaseEntityConfig with default values. */
  public BaseEnemyConfig() {
    // Default constructor with default field values
  }

  /** The enemy type's name */
  public String name = "Default Name";

  /** The atlas file for the enemy's animations */
  public String atlasFilePath = "images/robot_placeholder.atlas";

  /** The enemy's default sprite */
  public String defaultSprite = "images/default_enemy_image.png";

  /** Maximum health of the enemy */
  public int health = 1;

  /** Damage dealt by the enemy */
  public int attack = 1;

  /** The enemy's movement speed */
  public float movementSpeed = 1f;

  public String description = "Default Description";

  /**
   * The enemy's size NOTE: THIS DOESN'T WORK because LevelGameArea rescales the enemy to match the
   * tile height.
   */
  public float scale = 1f;
}
