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

  public int health;

  public int baseAttack;

  float movementSpeed;

  /**
   * Gets the health value for this entity.
   *
   * @return the health value
   */
  public int getHealth() {
    return health;
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getAttack() {
    return baseAttack;
  }

  public float getMovementSpeed() {
    return movementSpeed;
  }
}
