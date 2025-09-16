package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/**
 * Base configuration class for achievements. Defines the structure for achievement definitions that
 * are loaded from JSON config files.
 */
public class BaseAchievementConfig {
  private String name;
  private String description;
  private String statistic;
  private int quota;
  private int skillPoints;
  private String tier;

  /** Default constructor for JSON deserialization. */
  public BaseAchievementConfig() {
    // Empty constructor required for JSON deserialization
  }

  /**
   * Gets the display name of the achievement.
   *
   * @return the achievement name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the display name of the achievement.
   *
   * @param name the achievement name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description of the achievement.
   *
   * @return the achievement description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the achievement.
   *
   * @param description the achievement description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the statistic that this achievement tracks.
   *
   * @return the statistic name
   */
  public String getStatistic() {
    return statistic;
  }

  /**
   * Sets the statistic that this achievement tracks.
   *
   * @param statistic the statistic name
   */
  public void setStatistic(String statistic) {
    this.statistic = statistic;
  }

  /**
   * Gets the quota required to unlock this achievement.
   *
   * @return the quota value
   */
  public int getQuota() {
    return quota;
  }

  /**
   * Sets the quota required to unlock this achievement.
   *
   * @param quota the quota value
   */
  public void setQuota(int quota) {
    this.quota = quota;
  }

  /**
   * Gets the skill points awarded when this achievement is unlocked.
   *
   * @return the skill points
   */
  public int getSkillPoints() {
    return skillPoints;
  }

  /**
   * Sets the skill points awarded when this achievement is unlocked.
   *
   * @param skillPoints the skill points
   */
  public void setSkillPoints(int skillPoints) {
    this.skillPoints = skillPoints;
  }

  /**
   * Gets the tier of this achievement.
   *
   * @return the tier (T1, T2, T3)
   */
  public String getTier() {
    return tier;
  }

  /**
   * Sets the tier of this achievement.
   *
   * @param tier the tier (T1, T2, T3)
   */
  public void setTier(String tier) {
    this.tier = tier;
  }

  @Override
  public String toString() {
    return String.format(
        "Achievement{name='%s', statistic='%s', quota=%d, tier='%s'}",
        name, statistic, quota, tier);
  }

  /** DeserializedAchievementConfig is a wrapper class for the BaseAchievementConfig class. */
  public static class DeserializedAchievementConfig {
    private HashMap<String, BaseAchievementConfig> config;

    /** Creates a new DeserializedAchievementConfig. */
    public DeserializedAchievementConfig() {
      this.config = new HashMap<>();
    }

    /**
     * Sets the config map for the achievement configs.
     *
     * @param config the config map for the achievement configs
     */
    public void setConfig(Map<String, BaseAchievementConfig> config) {
      this.config = new HashMap<>(config);
    }

    /**
     * Gets the config map for the achievement configs.
     *
     * @return the config map for the achievement configs
     */
    public Map<String, BaseAchievementConfig> getConfig() {
      return config;
    }
  }
}
