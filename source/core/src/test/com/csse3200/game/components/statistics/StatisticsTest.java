package com.csse3200.game.components.statistics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsTest {

    @Test
    void testStatisticsKills() {
        Statistics statistics = new Statistics();
        int kills = 0;
        assertEquals(kills, statistics.getKills());
        statistics.setKills(5);
        kills = 5;
        assertEquals(kills, statistics.getKills());
        statistics.increaseKills();
        kills = 6;
        assertEquals(kills, statistics.getKills());
    }

    @Test
    void testStatistics2Kills() {
        Statistics statistics = new Statistics(1, 2, 3, 4, 5);
        int kills = 1;
        assertEquals(kills, statistics.getKills());
        statistics.setKills(5);
        kills = 5;
        assertEquals(kills, statistics.getKills());
        statistics.increaseKills();
        kills = 6;
        assertEquals(kills, statistics.getKills());
    }

    @Test
    void testStatisticsShots() {
        Statistics statistics = new Statistics();
        int shots = 0;
        assertEquals(shots, statistics.getShotsFired());
        statistics.setShotsFired(5);
        shots = 5;
        assertEquals(shots, statistics.getShotsFired());
        statistics.increaseShotsFired();
        shots = 6;
        assertEquals(shots, statistics.getShotsFired());
    }

    @Test
    void testStatistics2Shots() {
        Statistics statistics = new Statistics(1, 2, 3, 4, 5);
        int shots = 2;
        assertEquals(shots, statistics.getShotsFired());
        statistics.setShotsFired(5);
        shots = 5;
        assertEquals(shots, statistics.getShotsFired());
        statistics.increaseShotsFired();
        shots = 6;
        assertEquals(shots, statistics.getShotsFired());
    }

    @Test
    void testStatisticsLevels() {
        Statistics statistics = new Statistics();
        int levels = 0;
        assertEquals(levels, statistics.getLevelsPassed());
        statistics.setLevelsPassed(5);
        levels = 5;
        assertEquals(levels, statistics.getLevelsPassed());
        statistics.increaseLevelsPassed();
        levels = 6;
        assertEquals(levels, statistics.getLevelsPassed());
    }

    @Test
    void testStatistics2Levels() {
        Statistics statistics = new Statistics(1, 2, 3, 4, 5);
        int levels = 3;
        assertEquals(levels, statistics.getLevelsPassed());
        statistics.setLevelsPassed(5);
        levels = 5;
        assertEquals(levels, statistics.getLevelsPassed());
        statistics.increaseLevelsPassed();
        levels = 6;
        assertEquals(levels, statistics.getLevelsPassed());
    }

    @Test
    void testStatisticsDefences() {
        Statistics statistics = new Statistics();
        int defences = 10;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
        statistics.setNumDefencesUnlocked(15);
        defences = 15;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
        statistics.increaseNumDefencesUnlocked();
        defences = 16;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
        statistics.increaseNumDefencesUnlockedBySpecific(5);
        defences = 21;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
    }

    @Test
    void testStatistics2Defences() {
        Statistics statistics = new Statistics(1, 2, 3, 4, 5);
        int defences = 4;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
        statistics.setNumDefencesUnlocked(15);
        defences = 15;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
        statistics.increaseNumDefencesUnlocked();
        defences = 16;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
        statistics.increaseNumDefencesUnlockedBySpecific(5);
        defences = 21;
        assertEquals(defences, statistics.getNumDefencesUnlocked());
    }

    @Test
    void testStatisticsCoins() {
        Statistics statistics = new Statistics();
        int coins = 100;
        assertEquals(coins, statistics.getTotalCoinsEarned());
        statistics.setTotalCoinsEarned(150);
        coins = 150;
        assertEquals(coins, statistics.getTotalCoinsEarned());
        statistics.increaseTotalCoinsEarned();
        coins = 151;
        assertEquals(coins, statistics.getTotalCoinsEarned());
        statistics.increaseTotalCoinsEarnedBySpecific(10);
        coins = 161;
        assertEquals(coins, statistics.getTotalCoinsEarned());
    }

    @Test
    void testStatistics2Coins() {
        Statistics statistics = new Statistics(1, 2, 3, 4, 5);
        int coins = 5;
        assertEquals(coins, statistics.getTotalCoinsEarned());
        statistics.setTotalCoinsEarned(15);
        coins = 15;
        assertEquals(coins, statistics.getTotalCoinsEarned());
        statistics.increaseTotalCoinsEarned();
        coins = 16;
        assertEquals(coins, statistics.getTotalCoinsEarned());
        statistics.increaseTotalCoinsEarnedBySpecific(10);
        coins = 26;
        assertEquals(coins, statistics.getTotalCoinsEarned());
    }
}
