package com.csse3200.game.components.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;

@ExtendWith(GameExtension.class)
class StatisticsTest {
  private Profile profile;

  @BeforeEach
  void setUp() {
    profile = new Profile();
  }

  @Test
  void testDefaultStatisticsKills() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      int kills = 0;
      assertEquals(kills, profile.statistics().getKills());
      profile.statistics().setKills(5);
      kills = 5;
      assertEquals(kills, profile.statistics().getKills());
      profile.statistics().increaseKills();
      kills = 6;
      assertEquals(kills, profile.statistics().getKills());
    }
  }

  @Test
  void testVariableStatisticsKills() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      profile.statistics().setKills(1);
      int kills = 1;
      assertEquals(kills, profile.statistics().getKills());
      profile.statistics().setKills(5);
      kills = 5;
      assertEquals(kills, profile.statistics().getKills());
      profile.statistics().increaseKills();
      kills = 6;
      assertEquals(kills, profile.statistics().getKills());
    }
  }

  @Test
  void testDefaultStatisticsShots() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      int shots = 0;
      assertEquals(shots, profile.statistics().getShotsFired());
      profile.statistics().setShotsFired(5);
      shots = 5;
      assertEquals(shots, profile.statistics().getShotsFired());
      profile.statistics().increaseShotsFired();
      shots = 6;
      assertEquals(shots, profile.statistics().getShotsFired());
    }
  }

  @Test
  void testVariableStatisticsShots() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      profile.statistics().setShotsFired(2);
      int shots = 2;
      assertEquals(shots, profile.statistics().getShotsFired());
      profile.statistics().setShotsFired(5);
      shots = 5;
      assertEquals(shots, profile.statistics().getShotsFired());
      profile.statistics().increaseShotsFired();
      shots = 6;
      assertEquals(shots, profile.statistics().getShotsFired());
    }
  }

  @Test
  void testDefaultStatisticsLevels() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      int levels = 0;
      assertEquals(levels, profile.statistics().getLevelsPassed());
      profile.statistics().setLevelsPassed(5);
      levels = 5;
      assertEquals(levels, profile.statistics().getLevelsPassed());
      profile.statistics().increaseLevelsPassed();
      levels = 6;
      assertEquals(levels, profile.statistics().getLevelsPassed());
    }
  }

  @Test
  void testVariableStatisticsLevels() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      profile.statistics().setLevelsPassed(3);
      int levels = 3;
      assertEquals(levels, profile.statistics().getLevelsPassed());
      profile.statistics().setLevelsPassed(5);
      levels = 5;
      assertEquals(levels, profile.statistics().getLevelsPassed());
      profile.statistics().increaseLevelsPassed();
      levels = 6;
      assertEquals(levels, profile.statistics().getLevelsPassed());
    }
  }

  @Test
  void testDefaultStatisticsDefences() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      int defences = 2;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
      profile.statistics().setNumDefencesUnlocked(15);
      defences = 15;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
      profile.statistics().increaseNumDefencesUnlocked();
      defences = 16;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
      profile.statistics().increaseNumDefencesUnlockedBySpecific(5);
      defences = 21;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
    }
  }

  @Test
  void testVariableStatisticsDefences() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      profile.statistics().setNumDefencesUnlocked(4);
      int defences = 4;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
      profile.statistics().setNumDefencesUnlocked(15);
      defences = 15;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
      profile.statistics().increaseNumDefencesUnlocked();
      defences = 16;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
      profile.statistics().increaseNumDefencesUnlockedBySpecific(5);
      defences = 21;
      assertEquals(defences, profile.statistics().getNumDefencesUnlocked());
    }
  }

  @Test
  void testDefaultStatisticsCoins() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      int coins = 100;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
      profile.statistics().setTotalCoinsEarned(150);
      coins = 150;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
      profile.statistics().increaseTotalCoinsEarned();
      coins = 151;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
      profile.statistics().increaseTotalCoinsEarnedBySpecific(10);
      coins = 161;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
    }
  }

  @Test
  void testVariableStatisticsCoins() {
    try (MockedStatic<Persistence> mockedPersistence = mockStatic(Persistence.class)) {
      mockedPersistence.when(Persistence::profile).thenReturn(profile);

      profile.statistics().setTotalCoinsEarned(5);
      int coins = 5;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
      profile.statistics().setTotalCoinsEarned(15);
      coins = 15;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
      profile.statistics().increaseTotalCoinsEarned();
      coins = 16;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
      profile.statistics().increaseTotalCoinsEarnedBySpecific(10);
      coins = 26;
      assertEquals(coins, profile.statistics().getTotalCoinsEarned());
    }
  }
}
