package com.csse3200.game.progression.statistics;

import com.badlogic.gdx.audio.Sound;
import com.csse3200.game.entities.configs.BaseAchievementConfig;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Statistics class tracks and stores player/user statistics across the game and manages
 * achievements based on those statistics. It records cumulative data such as total kills, total
 * shots, player level, number of plants unlocked, and all-time total coins earned.
 *
 * <p>Statistics are initialised within and linked to a particular Profile. When statistics are
 * updated, the class checks achievement quotas and unlocks achievements automatically.
 *
 * <p>Achievements are stored as a list of unlocked achievement keys, similar to how Arsenal and
 * Inventory work.
 */
public class Statistics {
  private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
  private Map<String, Integer> stats;
  private List<String> achievements;

  /** Default constructor for Statistics. */
  public Statistics() {
    this.stats = new HashMap<>();
    this.achievements = new ArrayList<>();
    parseStatistics();
  }

  /**
   * Creates a Statistics instance with specified statistics and achievements.
   *
   * @param stats the statistics map
   * @param achievements the list of unlocked achievement keys
   */
  public Statistics(Map<String, Integer> stats, List<String> achievements) {
    this.stats = stats != null ? new HashMap<>(stats) : new HashMap<>();
    this.achievements = achievements != null ? new ArrayList<>(achievements) : new ArrayList<>();
    parseStatistics();
  }

  /** Initializes default statistics if they don't exist. */
  private void parseStatistics() {
    stats.putIfAbsent("enemiesKilled", 0);
    stats.putIfAbsent("shotsFired", 0);
    stats.putIfAbsent("levelsCompleted", 0);
    stats.putIfAbsent("levelsLost", 0);
    stats.putIfAbsent("defencesPlanted", 0);
    stats.putIfAbsent("defencesUnlocked", 0);
    stats.putIfAbsent("defencesLost", 0);
    stats.putIfAbsent("coinsCollected", 30);
    stats.putIfAbsent("coinsSpent", 0);
    stats.putIfAbsent("skillPointsCollected", 1);
    stats.putIfAbsent("skillPointsSpent", 0);
    stats.putIfAbsent("purchasesMade", 0);
    stats.putIfAbsent("wavesCompleted", 0);
    stats.putIfAbsent("itemsCollected", 0);
  }

  /**
   * Gets the value of a specific statistic.
   *
   * @param key the name of the statistic
   * @return the value of the statistic, or 0 if not found
   */
  public int getStatistic(String key) {
    return stats.getOrDefault(key, 0);
  }

  /**
   * Gets all statistics.
   *
   * @return map of all statistics
   */
  public Map<String, Integer> getAllStatistics() {
    return new HashMap<>(stats);
  }

  /**
   * Gets the list of unlocked achievement keys.
   *
   * @return list of unlocked achievement keys
   */
  public List<String> getUnlockedAchievements() {
    return new ArrayList<>(achievements);
  }

  /**
   * Checks if an achievement is unlocked.
   *
   * @param achievementKey the achievement key
   * @return true if unlocked, false otherwise
   */
  public boolean isAchievementUnlocked(String achievementKey) {
    return achievements.contains(achievementKey);
  }

  /**
   * Updates a statistic and checks for achievement unlocks.
   *
   * @param key the name of the statistic to update
   * @param newValue the new value for the statistic
   */
  public void setStatistic(String key, int newValue) {
    if (newValue >= 0) {
      int oldValue = stats.getOrDefault(key, 0);
      stats.put(key, newValue);
      if (newValue > oldValue) {
        checkAchievements(key, newValue);
      }
    }
  }

  /**
   * Increments a statistic by 1 and checks for achievement unlocks.
   *
   * @param key the name of the statistic to increment
   */
  public void incrementStatistic(String key) {
    incrementStatistic(key, 1);
  }

  /**
   * Increments a statistic by a specific amount and checks for achievement unlocks.
   *
   * @param key the name of the statistic to increment
   * @param amount the amount to increment by
   */
  public void incrementStatistic(String key, int amount) {
    if (amount > 0) {
      int currentValue = stats.getOrDefault(key, 0);
      int newValue = currentValue + amount;
      stats.put(key, newValue);
      checkAchievements(key, newValue);
    }
  }

  /**
   * Checks if any achievements should be unlocked based on the updated statistic.
   *
   * @param key the name of the statistic that was updated
   * @param newValue the new value of the statistic
   */
  private void checkAchievements(String key, int newValue) {
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      return;
    }

    Map<String, BaseAchievementConfig> achievementConfigs = configService.getAchievementConfigs();
    if (achievementConfigs == null) {
      return;
    }

    for (Map.Entry<String, BaseAchievementConfig> entry : achievementConfigs.entrySet()) {
      String achievementKey = entry.getKey();
      BaseAchievementConfig config = entry.getValue();

      // Check if this achievement tracks the updated statistic
      if (key.equals(config.getStatistic())
          && !isAchievementUnlocked(achievementKey)
          && newValue >= config.getQuota()) {
        unlockAchievement(achievementKey, config);
      }
    }
  }

  /**
   * Unlocks an achievement and displays the popup.
   *
   * @param achievementKey the key of the achievement to unlock
   * @param config the achievement configuration
   */
  private void unlockAchievement(String achievementKey, BaseAchievementConfig config) {
    if (!achievements.contains(achievementKey)) {
      // load resource to play sound

        ResourceService resourceService = ServiceLocator.getResourceService();
        if (resourceService != null) {
            resourceService.loadSounds(new String[] {"sounds/achievementUnlock.mp3"});
            resourceService.loadAll();
            Sound unlock = resourceService.getAsset("sounds/achievementUnlock.mp3", Sound.class);
            if (unlock != null) {
                float volume = ServiceLocator.getSettingsService().getSoundVolume();
                unlock.play(volume);
            }
        } else {
            logger.warn("ResourceService not registered. Achievement sound not played.");
        }
      achievements.add(achievementKey);
      logger.info("Achievement unlocked: {} - {}", config.getName(), config.getDescription());
      ServiceLocator.getProfileService()
          .getProfile()
          .getWallet()
          .addSkillsPoints(config.getSkillPoints());
      incrementStatistic("skillPointsCollected", config.getSkillPoints());

      // Display achievement popup
      if (ServiceLocator.getDialogService() != null) {
        ServiceLocator.getDialogService()
            .achievement(
                config.getName(),
                config.getDescription(),
                config.getSkillPoints(),
                config.getTier());
      }
    }
  }
}
