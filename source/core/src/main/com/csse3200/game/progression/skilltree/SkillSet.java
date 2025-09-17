package com.csse3200.game.progression.skilltree;

import java.util.ArrayList;
import java.util.List;

/**
 * The SkillSet class manages all available skills in the game and tracks which skills have been
 * unlocked by a player. Contains a static list of all predefined skills Tracks unlocked skills for
 * a player which get activated from the Skilltree Screen
 */
public class SkillSet {

  /** Static list of all available skills in the game. */
  private static final List<Skill> Skills = new ArrayList<>();

  /** List of skills that have been unlocked for the player. */
  private final List<Skill> unlockedSkills = new ArrayList<>();

  // Static initializer block to populate the predefined skills.
  // Skills are categorized into Basic, Intermediate, Advanced  and Expert levels with
  // increasing stat bonuses.

  static {
    // Basic Skills
    Skills.add(new Skill("Health Basic", Skill.StatType.HEALTH, 1.1f, 1));
    Skills.add(new Skill("Attack Basic", Skill.StatType.ATTACK_DAMAGE, 1.1f, 1));
    Skills.add(new Skill("Firing Speed Basic", Skill.StatType.FIRING_SPEED, 1.1f, 1));
    Skills.add(new Skill("Crit Basic", Skill.StatType.CRIT_CHANCE, 0.1f, 1));
    Skills.add(new Skill("Currency Basic", Skill.StatType.CURRENCY_GEN, 1.1f, 1));

    // Intermediate Skills
    Skills.add(new Skill("Health Intermediate", Skill.StatType.HEALTH, 1.2f, 2));
    Skills.add(new Skill("Attack Intermediate", Skill.StatType.ATTACK_DAMAGE, 1.2f, 2));
    Skills.add(new Skill("Firing Speed Intermediate", Skill.StatType.FIRING_SPEED, 1.2f, 2));
    Skills.add(new Skill("Crit Intermediate", Skill.StatType.CRIT_CHANCE, 0.2f, 2));
    Skills.add(new Skill("Currency Intermediate", Skill.StatType.CURRENCY_GEN, 1.2f, 2));

    // Advanced Skills
    Skills.add(new Skill("Health Advanced", Skill.StatType.HEALTH, 1.3f, 3));
    Skills.add(new Skill("Attack Advanced", Skill.StatType.ATTACK_DAMAGE, 1.3f, 3));
    Skills.add(new Skill("Firing Speed Advanced", Skill.StatType.FIRING_SPEED, 1.3f, 3));
    Skills.add(new Skill("Crit Advanced", Skill.StatType.CRIT_CHANCE, 0.3f, 3));
    Skills.add(new Skill("Currency Advanced", Skill.StatType.CURRENCY_GEN, 1.3f, 3));

    // ExpertSkills
    Skills.add(new Skill("Health Expert", Skill.StatType.HEALTH, 1.50f, 4));
    Skills.add(new Skill("Attack Expert", Skill.StatType.ATTACK_DAMAGE, 1.50f, 4));
    Skills.add(new Skill("Firing Speed Expert", Skill.StatType.FIRING_SPEED, 1.50f, 4));
    Skills.add(new Skill("Crit Expert", Skill.StatType.CRIT_CHANCE, 0.5f, 4));
    Skills.add(new Skill("Currency Expert", Skill.StatType.CURRENCY_GEN, 1.5f, 4));
  }

  /**
   * Returns the list of all predefined skills in the game.
   *
   * @return a static list of Skills
   */
  public static List<Skill> getSkills() {
    return Skills;
  }

  /**
   * Adds a skill to this instance's unlocked skills list.
   *
   * @param skill to be added to list
   */
  public void addSkill(Skill skill) {
    unlockedSkills.add(skill);
  }

  public void removeSkill(Skill.StatType type) {
    unlockedSkills.removeIf(skill -> skill.getStatType().equals(type));
  }

  /**
   * Returns the list of skills that have been unlocked for this instance/player.
   *
   * @return a list of unlocked {@link Skill} objects
   */
  public List<Skill> getUnlockedSkills() {
    return unlockedSkills;
  }

  /**
   * Checks if a skill with the given name has been unlocked.
   *
   * @param name the name of the skill to check
   * @return true if the skill is unlocked, false otherwise
   */
  public boolean checkIfUnlocked(String name) {
    for (Skill skill : unlockedSkills) {
      if (skill.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Searches for a skill by name in the static list of all skills.
   *
   * @param name the name of the skill to search for
   * @return the {@link Skill} object if found; {@code null} otherwise
   */
  public Skill getSkill(String name) {
    for (Skill skill : SkillSet.getSkills()) {
      if (skill.getName().equals(name)) {
        return skill;
      }
    }
    return null;
  }

  /**
   * @param type the StatType of the skill to be checked
   * @return a float that represents the percentage increase of the input stat
   */
  public float getUpgradeValue(Skill.StatType type) {
    int level = getCurrentLevel(type);
    for (Skill skill : unlockedSkills) {
      if (skill.getStatType() == type && getLevel(skill.getName()) == level) {
        return skill.getPercentage();
      }
    }
    if (type == Skill.StatType.CRIT_CHANCE) {
      return 0;
    }
    return 1;
  }

  /**
   * @param skillName the name of the skill
   * @return an integer that corresponds to the level associated with the input
   */
  public int getLevel(String skillName) {
    int level;
    if (skillName.contains("Basic")) {
      level = 1;
    } else if (skillName.contains("Intermediate")) {
      level = 2;
    } else if (skillName.contains("Advanced")) {
      level = 3;
    } else if (skillName.contains("Expert")) {
      level = 4;
    } else {
      return 0;
    }
    return level;
  }

  /**
   * @param type the StateType of the skill
   * @return an integer that corresponds to the furthest unlocked level of that skill
   */
  public int getCurrentLevel(Skill.StatType type) {
    int level = 0;
    for (Skill skill : unlockedSkills) {
      if (skill.getStatType() == type && getLevel(skill.getName()) > level) {
        level = getLevel(skill.getName());
      }
    }
    return level;
  }

  /**
   * Checks if a skill with the given name has been unlocked.
   *
   * @param skillName the name of the skill to check
   * @return true if the skill is either Basic or previous levels with same type have been unlocked
   *     and false if otherwise
   */
  public boolean isUnlockable(String skillName) {
    int level = getLevel(skillName);
    Skill.StatType type = getSkill(skillName).getStatType();

    if (getCurrentLevel(type) == 0 && level == 1) {
      return true;
    } else if (getCurrentLevel(type) == 0 && level != 1) {
      return false;
    } else {
      return getCurrentLevel(type) == level - 1;
    }
  }
}
