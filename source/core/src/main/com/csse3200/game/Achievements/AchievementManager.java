package com.csse3200.game.Achievements;
import com.csse3200.game.services.ServiceLocator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The AchievementManager class is responsible for storing all achievements.
 */

public class AchievementManager {
    private final Map<String, Achievement> achievements = new HashMap<>();

    /**
     * Registers the achievements.
     */
    public AchievementManager() {
        // Register all achievements here
        //at the target file do:
        // ServiceLocator.registerAchievementManager(new AchievementManager());
        // then ServiceLocator.getAchievementManager().unlock("(first parameter as the ID)");
        achievements.put("100_COINS",
                new Achievement("100 COINS", "Earned 100 coins", 5));
        achievements.put("50_KILLS",
                new Achievement("50 KILLS", "Earned 50 kills", 10));
        achievements.put("LEVEL_1_COMPLETE",
                new Achievement("LEVEL 1 COMPLETE", "Completed the first level", 10));
        achievements.put("50_SHOTS",
                new Achievement("50 SHOTS", "You fired 50 shots.", 5));
        achievements.put("5_DEFENSES",
                new Achievement("5 DEFENSES", "Unlocked 5 defenses", 5));
    }

    /*public void addAchievement(Achievement a) {
        achievements.put(a.getName(), a);
    }
     */

    /** unlocks the Achievement through its provided name.
     *
     * @param name        achievement name
     */
    public void unlock(String name) {
        Achievement a = achievements.get(name);
        if (a != null && !a.isUnlocked()) {
            a.unlock();

        }
    }

    /** Checks if an achievement is locked/unlocked. Returns true if unlocked, false otherwise.
     *
     * @param name        achievement name
     */
    public boolean isUnlocked(String name) {
        Achievement a = achievements.get(name);
        return a != null && a.isUnlocked();
    }

    /** Returns all achievements.
     *
     */
    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }

    /** Checks the values of the Statistics class.
     *
     * @param stats        stat class
     */
    /*
    public void checkStatsAchievements(Statistics stats) {
        if (stats.getTotalCoinsEarned() >= 100) {
            unlock("100_COINS");
        }
        if (stats.getKills() >= 50) {
            unlock("50_KILLS");
        }
        if (stats.getLevelsPassed() >= 1) {
            unlock("LEVEL_1_COMPLETE");
        }
        if (stats.getShotsFired() >= 50) {
            unlock("50_SHOTS");
        }
        if (stats.getNumDefencesUnlocked() >= 5)
        {
            unlock("5_DEFENSES");
        }

     */
    }




