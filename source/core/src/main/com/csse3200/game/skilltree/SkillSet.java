package com.csse3200.game.skilltree;

import java.util.ArrayList;
import java.util.List;

public class SkillSet {

    private static List<Skill> Skills = new ArrayList<>();
    private List<Skill> unlockedSkills = new ArrayList<>();

    static {

        Skills.add(new Skill("Increase Health Basic", Skill.StatType.HEALTH, 10, 1));
        Skills.add(new Skill("Increase AD Basic", Skill.StatType.ATTACK_DAMAGE, 10, 1));
        Skills.add(new Skill("Increase firing Basic", Skill.StatType.FIRING_SPEED, 10, 1));
        Skills.add(new Skill("Increase crit Basic", Skill.StatType.CRIT_CHANCE, 10, 1));
        Skills.add(new Skill("Increase armour Basic", Skill.StatType.ARMOUR, 10, 1));

        Skills.add(new Skill("Increase Health Intermediate", Skill.StatType.HEALTH, 20, 2));
        Skills.add(new Skill("Increase AD Intermediate", Skill.StatType.ATTACK_DAMAGE, 20, 2));
        Skills.add(new Skill("Increase firing Intermediate", Skill.StatType.FIRING_SPEED, 20, 2));
        Skills.add(new Skill("Increase crit Intermediate", Skill.StatType.CRIT_CHANCE, 20, 2));
        Skills.add(new Skill("Increase armour Intermediate", Skill.StatType.ARMOUR, 20, 2));

        Skills.add(new Skill("Increase Health Advanced", Skill.StatType.HEALTH, 30, 3));
        Skills.add(new Skill("Increase AD Advanced", Skill.StatType.ATTACK_DAMAGE, 30, 3));
        Skills.add(new Skill("Increase firing Advanced", Skill.StatType.FIRING_SPEED, 30, 3));
        Skills.add(new Skill("Increase crit Advanced", Skill.StatType.CRIT_CHANCE, 30, 3));
        Skills.add(new Skill("Increase armour Advanced", Skill.StatType.ARMOUR, 30, 3));

    }

    public static List<Skill> getSkills() {
        return Skills;
    }

    public void addSkill(Skill skill) {
        unlockedSkills.add(skill);
        // remove skill point/s
    }

    public List<Skill> getUnlockedSkills() {
        return unlockedSkills;
    }
}
