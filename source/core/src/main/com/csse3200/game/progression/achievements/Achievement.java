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

  //progression tracking
  private int currentProgress;
  private int goal;

  // Tier system
  public enum Tier { T1, T2, T3 }
  private Tier tier;

  /**
   * Default constructor for Achievement.
   */
  public Achievement() {
    this.name = "";
    this.description = "";
    this.skillPoint = 0;
    //all achievements are T1 by default
    this.tier = Tier.T1;
    //this.unlocked = false;
  }

  /**
   * Creates a Achievement instance with specified starting achievement details.
   *
   * @param name        achievement name
   * @param description achievement description
   * @param skillPoint  achievement skillpoint
   */
  public Achievement(String name, String description, int skillPoint, int goal, Tier tier) {
    this.name = name;
    this.description = description;
    this.skillPoint = skillPoint;
    this.goal = goal;
    this.tier = tier;
    this.currentProgress = 0;
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
   * Gets the achievement's current progress.
   *
   * @return the achievement's current progress.
   */
  public int getCurrentProgress() {
    return currentProgress;
  }

  /**
   * Gets the achievement's goal.
   *
   * @return the achievement's goal
   */
  public int getGoal() {
    return goal;
  }

  /**
   * Gets the achievement's tier.
   *
   * @return the achievement's tier
   */
  public Tier getTier() {
    return tier;
  }

  /**
   * Increment progress by a certain amount.
   */
  public void addProgress(int amount) {
    if (!unlocked) {
      currentProgress += amount;
      if (currentProgress >= goal) {
        unlock();
      }
    }
  }

  /**
   * Displays progress in "x/y" form.
   */
  public String getProgressString() {
    return currentProgress + "/" + goal;
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
