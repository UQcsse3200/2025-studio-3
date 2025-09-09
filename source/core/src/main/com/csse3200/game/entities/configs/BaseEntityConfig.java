package com.csse3200.game.entities.configs;

/**
 * Defines a basic set of properties stored in entities config files to be loaded by Entity
 * Factories.
 */
public class BaseEntityConfig {
  /** Creates a new BaseEntityConfig with default values. */
  public BaseEntityConfig() {
    // Default constructor with default field values
  }

  int health;

  int baseAttack;

  int speed;

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
}
