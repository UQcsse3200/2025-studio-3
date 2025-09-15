package com.csse3200.game.progression;

import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.persistence.Savefile;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a user profile in the game. Allows customization of player attributes and tracking of
 * progress.
 *
 * <p>Later we can add an arsenal of unlocked defences, and a way to track overall level progress.
 */
public class Profile {
  private static final Logger logger = LoggerFactory.getLogger(Profile.class);
  private String name;
  private Wallet wallet; // The player's wallet (incl. coins & skill points)
  private Inventory inventory; // The player's inventory of items (not defences)
  private SkillSet skillset; // The player's skills / skill tree
  private Statistics statistics; // The player's statistics (includes achievements)
  private Arsenal arsenal; // The player's arsenal of unlocked defences
  private String currentLevel; // The player's current level

  /** Creates a new profile with default values. */
  public Profile() {
    this.name = Savefile.createName();
    this.wallet = new Wallet();
    this.inventory = new Inventory();
    this.skillset = new SkillSet();
    this.statistics = new Statistics();
    this.arsenal = new Arsenal();
    this.currentLevel = "level1"; // Default level, will be updated when levels are loaded
  }

  /** Initialise a profile with the provided values. */
  public Profile(
      String name,
      Wallet wallet,
      Inventory inventory,
      SkillSet skillset,
      Statistics statistics,
      Arsenal arsenal,
      String currentLevel) {
    this.name = name;
    this.wallet = wallet;
    this.inventory = inventory;
    this.skillset = skillset;
    this.statistics = statistics != null ? statistics : new Statistics();
    this.arsenal = arsenal;
    this.currentLevel = currentLevel;
  }

  /**
   * Get the name of the profile.
   *
   * @return the name of the profile.
   */
  public String getName() {
    return name;
  }

  /**
   * Change the name of the profile.
   *
   * @param name the new name of the profile.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the current level of the profile.
   *
   * @return the current level of the profile.
   */
  public String getCurrentLevel() {
    return currentLevel;
  }

  /**
   * Set the current level of the profile.
   *
   * @param currentLevel the new current level of the profile.
   */
  public void setCurrentLevel(String currentLevel) {
    this.currentLevel = currentLevel;
  }

  /**
   * Get the wallet associated with the profile.
   *
   * @return the wallet of the profile.
   */
  public Wallet wallet() {
    return wallet;
  }

  /**
   * Get the inventory associated with the profile.
   *
   * @return the inventory of the profile.
   */
  public Inventory inventory() {
    return inventory;
  }

  /**
   * Get the arsenal associated with the profile.
   *
   * @return the arsenal of the profile.
   */
  public Arsenal arsenal() {
    return arsenal;
  }

  /**
   * Add an item to the inventory.
   *
   * @param itemKey the key of the item to add.
   */
  public void addItemToInventory(String itemKey) {
    inventory.addItem(itemKey);
  }

  /**
   * Remove an item from the inventory.
   *
   * @param itemKey the key of the item to remove.
   */
  public void removeItemFromInventory(String itemKey) {
    inventory.removeItem(itemKey);
  }

  /**
   * Get the items in the inventory.
   *
   * @return the items in the inventory.
   */
  public Map<String, BaseItemConfig> getInventoryItems() {
    Map<String, BaseItemConfig> items = new HashMap<>();
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      logger.warn("ConfigService is null");
      return items;
    }
    for (String itemKey : inventory.getKeys()) {
      items.put(itemKey, configService.getItemConfig(itemKey));
    }
    return items;
  }

  /**
   * Get the skillset associated with the profile.
   *
   * @return the skillset of the profile.
   */
  public SkillSet skillset() {
    return skillset;
  }

  // Achievement methods now delegate to Statistics class
  // (which manages achievements based on statistical progress)

  /**
   * Get the statistics associated with the profile.
   *
   * @return the statistics of the profile.
   */
  public Statistics statistics() {
    return statistics;
  }
}
