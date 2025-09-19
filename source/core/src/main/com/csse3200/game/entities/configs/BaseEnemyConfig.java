package com.csse3200.game.entities.configs;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class representing the stats defining an enemy, including combat stats, movement
 * speed,
 */
public class BaseEnemyConfig extends BaseEntityConfig {
  private int attack;
  private float movementSpeed;
  private float teleportCooldownSeconds;
  private float teleportChance;
  private int maxTeleports;
  private int invulnerabilityMs;

  /**
   * The enemy's size NOTE: THIS DOESN'T WORK because LevelGameArea rescales the enemy to match the
   * tile height.
   */
  private float scale; // so why is this here??

  /** Creates a new BaseEntityConfig with default values. */
  public BaseEnemyConfig() {
    // Default constructor
  }

  /**
   * Gets the attack value for this entity.
   *
   * @return the attack value
   */
  public int getAttack() {
    return attack;
  }

  /**
   * Gets the movement speed value for this entity.
   *
   * @return the movement speed value
   */
  public float getMovementSpeed() {
    return movementSpeed;
  }

  /**
   * Checks if this entity is a teleport robot.
   *
   * @return true if this entity is a teleport robot, false otherwise
   */
  public boolean isTeleportRobot() {
    return maxTeleports > 0;
  }

  /**
   * Gets the teleport cooldown seconds value for this entity.
   *
   * @return the teleport cooldown seconds value
   */
  public float getTeleportCooldownSeconds() {
    return teleportCooldownSeconds;
  }

  /**
   * Gets the teleport chance value for this entity.
   *
   * @return the teleport chance value
   */
  public float getTeleportChance() {
    return teleportChance;
  }

  /**
   * Gets the max teleports value for this entity.
   *
   * @return the max teleports value
   */
  public int getMaxTeleports() {
    return maxTeleports;
  }

  /**
   * Gets the invulnerability ms value for this entity.
   *
   * @return the invulnerability ms value
   */
  public int getInvulnerabilityMs() {
    return invulnerabilityMs;
  }

  /**
   * Gets the scale value for this entity.
   *
   * @return the scale value
   */
  public float getScale() {
    return scale;
  }

  /** DeserializedEnemyConfig is a wrapper class for the BaseEnemyConfig class. */
  public static class DeserializedEnemyConfig {
    private HashMap<String, BaseEnemyConfig> config;

    /** Creates a new DeserializedEnemyConfig. */
    public DeserializedEnemyConfig() {
      this.config = new HashMap<>();
    }

    /**
     * Sets the config map for the enemy configs.
     *
     * @param config the config map for the enemy configs
     */
    public void setConfig(Map<String, BaseEnemyConfig> config) {
      this.config = new HashMap<>(config);
    }

    /**
     * Gets the config map for the enemy configs.
     *
     * @return the config map for the enemy configs
     */
    public Map<String, BaseEnemyConfig> getConfig() {
      return config;
    }
  }
}
