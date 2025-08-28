package com.csse3200.game.components.statistics;

import com.csse3200.game.GdxGame;

public class Statistics {
    int kills;
    int shotsFired;
    int levelsPassed;
    int numDefencesUnlocked;
    int totalCoinsEarned;

    public Statistics() {
        this.kills = 0;
        this.shotsFired = 0;
        this.levelsPassed = 0;
        this.numDefencesUnlocked = 10; //temp
        this.totalCoinsEarned = 100;
    }

    public Statistics(int kills, int shotsFired, int levelsPassed, int numDefencesUnlocked, int totalCoinsEarned) {
        this.kills = kills;
        this.shotsFired = shotsFired;
        this.levelsPassed = levelsPassed;
        this.numDefencesUnlocked = numDefencesUnlocked;
        this.totalCoinsEarned = totalCoinsEarned;
    }

    public int getKills() {
        return kills;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getLevelsPassed() {
        return levelsPassed;
    }

    public int getNumDefencesUnlocked() {
        return numDefencesUnlocked;
    }

    public int getTotalCoinsEarned() {
        return totalCoinsEarned;
    }

    public void setKills(int kills) {
        if (kills >= 0) {
            this.kills = kills;
        }
    }

    public void setShotsFired(int shotsFired) {
        if (shotsFired >= 0) {
            this.shotsFired = shotsFired;
        }
    }

    public void setLevelsPassed (int levelsPassed) {
        if (levelsPassed >= 0) {
            this.levelsPassed = levelsPassed;
        }
    }

    public void setNumDefencesUnlocked(int numPlantsUnlocked) {
        if (numPlantsUnlocked >= 0) {
            this.numDefencesUnlocked = numPlantsUnlocked;
        }
    }

    public void setTotalCoinsEarned(int totalCoinsEarned) {
        if (totalCoinsEarned >= 0) {
            this.totalCoinsEarned = totalCoinsEarned;
        }
    }

    public void increaseKills() {
        this.kills++;
    }

    public void increaseShotsFired() {
        this.shotsFired++;
    }

    public void increaseLevelsPassed() {
        this.levelsPassed++;
    }

    public void increaseNumDefencesUnlocked() {
        this.numDefencesUnlocked++;
    }

    public void increaseNumDefencesUnlockedBySpecific(int extraDefences) {
        this.numDefencesUnlocked += extraDefences;
    }

    public void increaseTotalCoinsEarned() {
        this.totalCoinsEarned++;
    }

    public void increaseTotalCoinsEarnedBySpecific(int extraCoinsEarned) {
        this.totalCoinsEarned += extraCoinsEarned;
    }
}
