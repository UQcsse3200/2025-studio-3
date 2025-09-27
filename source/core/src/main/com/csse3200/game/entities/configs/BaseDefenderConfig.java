package com.csse3200.game.entities.configs;

/** Defines a set of properties for all defenders. */
public class BaseDefenderConfig extends BaseEntityConfig {
  private int cost;
  private int rangeType; // what is this???
  private int range;
  private int attackSpeed;
  private int critChance;
  private int attack;
  private String projectilePath;

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
   * Gets the range type of the defender.
   *
   * @return the range type value
   */
  public int getRangeType() {
    return rangeType;
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
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getAttackSpeed() {
    return attackSpeed;
  }

  /**
   * Gets the attack speed value for this entity.
   *
   * @return the attack speed value
   */
  public int getCritChance() {
    return critChance;
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getAttack() {
    return attack;
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public String getProjectilePath() {
    return projectilePath;
  }
}
