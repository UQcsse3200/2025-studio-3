package com.csse3200.game.entities.configs;

/** Defines the properties stored in defences.json to be loaded by the NPC Factory. */
public class BaseDefenderConfig extends BaseEntityConfig {
  /** Creates a new BaseDefenceConfig with default values. */
  public BaseDefenderConfig() {
    // Default constructor with default field values
  }

  /** Defence type identifier */
  private int rangeType;

  /** Attack range of the defence */
  private int range;

  /** Current state of the defence */
  private int state;

  /** Attack speed of the defence */
  private int attackSpeed;

  /** Critical hit chance percentage */
  private int critChance;

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getRangeType() {
    return rangeType;
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getRange() {
    return range;
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getAttackState() {
    return state;
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
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getCritChance() {
    return critChance;
  }
}
