package com.csse3200.game.components.statistics;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(GameExtension.class)
public class StatisticsTest {

    @Test
    void testDefaultStatisticsKills() {
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
    void testVariableStatisticsKills() {
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
    void testDefaultStatisticsShots() {
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
    void testVariableStatisticsShots() {
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
    void testDefaultStatisticsLevels() {
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
    void testVariableStatisticsLevels() {
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
    void testDefaultStatisticsDefences() {
        Statistics statistics = new Statistics();
        int defences = 2;
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
    void testVariableStatisticsDefences() {
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
    void testDefaultStatisticsCoins() {
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
    void testVariableStatisticsCoins() {
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
