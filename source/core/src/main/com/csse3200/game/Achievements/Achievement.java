package com.csse3200.game.Achievements;

public class Achievement {
    private final String name;
    private final String description;
    private final int skillPoint;
    private boolean unlocked = false;
    private int current;  // current progress
    private int target;   // goal to reach


    public Achievement(String name, String description, int skillPoint) {
        this.name = name;
        this.description = description;
        this.skillPoint = skillPoint;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getSkillPoint() {return skillPoint; }
    public boolean isUnlocked() { return unlocked; }

    public void unlock() {
        if (!unlocked) {
            unlocked = true;
            System.out.println("Achievement Unlocked: " + name + " - " + description);
            // TODO: Trigger UI popup here
        }
    }
}
