package com.csse3200.game.entities.configs;

/** Defines the properties stored in defences.json to be loaded by the NPC Factory. */
public class BaseDefenceConfig extends BaseEntityConfig {
  /** Creates a new BaseDefenceConfig with default values. */
  public BaseDefenceConfig() {
    // Default constructor with default field values
  }

  /*
  public enum Type {
      LONG_RANGE,
      RANGED,
      MELEE
  };

  public enum State {
      IDLE,
      ATTACKING
  };
  */

  public String name = "Name";
  public String description = "Description";
  public int health = 1;
  public int baseAttack = 1;

  /** Defence type identifier */
  public int type = 1;

  /** Attack range of the defence */
  public int range = 1;

  /** Current state of the defence */
  public int state = 1;

  /** Attack speed of the defence */
  public int attackSpeed = 1;

  /** Critical hit chance percentage */
  public int critChance = 0;
}
