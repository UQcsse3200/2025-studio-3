package com.csse3200.game.skilltree;

public class Skill {

    public enum StatType {
        HEALTH,
        ATTACK_DAMAGE,
        FIRING_SPEED,
        CRIT_CHANCE,
        ARMOUR
    }
    private final  String name;
    private final StatType statType;
    private final int cost;
    private boolean locked;
    private final float percentage;

    public Skill(String name, StatType statType, float percentage, int cost) {
        this.name = name;
        this.statType = statType;
        this.percentage = percentage;
        this.cost = cost;
        this.locked = true;
    }


    public String getName() {
        return name;
    }
    public StatType getStatType() {
        return statType;
    }
    public int getCost() {
        return cost;
    }

    public float getPercentage() {
        return percentage;
    }

    public boolean getLockStatus() {
        return locked;
    }

    public void unlock() {
        this.locked = false;


    }
}
