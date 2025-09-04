package com.csse3200.game.entities.configs;


/**
 * Defines the properties stored in defences.json to be loaded by the NPC Factory.
 */
public class BaseDefenceConfig extends BaseEntityConfig {
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

    public int type = 1;
    public int range = 1;
    public int state = 1;
    public int attackSpeed = 1;
    public int critChance = 0;
}
