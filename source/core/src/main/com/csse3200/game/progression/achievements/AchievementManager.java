package com.csse3200.game.progression.achievements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The AchievementManager class is responsible for storing all achievements.
 */

public class AchievementManager {
  private static Map<String, Achievement> achievements = new HashMap<>();

  /**
   * Constructor for AchievementManager.
   */
  public AchievementManager() {
    initializeDefaultAchievements();
  }

  /**
   * Initializes the default achievements.
   */
  private void initializeDefaultAchievements() {
    achievements.put("5_DEFENSES", new Achievement(
        "5_DEFENSES", "Unlocked 5 defenses", 5, 4, Achievement.Tier.T1));
    achievements.put("100_COINS", new Achievement(
        "100_COINS", "Earned 100 coins", 5, 5, Achievement.Tier.T1));
    achievements.put("LEVEL_1_COMPLETE", new Achievement(
        "LEVEL_1_COMPLETE", "Completed the first level", 10, 10, Achievement.Tier.T1));
    achievements.put("50_SHOTS", new Achievement(
        "50_SHOTS", "You fired 50 shots.", 5, 5, Achievement.Tier.T1));
    achievements.put("50_KILLS", new Achievement(
        "50_KILLS", "Earned 50 kills", 10, 10, Achievement.Tier.T1));
  }

  /**
   * Sets the achievements map.
   *
   * @param achievements map of achievements
   */
  public void setAchievements(Map<String, Achievement> achievements) {
        this.achievements = achievements != null ? 
            new HashMap<>(achievements) : new HashMap<>();
        if (this.achievements.isEmpty()) {
            initializeDefaultAchievements();
        }
    }

  /**
   * unlocks the Achievement through its provided name.
   *
   * @param name achievement name
   */
  public static void unlock(String name) {
    Achievement a = achievements.get(name);
    if (a != null && !a.isUnlocked()) {
      a.unlock();
    }
  }

  /**
   * Checks if an achievement is locked/unlocked. Returns true if unlocked, false
   * otherwise.
   *
   * @param name achievement name
   */
  public boolean isUnlocked(String name) {
    Achievement a = achievements.get(name);
    return a != null && a.isUnlocked();
  }

  /**
   * Returns all achievements.
   */
  public Collection<Achievement> getAllAchievements() {
    return achievements.values();
  }

  //achievements 2
  /** Increment progress on an achievement by name. Automatically unlocks if goal reached. */
  public static void addProgress(String name, int amount) {
    Achievement a = achievements.get(name);
    if (a != null) {
      a.addProgress(amount);
    }
  }

  /** Get an achievement by name for direct access (e.g., tiers, skill points, progress). */
  public static Achievement getAchievement(String name) {
    return achievements.get(name);
  }

  //probably not needed
  /** Add a new achievement to the manager. */
  public void addAchievement(Achievement achievement) {
    if (achievement != null && achievement.getName() != null) {
      achievements.put(achievement.getName(), achievement);
    }
  }

}

/*
to use in another class for testing:
// Increment progress
        AchievementManager.addProgress("5_DEFENSES", 1);

// Direct access for more control
        Achievement a = AchievementManager.getAchievement("5_DEFENSES");
        if (a != null) {
            System.out.println(a.getProgressString()); // "1/5"
            System.out.println(a.getTier());            // Tier.T1
        }

// Unlock directly
        AchievementManager.unlock("LEVEL_1_COMPLETE");
 */




