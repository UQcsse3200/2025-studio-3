package com.csse3200.game.progression.achievements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** The AchievementManager class is responsible for storing all achievements. */
public class AchievementManager {
  private Map<String, Achievement> achievements = new HashMap<>();

  /** Constructor for AchievementManager. */
  public AchievementManager() {
    initializeDefaultAchievements();
  }

  /** Initializes the default achievements. */
  private void initializeDefaultAchievements() {
    achievements.put("5_DEFENSES", new Achievement("5_DEFENSES", "Unlocked 5 defenses", 5));
    achievements.put("100_COINS", new Achievement("100_COINS", "Earned 100 coins", 5));
    achievements.put(
        "LEVEL_1_COMPLETE", new Achievement("LEVEL_1_COMPLETE", "Completed the first level", 10));
    achievements.put("50_SHOTS", new Achievement("50_SHOTS", "You fired 50 shots.", 5));
    achievements.put("50_KILLS", new Achievement("50_KILLS", "Earned 50 kills", 10));
  }

  // POTENTIAL NEW ACHIEVEMENTS
  // achievementManager.addAchievement(
  // new Achievement("First move", "Made your first move", 25)
  // );
  // achievementManager.addAchievement(
  // new Achievement("5Secin", "Loaded into the game for 5 seconds", 30)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Explorer", "Opened the achievements menu", 10)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Persistent", "Played the game 5 times", 50)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Completionist", "Unlocked all achievements", 100)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Sharpshooter", "Hit 10 perfect moves in a row", 40)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Speedrunner", "Finished a level in under 30 seconds", 75)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Collector", "Collected 100 items total", 20)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Unstoppable", "Survived for 10 minutes without failing", 60)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Night Owl", "Played the game after midnight", 15)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Early Bird", "Played the game before 7am", 15)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Casual Gamer", "Played for 1 hour total", 25)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Marathoner", "Played for 10 hours total", 100)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Risk Taker", "Triggered a near-fail but survived", 35)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Tactician", "Used 5 different strategies in one game", 45)
  // );
  // achievementManager.addAchievement(
  // new Achievement("The Comeback", "Recovered from near defeat to win", 80)
  // );
  // achievementManager.addAchievement(
  // new Achievement("Legend", "Achieved the highest possible score", 200)
  // );

  /**
   * Sets the achievements map.
   *
   * @param achievements map of achievements
   */
  public void setAchievements(Map<String, Achievement> achievements) {
    this.achievements = achievements != null ? new HashMap<>(achievements) : new HashMap<>();
    if (this.achievements.isEmpty()) {
      initializeDefaultAchievements();
    }
  }

  /**
   * unlocks the Achievement through its provided name.
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
   * Checks if an achievement is locked/unlocked. Returns true if unlocked, false otherwise.
   *
   * @param name achievement name
   */
  public boolean isUnlocked(String name) {
    Achievement a = achievements.get(name);
    return a != null && a.isUnlocked();
  }

  /** Returns all achievements. */
  public Collection<Achievement> getAllAchievements() {
    return achievements.values();
  }
}
