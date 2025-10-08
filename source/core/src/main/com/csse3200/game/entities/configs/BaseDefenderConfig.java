package com.csse3200.game.entities.configs;

/** Defines a set of properties for all defenders. */
public class BaseDefenderConfig extends BaseEntityConfig {
  private int cost;
  private int range;
  private float attackSpeed;
  private float critChance;
  private int damage;
  private String direction;
  private int numSprites;
  private String projectilePath;
  private String soundPath;

  /** Creates a new BaseDefenceConfig with default values. */
  public BaseDefenderConfig() {
    // Default constructor
  }

  /**
   * Gets the cost of the defender.
   *
   * @return the cost of the defender
   */
  public int getCost() {
    return cost;
  }

  /**
   * Gets the range value for this entity.
   *
   * @return the range value
   */
  public int getRange() {
    return range;
  }

  /**
   * Gets the attack value for this entity (measured in seconds/attack, like an attack 'period')
   *
   * @return the attack value
   */
  public float getAttackSpeed() {
    return attackSpeed;
  }

  /**
   * Gets the attack speed value for this entity.
   *
   * @return the attack speed value
   */
  public float getCritChance() {
    return critChance;
  }

  /**
   * Gets the damage value for this entity.
   *
   * @return the damage value
   */
  public int getDamage() {
    return damage;
  }

  /**
   * Get the direction the entity faces
   *
   * @return attacking direction of the entity
   */
  public String getDirection() {
    return direction;
  }

  /**
   * Gets the number of sprites in the enitity's sprite sheet
   *
   * @return the number of sprites
   */
  public int getNumSprites() {
    return numSprites;
  }

  /**
   * Gets the file location for the projectile for this entity.
   *
   * @return the projectile path
   */
  public String getProjectilePath() {
    return projectilePath;
  }

  /**
   * Gets the path to the sound that plays on placement
   *
   * @return the path to the sound
   */
  public String getSoundPath() {
    return soundPath;
  }

  /**
   * Gets the path to the sound that plays on placement
   *
   * @return the path to the sound
   */
  public String getSoundPath() {
    return soundPath;
  }
}
