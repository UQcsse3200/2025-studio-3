package com.csse3200.game.progression.statistics;

import com.csse3200.game.persistence.Persistence;
import java.util.Map;

/**
 * The Statistics class tracks and stores global player/user statistics across the game. It records
 * cumulative data such as total kills, total shots, player level, number of plants unlocked, and
 * all-time total coins earned.
 *
 * <p>Statistics are initialised within and linked to a particular Profile. Statistics are relevant
 * to unlocking Achievements.
 *
 * <p>Statistics can be initialised with default or variable starting statistics.
 */
public class Statistics {
  int kills;
  int shotsFired;
  int levelsPassed;
  int numDefencesUnlocked;
  int totalCoinsEarned;
  private static final String KILLS_ACHIEVEMENT = "50_KILLS";
  private static final String SHOTS_FIRED_ACHIEVEMENT = "50_SHOTS";
  private static final String LEVELS_PASSED_ACHIEVEMENT = "LEVEL_1_COMPLETE";
  private static final String NUM_DEFENCES_UNLOCKED_ACHIEVEMENT = "5_DEFENSES";
  private static final String TOTAL_COINS_EARNED_ACHIEVEMENT = "100_COINS";

  // TODO: Remove once achievement progression is implemented
  private static final Map<String, Integer> ACHIEVEMENT_QUOTAS =
      Map.of(
          KILLS_ACHIEVEMENT, 50,
          SHOTS_FIRED_ACHIEVEMENT, 200,
          LEVELS_PASSED_ACHIEVEMENT, 1,
          NUM_DEFENCES_UNLOCKED_ACHIEVEMENT, 5,
          TOTAL_COINS_EARNED_ACHIEVEMENT, 100);

  /** Default constructor for Statistics. */
  public Statistics() {
    this.kills = 0;
    this.shotsFired = 0;
    this.levelsPassed = 0;
    this.numDefencesUnlocked = 2; // default value
    this.totalCoinsEarned = 100; // all-time total of coins earned
  }

  /**
   * Creates a Statistics instance with specified starting statistics.
   *
   * @param kills the initial number of kills
   * @param shotsFired the initial number of shots fired
   * @param levelsPassed the initial number of levels passed
   * @param numDefencesUnlocked the initial number of defences unlocked
   * @param totalCoinsEarned the initial number of coins earned
   */
  public Statistics(
      int kills, int shotsFired, int levelsPassed, int numDefencesUnlocked, int totalCoinsEarned) {
    this.kills = kills;
    this.shotsFired = shotsFired;
    this.levelsPassed = levelsPassed;
    this.numDefencesUnlocked = numDefencesUnlocked;
    this.totalCoinsEarned = totalCoinsEarned;
  }

  /**
   * Gets the current number of kills player (through their defences) has made.
   *
   * @return the current number of kills
   */
  public int getKills() {
    return kills;
  }

  /**
   * Gets the current number of shots player (through their defences) has fired.
   *
   * @return the current number of shots fired
   */
  public int getShotsFired() {
    return shotsFired;
  }

  /**
   * Gets the current number of levels player has passed.
   *
   * @return the current number of levels passed
   */
  public int getLevelsPassed() {
    return levelsPassed;
  }

  /**
   * Gets the current number of defences a player has unlocked.
   *
   * @return the current number of defences unlocked
   */
  public int getNumDefencesUnlocked() {
    return numDefencesUnlocked;
  }

  /**
   * Gets the current all-time total coins a player has earned.
   *
   * @return the current total coins earned
   */
  public int getTotalCoinsEarned() {
    return totalCoinsEarned;
  }

  /**
   * Sets the current number of kills player (through their defences) has made.
   *
   * @param kills the new number of kills
   */
  public void setKills(int kills) {
    if (kills >= 0) {
      this.kills = kills;
    }
  }

  /**
   * Sets the current number of shots player (through their defences) has fired.
   *
   * @param shotsFired the new number of shots fired
   */
  public void setShotsFired(int shotsFired) {
    if (shotsFired >= 0) {
      this.shotsFired = shotsFired;
    }
  }

