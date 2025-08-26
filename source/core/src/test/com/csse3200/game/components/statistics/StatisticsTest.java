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
}
