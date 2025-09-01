package com.csse3200.game.components.statistics;

/**
 * The Statistics class tracks and stores global player/user statistics across the game. It records cumulative data
 * such as total kills, total shots, player level, number of plants unlocked, and all-time total coins earned.
 *
 * Statistics are initialised within and linked to a particular Profile. Statistics are relevant to unlocking
 * Achievements.
 *
 * Statistics can be initialised with default or variable starting statistics.
 */
public class Statistics {
    int kills;
    int shotsFired;
    int levelsPassed;
    int numDefencesUnlocked;
    int totalCoinsEarned;

    /**
     * Default constructor for Statistics.
     */
    public Statistics() {
        this.kills = 0;
        this.shotsFired = 0;
        this.levelsPassed = 0;
        this.numDefencesUnlocked = 10; // default value
        this.totalCoinsEarned = 100; // all-time total of coins earned
    }

    /** Creates a Statistics instance with specified starting statistics.
     *
     * @param kills        the initial number of kills
     * @param shotsFired   the initial number of shots fired
     * @param levelsPassed the initial number of levels passed
     * @param numDefencesUnlocked the initial number of defences unlocked
     * @param totalCoinsEarned the initial number of coins earned
     */
    public Statistics(int kills, int shotsFired, int levelsPassed, int numDefencesUnlocked, int totalCoinsEarned) {
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
    public void setLevelsPassed (int levelsPassed) {
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

    /**
     * Increases kills by 1.
     */
    public void increaseKills() {
        this.kills++;
    }

    /**
     * Increases kills by 1.
     */
    public void increaseShotsFired() {
        this.shotsFired++;
    }

    /**
     * Increases levels passed by 1.
     */
    public void increaseLevelsPassed() {
        this.levelsPassed++;
    }

    /**
     * Increases number of defences unlocked by 1.
     */
    public void increaseNumDefencesUnlocked() {
        this.numDefencesUnlocked++;
    }

    /**
     * Increases number of defences unlocked by a specific number of defences.
     *
     * @param extraDefences additional number of defences unlocked
     */
    public void increaseNumDefencesUnlockedBySpecific(int extraDefences) {
        this.numDefencesUnlocked += extraDefences;
    }

    /**
     * Increases all-time total coins earned by 1.
     */
    public void increaseTotalCoinsEarned() {
        this.totalCoinsEarned++;
    }

    /**
     * Increases all-time total coins earned by a specific number of coins.
     *
     * @param extraCoinsEarned additional number of coins earned
     */
    public void increaseTotalCoinsEarnedBySpecific(int extraCoinsEarned) {
        this.totalCoinsEarned += extraCoinsEarned;
    }
}
