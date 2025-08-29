package com.csse3200.game.entities.configs;

/**
 * Defines a basic set of properties that each defence entity will share
 */
public abstract class DefenceConfig extends BaseEntityConfig {
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
