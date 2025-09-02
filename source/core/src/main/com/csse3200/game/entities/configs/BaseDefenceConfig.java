package com.csse3200.game.entities.configs;


/**
 * Defines the properties stored in sigma config files to be loaded by the NPC Factory.
 */
public class BaseDefenceConfig extends BaseEntityConfig {
    public enum Type {
        LONG_RANGE,
        RANGED,
        MELEE
    };

    public enum State {
        IDLE,
        ATTACKING
    };

    public int health;
    public int baseAttack;
    public Type type;
    public int range;
    public State state;
    public int attackSpeed;
    public int critChance;
}
