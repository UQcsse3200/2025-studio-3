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

    public Type type = Type.LONG_RANGE;
    public int range = 1;
    public State state = State.IDLE;
    public int attackSpeed = 1;
    public int critChance = 0;
}
