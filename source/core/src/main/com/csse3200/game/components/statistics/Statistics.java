package com.csse3200.game.components.statistics;

public class Statistics {
    int kills;
    int shotsFired;
    int levelsPassed;
    int numDefencesUnlocked;

    public Statistics() {
        this.kills = 0;
        this.shotsFired = 0;
        this.levelsPassed = 0;
        this.numDefencesUnlocked = 10;
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

    public int getDefencesUnlocked() {
        return numDefencesUnlocked;
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

    public void setNumPlantsUnlocked(int numPlantsUnlocked) {
        if (numPlantsUnlocked >= 0) {
            this.numDefencesUnlocked = numPlantsUnlocked;
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

    public void increaseNumPlantsUnlocked() {
        this.numDefencesUnlocked++;
    }

}
