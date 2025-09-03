package com.csse3200.game.progression.achievements;

/**
 * The Achievement class is responsible for unlocking achievements. It stores
 * the names, description and skillpoint of each individual achievement.
 * Achievements are tied to a particular profile/load file. Achievements are
 * unlocked based on the specified unlocking citeria(s). Achievements are 
 * initialised to be locked.
 */

public class Achievement {
  private final String name;
  private final String description;
  private final int skillPoint;
  private boolean unlocked = false;

  /**
   * Default constructor for Achievement.
   */
  public Achievement() {
    this.name = "";
    this.description = "";
    this.skillPoint = 0;
    this.unlocked = false;
  }

  /**
   * Creates a Achievement instance with specified starting achievement details.
   *
   * @param name        achievement name
   * @param description achievement description
   * @param skillPoint  achievement skillpoint
   */
  public Achievement(String name, String description, int skillPoint) {
    this.name = name;
    this.description = description;
    this.skillPoint = skillPoint;
  }

  /**
   * Gets the achievement name.
   *
   * @return the achievement name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the achievement description.
   *
   * @return the achievement description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the achievement's skillpoint.
   *
   * @return the achievement's skillpoint
   */
  public int getSkillPoint() {
    return skillPoint;
  }

  /**
   * Checks the state of the achievement.
   *
   * @return the state of the achievement (true is unlocked/false if locked)
   */
  public boolean isUnlocked() {
    return unlocked;
  }

  /**
   * unlocks the achievement. Pops up on current screen indicating the associated
   * achievement has been unlocked.
   */
  public void unlock() {
    if (!unlocked) {
      unlocked = true;
      System.out.println("Achievement Unlocked: " + name + " - " + description);
    }
  }
}
