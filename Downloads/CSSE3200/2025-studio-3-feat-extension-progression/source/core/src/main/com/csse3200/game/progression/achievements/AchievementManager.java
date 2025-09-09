package com.csse3200.game.progression.achievements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The AchievementManager class is responsible for storing all achievements.
 */
public class AchievementManager {
  private Map<String, Achievement> achievements = new HashMap<>();

  /**
   * Constructor for AchievementManager.
   */
  public AchievementManager() {
    if (achievements.isEmpty()) {
      initializeDefaultAchievements();
    }
  }


  /**
   * Initializes the default achievements.
   * Only adds achievements that are missing (so unlocked ones aren’t reset).
   */
  private void initializeDefaultAchievements() {
    addIfMissing(new Achievement(
            "5_DEFENSES", "Unlocked 5 defenses", 5, 4, Achievement.Tier.T1));
    addIfMissing(new Achievement(
            "100_COINS", "Earned 100 coins", 5, 5, Achievement.Tier.T2));
    addIfMissing(new Achievement(
            "LEVEL_1_COMPLETE", "Completed the first level", 10, 10, Achievement.Tier.T3));
    addIfMissing(new Achievement(
            "50_SHOTS", "You fired 50 shots.", 5, 5, Achievement.Tier.T3));
    addIfMissing(new Achievement(
            "50_KILLS", "Earned 50 kills", 10, 10, Achievement.Tier.T2));
  }

  /** Helper: only add if not already present (prevents resetting saved states). */
  private void addIfMissing(Achievement achievement) {
    if (!achievements.containsKey(achievement.getName())) {
      achievements.put(achievement.getName(), achievement);
    }
  }

  /**
   * Unlocks the Achievement through its provided name.
   *
   * @param name achievement name
   */
  public void unlock(String name) {
    Achievement a = achievements.get(name);
    if (a != null && !a.isUnlocked()) {
      a.unlock();
    }
  }

  /**
   * Checks if an achievement is unlocked. Returns true if unlocked, false otherwise.
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

  /** Increment progress on an achievement by name. Automatically unlocks if goal reached. */
  public void addProgress(String name, int amount) {
    Achievement a = achievements.get(name);
    if (a != null) {
      a.addProgress(amount);
    }
  }

  /** Get an achievement by name for direct access (e.g., tier, skill points, progress). */
  public Achievement getAchievement(String name) {
    return achievements.get(name);
  }

  /** Add a new achievement to the manager. */
  public void addAchievement(Achievement achievement) {
    if (achievement != null && achievement.getName() != null) {
      achievements.put(achievement.getName(), achievement);
    }
  }
}
