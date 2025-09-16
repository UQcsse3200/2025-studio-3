package com.csse3200.game.progression.statistics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.configs.BaseAchievementConfig;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@ExtendWith(GameExtension.class)
class StatisticsTest {

  @Mock private ConfigService mockConfigService;

  private Statistics statistics;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ServiceLocator.registerConfigService(mockConfigService);
    statistics = new Statistics();
  }

  @Test
  void shouldCreateDefaultStatistics() {
    Statistics stats = new Statistics();

    // Verify default statistics are initialized
    assertEquals(0, stats.getStatistic("enemiesKilled"));
    assertEquals(0, stats.getStatistic("shotsFired"));
    assertEquals(0, stats.getStatistic("levelsCompleted"));
    assertEquals(0, stats.getStatistic("defensesPurchased"));
    assertEquals(30, stats.getStatistic("coinsCollected"));
    assertEquals(0, stats.getStatistic("coinsSpent"));
    assertEquals(1, stats.getStatistic("skillPointsCollected"));
    assertEquals(0, stats.getStatistic("skillPointsSpent"));
    assertEquals(0, stats.getStatistic("purchasesMade"));
    assertEquals(0, stats.getStatistic("wavesCompleted"));
    assertEquals(0, stats.getStatistic("itemsCollected"));

    // Verify empty achievements list
    assertTrue(stats.getUnlockedAchievements().isEmpty());
  }

  @Test
  void shouldCreateStatisticsWithProvidedData() {
    Map<String, Integer> stats = new HashMap<>();
    stats.put("enemiesKilled", 10);
    stats.put("shotsFired", 50);

    List<String> achievements = new ArrayList<>();
    achievements.add("firstKill");
    achievements.add("sharpshooter");

    Statistics statisticsNew = new Statistics(stats, achievements);

    assertEquals(10, statisticsNew.getStatistic("enemiesKilled"));
    assertEquals(50, statisticsNew.getStatistic("shotsFired"));
    assertEquals(30, statisticsNew.getStatistic("coinsCollected"));

    assertEquals(2, statisticsNew.getUnlockedAchievements().size());
    assertTrue(statisticsNew.isAchievementUnlocked("firstKill"));
    assertTrue(statisticsNew.isAchievementUnlocked("sharpshooter"));
  }

  @Test
  void shouldHandleNullInputsInConstructor() {
    Statistics statisticsNew = new Statistics(null, null);

    // Should still initialize defaults
    assertEquals(30, statisticsNew.getStatistic("coinsCollected"));
    assertTrue(statisticsNew.getUnlockedAchievements().isEmpty());
  }

  @Test
  void shouldGetStatisticValue() {
    statistics.setStatistic("enemiesKilled", 15);

    assertEquals(15, statistics.getStatistic("enemiesKilled"));
    assertEquals(0, statistics.getStatistic("nonExistentStat"));
  }

  @Test
  void shouldGetAllStatistics() {
    statistics.setStatistic("enemiesKilled", 10);
    statistics.setStatistic("shotsFired", 25);

    Map<String, Integer> allStats = statistics.getAllStatistics();

    assertEquals(10, allStats.get("enemiesKilled"));
    assertEquals(25, allStats.get("shotsFired"));
    assertEquals(30, allStats.get("coinsCollected")); // default

    // Should return a copy (mutations shouldn't affect original)
    allStats.put("enemiesKilled", 999);
    assertEquals(10, statistics.getStatistic("enemiesKilled"));
  }

  @Test
  void shouldGetUnlockedAchievementsCopy() {
    // Mock achievement unlock
    when(mockConfigService.getAchievementConfigs()).thenReturn(createMockAchievementConfigs());

    statistics.setStatistic("enemiesKilled", 5); // Should unlock achievement

    List<String> achievements = statistics.getUnlockedAchievements();
    int originalSize = achievements.size();

    // Modify the returned list
    achievements.add("fakeAchievement");

    // Original should be unchanged
    assertEquals(originalSize, statistics.getUnlockedAchievements().size());
  }

  @Test
  void shouldCheckAchievementUnlockStatus() {
    // Mock achievement unlock
    when(mockConfigService.getAchievementConfigs()).thenReturn(createMockAchievementConfigs());

    assertFalse(statistics.isAchievementUnlocked("killStreak"));

    statistics.setStatistic("enemiesKilled", 5); // Should unlock achievement

    assertTrue(statistics.isAchievementUnlocked("killStreak"));
  }

  @Test
  void shouldSetStatisticValue() {
    statistics.setStatistic("enemiesKilled", 20);
    assertEquals(20, statistics.getStatistic("enemiesKilled"));

    // Setting to same value should work
    statistics.setStatistic("enemiesKilled", 20);
    assertEquals(20, statistics.getStatistic("enemiesKilled"));

    // Setting to lower value should work but not trigger achievements
    statistics.setStatistic("enemiesKilled", 15);
    assertEquals(15, statistics.getStatistic("enemiesKilled"));
  }

  @Test
  void shouldNotSetNegativeStatistic() {
    statistics.setStatistic("enemiesKilled", 10);
    statistics.setStatistic("enemiesKilled", -5);

    // Should remain unchanged
    assertEquals(10, statistics.getStatistic("enemiesKilled"));
  }

  @Test
  void shouldIncrementStatisticByOne() {
    statistics.incrementStatistic("enemiesKilled");
    assertEquals(1, statistics.getStatistic("enemiesKilled"));

    statistics.incrementStatistic("enemiesKilled");
    assertEquals(2, statistics.getStatistic("enemiesKilled"));
  }

  @Test
  void shouldIncrementStatisticByAmount() {
    statistics.incrementStatistic("shotsFired", 10);
    assertEquals(10, statistics.getStatistic("shotsFired"));

    statistics.incrementStatistic("shotsFired", 5);
    assertEquals(15, statistics.getStatistic("shotsFired"));
  }

  @Test
  void shouldNotIncrementByZeroOrNegative() {
    statistics.setStatistic("enemiesKilled", 5);

    statistics.incrementStatistic("enemiesKilled", 0);
    assertEquals(5, statistics.getStatistic("enemiesKilled"));

    statistics.incrementStatistic("enemiesKilled", -3);
    assertEquals(5, statistics.getStatistic("enemiesKilled"));
  }

  @Test
  void shouldTriggerAchievementUnlockOnIncrementStatistic() {
    when(mockConfigService.getAchievementConfigs()).thenReturn(createMockAchievementConfigs());

    assertFalse(statistics.isAchievementUnlocked("killStreak"));

    // Increment to achievement threshold
    statistics.incrementStatistic("enemiesKilled", 5);

    assertTrue(statistics.isAchievementUnlocked("killStreak"));
  }

  @Test
  void shouldNotUnlockAchievementWhenDecreasingStatistic() {
    when(mockConfigService.getAchievementConfigs()).thenReturn(createMockAchievementConfigs());

    // Set above threshold to unlock achievement
    statistics.setStatistic("enemiesKilled", 10);
    assertTrue(statistics.isAchievementUnlocked("killStreak"));

    // Test that decreasing the statistic doesn't unlock achievement in a new instance
    Statistics newStats = new Statistics();
    // First set to a value that doesn't trigger achievement (3 < 5)
    newStats.setStatistic("enemiesKilled", 3);
    // Should not have unlocked achievement since value is below threshold
    assertFalse(newStats.isAchievementUnlocked("killStreak"));
  }

  @Test
  void shouldNotUnlockSameAchievementTwice() {
    when(mockConfigService.getAchievementConfigs()).thenReturn(createMockAchievementConfigs());

    // Unlock achievement first time
    statistics.setStatistic("enemiesKilled", 5);
    assertTrue(statistics.isAchievementUnlocked("killStreak"));
    assertEquals(1, statistics.getUnlockedAchievements().size());

    // Try to unlock again
    statistics.setStatistic("enemiesKilled", 10);
    assertTrue(statistics.isAchievementUnlocked("killStreak"));
    assertEquals(1, statistics.getUnlockedAchievements().size()); // Should still be 1
  }

  @Test
  void shouldHandleNullConfigService() {
    ServiceLocator.registerConfigService(null);

    // Should not crash when trying to check achievements
    statistics.setStatistic("enemiesKilled", 100);

    // No achievements should be unlocked
    assertTrue(statistics.getUnlockedAchievements().isEmpty());
  }

  @Test
  void shouldHandleNullAchievementConfigs() {
    when(mockConfigService.getAchievementConfigs()).thenReturn(null);

    // Should not crash when achievement configs are null
    statistics.setStatistic("enemiesKilled", 100);

    // No achievements should be unlocked
    assertTrue(statistics.getUnlockedAchievements().isEmpty());
  }

  @Test
  void shouldHandleEmptyAchievementConfigs() {
    when(mockConfigService.getAchievementConfigs()).thenReturn(new HashMap<>());

    // Should not crash when achievement configs are empty
    statistics.setStatistic("enemiesKilled", 100);

    // No achievements should be unlocked
    assertTrue(statistics.getUnlockedAchievements().isEmpty());
  }

  @Test
  void shouldUnlockMultipleAchievementsForSameStatistic() {
    Map<String, BaseAchievementConfig> configs = new HashMap<>();

    // Create two achievements for the same statistic
    BaseAchievementConfig achievement1 = createMockAchievement("killStreak5", "enemiesKilled", 5);
    BaseAchievementConfig achievement2 = createMockAchievement("killStreak10", "enemiesKilled", 10);

    configs.put("killStreak5", achievement1);
    configs.put("killStreak10", achievement2);

    when(mockConfigService.getAchievementConfigs()).thenReturn(configs);

    // Set statistic to unlock both
    statistics.setStatistic("enemiesKilled", 15);

    assertTrue(statistics.isAchievementUnlocked("killStreak5"));
    assertTrue(statistics.isAchievementUnlocked("killStreak10"));
    assertEquals(2, statistics.getUnlockedAchievements().size());
  }

  @Test
  void shouldOnlyUnlockAchievementForMatchingStatistic() {
    Map<String, BaseAchievementConfig> configs = new HashMap<>();

    BaseAchievementConfig killAchievement = createMockAchievement("killStreak", "enemiesKilled", 5);
    BaseAchievementConfig shotAchievement = createMockAchievement("sharpshooter", "shotsFired", 10);

    configs.put("killStreak", killAchievement);
    configs.put("sharpshooter", shotAchievement);

    when(mockConfigService.getAchievementConfigs()).thenReturn(configs);

    // Only update enemies killed
    statistics.setStatistic("enemiesKilled", 10);

    assertTrue(statistics.isAchievementUnlocked("killStreak"));
    assertFalse(statistics.isAchievementUnlocked("sharpshooter"));
  }

  @Test
  void shouldHandleNullDialogService() {
    // Ensure dialog service is null
    ServiceLocator.clear();
    ServiceLocator.registerConfigService(mockConfigService);

    when(mockConfigService.getAchievementConfigs()).thenReturn(createMockAchievementConfigs());

    // Should not crash when dialog service is null
    statistics.setStatistic("enemiesKilled", 5);

    // Achievement should still be unlocked
    assertTrue(statistics.isAchievementUnlocked("killStreak"));
  }

  private Map<String, BaseAchievementConfig> createMockAchievementConfigs() {
    Map<String, BaseAchievementConfig> configs = new HashMap<>();
    BaseAchievementConfig achievement = createMockAchievement("killStreak", "enemiesKilled", 5);
    configs.put("killStreak", achievement);
    return configs;
  }

  private BaseAchievementConfig createMockAchievement(String name, String statistic, int quota) {
    BaseAchievementConfig achievement = new BaseAchievementConfig();
    achievement.setName(name);
    achievement.setDescription("Test achievement");
    achievement.setStatistic(statistic);
    achievement.setQuota(quota);
    achievement.setSkillPoints(10);
    achievement.setTier("T1");
    return achievement;
  }
}
