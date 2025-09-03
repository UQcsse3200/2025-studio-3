package com.csse3200.game.progression.skilltree;

import java.util.ArrayList;
import java.util.List;

/**
 * The SkillSet class manages all available skills in the game and tracks which
 * skills have been unlocked by a player.
 * Contains a static list of all predefined skills
 * Tracks unlocked skills for a player which get activated from the Skilltree
 * Screen
 */

public class SkillSet {

  /**
   * Static list of all available skills in the game.
   */
  private static transient final List<Skill> Skills = new ArrayList<>();

  /**
   * List of skills that have been unlocked for the player.
   */
  private List<Skill> unlockedSkills = new ArrayList<>();

  // Static initializer block to populate the predefined skills.
  // Skills are categorized into Basic, Intermediate, and Advanced levels with
  // increasing stat bonuses.

  static {
    // Basic Skills
    Skills.add(new Skill("Increase Health Basic", Skill.StatType.HEALTH, 10, 1));
    Skills.add(new Skill("Increase AD Basic", Skill.StatType.ATTACK_DAMAGE, 10, 1));
    Skills.add(new Skill("Increase firing Basic", Skill.StatType.FIRING_SPEED, 10, 1));
    Skills.add(new Skill("Increase crit Basic", Skill.StatType.CRIT_CHANCE, 10, 1));
    Skills.add(new Skill("Increase armour Basic", Skill.StatType.ARMOUR, 10, 1));

    // Intermediate Skills
    Skills.add(new Skill("Increase Health Intermediate", Skill.StatType.HEALTH, 20, 2));
    Skills.add(new Skill("Increase AD Intermediate", Skill.StatType.ATTACK_DAMAGE, 20, 2));
    Skills.add(new Skill("Increase firing Intermediate", Skill.StatType.FIRING_SPEED, 20, 2));
    Skills.add(new Skill("Increase crit Intermediate", Skill.StatType.CRIT_CHANCE, 20, 2));
    Skills.add(new Skill("Increase armour Intermediate", Skill.StatType.ARMOUR, 20, 2));

    // Advanced Skills
    Skills.add(new Skill("Increase Health Advanced", Skill.StatType.HEALTH, 30, 3));
    Skills.add(new Skill("Increase AD Advanced", Skill.StatType.ATTACK_DAMAGE, 30, 3));
    Skills.add(new Skill("Increase firing Advanced", Skill.StatType.FIRING_SPEED, 30, 3));
    Skills.add(new Skill("Increase crit Advanced", Skill.StatType.CRIT_CHANCE, 30, 3));
    Skills.add(new Skill("Increase armour Advanced", Skill.StatType.ARMOUR, 30, 3));
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
   * @param name the name of the skill to check
   * @return true if the skill is unlocked, false otherwise
   */
  public boolean checkIfUnlocked(String name){
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
}
