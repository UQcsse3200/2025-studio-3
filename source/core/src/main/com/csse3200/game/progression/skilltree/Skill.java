package com.csse3200.game.progression.skilltree;



/**
 * Represents a skill in the game's skill tree
 * Each skill has a name, a type of stat it affects, a percentage bonus, a cost and is either un/locked.
 */
public class Skill {

    /**
     * Enumeration of all possible stats that a skill can modify.
     */
    public enum StatType {
        /** Increases the player's health. */
        HEALTH,
        /** Increases the player's attack damage. */
        ATTACK_DAMAGE,
        /** Increases the player's firing speed. */
        FIRING_SPEED,
        /** Increases the player's critical hit chance. */
        CRIT_CHANCE,
        /** Increases the player's defense. */
        ARMOUR
    }

    private final String name;
    private final StatType statType;
    private final int cost;
    private boolean locked;
    private final float percentage;

    /**
     * Constructs a new Skill with the specified parameters.
     * @param name       the name of the skill
     * @param statType   the type of stat the skill affects
     * @param percentage the percentage bonus the skill provides
     * @param cost       the cost to unlock the skill
     */
    public Skill(String name, StatType statType, float percentage, int cost) {
        this.name = name;
        this.statType = statType;
        this.percentage = percentage;
        this.cost = cost;
        this.locked = true; // skills are initially locked
    }

    /**
     * Returns the name of the skill.
     * @return the skill name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of stat this skill affects.
     * @return the stat type
     */
    public StatType getStatType() {
        return statType;
    }

    /**
     * Returns the cost required to unlock this skill.
     * @return the skill cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns the percentage bonus this skill provides.
     * @return the bonus percentage
     */
    public float getPercentage() {
        return percentage;
    }

    /**
     * Returns the lock status of the skill.
     * @return true if the skill is locked and false if unlocked
     */
    public boolean getLockStatus() {
        return locked;
    }

    /**
     * Unlocks the skill, setting its lock status to false.
     */
    public void unlock() {
        this.locked = false;
    }
}
