package com.csse3200.game.entities.configs;

/** Defines the properties stored in defences.json to be loaded by the NPC Factory. */
public class BaseDefenceConfig extends BaseEntityConfig {
  /** Creates a new BaseDefenceConfig with default values. */
  public BaseDefenceConfig() {
    // Default constructor with default field values
  }

  public int health;

  public int baseAttack;

  /** Defence type identifier */
  public int type;

  /** Attack range of the defence */
  public int range;

  /** Current state of the defence */
  public int state;

  /** Attack speed of the defence */
  public int attackSpeed;

  /** Critical hit chance percentage */
  public int critChance;
}
