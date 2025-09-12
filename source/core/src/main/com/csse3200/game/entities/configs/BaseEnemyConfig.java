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

  /**
   * Gets the name of this entity.
   *
   * @return the name
   */
  public String getName() {
    return "Base enemy";
  }

  /**
   * Gets the file path atlas file associated with the entity's animations.
   *
   * @return the file path of the atlas file.
   */
  public String getAtlasFile() {
    return "images/robot_placeholder.atlas";
  }

  /**
   * Gets the filepath of the default sprite for the enemy. This sprite will be used in the enemy
   * log.
   *
   * @return the file path of the default enemy sprite.
   */
  public String getDefaultSprite() {
    return "images/default_enemy_image.png";
  }

  /**
   * Gets the health value for this entity.
   *
   * @return the health value
   */
  public int getHealth() {
    return 1;
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getAttack() {
    return 1;
  }

  /**
   * Gets the movement speed for this entity.
   *
   * @return the movement speed
   */
  public float getMovementSpeed() {
    return 1;
  }

  /**
   * Gets the size of the enemy sprite. 1 is the standard size, so the enemy will be scaled by this
   * value. NOTE: THIS DOESN'T WORK because LevelGameArea rescales the enemy to match the tile
   * height.
   *
   * @return the enemy sprite size.
   */
  public float getScale() {
    return 1f;
  }
}