  /**
   * Sets the current number of levels player has passed.
   *
   * @param levelsPassed the new number of levels passed
   */
  public void setLevelsPassed(int levelsPassed) {
    if (levelsPassed >= 0) {
      this.levelsPassed = levelsPassed;
    }
  }

  /**
   * Sets the current number of defences player has unlocked.
   *
   * @param numDefencesUnlocked the new number of kills
   */
  public void setNumDefencesUnlocked(int numDefencesUnlocked) {
    if (numDefencesUnlocked >= 0) {
      this.numDefencesUnlocked = numDefencesUnlocked;
    }
  }

  /**
   * Sets the current number of all-time total coins player has earned.
   *
   * @param totalCoinsEarned the new number of total coins earned
   */
  public void setTotalCoinsEarned(int totalCoinsEarned) {
    if (totalCoinsEarned >= 0) {
      this.totalCoinsEarned = totalCoinsEarned;
    }
  }

  /** Increases kills by 1. */
  public void increaseKills() {
    this.kills++;
    if (this.kills >= ACHIEVEMENT_QUOTAS.get(KILLS_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(KILLS_ACHIEVEMENT);
    }
  }

  /** Increases kills by 1. */
  public void increaseShotsFired() {
    this.shotsFired++;
    if (!Persistence.profile().achievements().isUnlocked(SHOTS_FIRED_ACHIEVEMENT)
        && this.shotsFired >= ACHIEVEMENT_QUOTAS.get(SHOTS_FIRED_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(SHOTS_FIRED_ACHIEVEMENT);
    }
  }

  /** Increases levels passed by 1. */
  public void increaseLevelsPassed() {
    this.levelsPassed++;
    if (!Persistence.profile().achievements().isUnlocked(LEVELS_PASSED_ACHIEVEMENT)
        && this.levelsPassed >= ACHIEVEMENT_QUOTAS.get(LEVELS_PASSED_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(LEVELS_PASSED_ACHIEVEMENT);
    }
  }

  /** Increases number of defences unlocked by 1. */
  public void increaseNumDefencesUnlocked() {
    this.numDefencesUnlocked++;
    if (!Persistence.profile().achievements().isUnlocked(NUM_DEFENCES_UNLOCKED_ACHIEVEMENT)
        && this.numDefencesUnlocked >= ACHIEVEMENT_QUOTAS.get(NUM_DEFENCES_UNLOCKED_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(NUM_DEFENCES_UNLOCKED_ACHIEVEMENT);
    }
  }

  /**
   * Increases number of defences unlocked by a specific number of defences.
   *
   * @param extraDefences additional number of defences unlocked
   */
  public void increaseNumDefencesUnlockedBySpecific(int extraDefences) {
    this.numDefencesUnlocked += extraDefences;
    if (!Persistence.profile().achievements().isUnlocked(NUM_DEFENCES_UNLOCKED_ACHIEVEMENT)
        && this.numDefencesUnlocked >= ACHIEVEMENT_QUOTAS.get(NUM_DEFENCES_UNLOCKED_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(NUM_DEFENCES_UNLOCKED_ACHIEVEMENT);
    }
  }

  /** Increases all-time total coins earned by 1. */
  public void increaseTotalCoinsEarned() {
    this.totalCoinsEarned++;
    if (!Persistence.profile().achievements().isUnlocked(TOTAL_COINS_EARNED_ACHIEVEMENT)
        && this.totalCoinsEarned >= ACHIEVEMENT_QUOTAS.get(TOTAL_COINS_EARNED_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(TOTAL_COINS_EARNED_ACHIEVEMENT);
    }
  }

  /**
   * Increases all-time total coins earned by a specific number of coins.
   *
   * @param extraCoinsEarned additional number of coins earned
   */
  public void increaseTotalCoinsEarnedBySpecific(int extraCoinsEarned) {
    this.totalCoinsEarned += extraCoinsEarned;
    if (!Persistence.profile().achievements().isUnlocked(TOTAL_COINS_EARNED_ACHIEVEMENT)
        && this.totalCoinsEarned >= ACHIEVEMENT_QUOTAS.get(TOTAL_COINS_EARNED_ACHIEVEMENT)) {
      Persistence.profile().achievements().unlock(TOTAL_COINS_EARNED_ACHIEVEMENT);
    }
  }
}
