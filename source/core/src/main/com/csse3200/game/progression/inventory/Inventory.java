package com.csse3200.game.progression.inventory;

import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Inventory class to manage the player's items. */
public class Inventory {
  private final List<String> items;

  /** Constructor for the Inventory class. */
  public Inventory() {
    items = new ArrayList<>();
  }

  /**
   * Adds an item to the inventory.
   *
   * @param itemKey The key of the item to add.
   */
  public void addItem(String itemKey) {
    items.add(itemKey);
  }

  /**
   * Removes an item from the inventory.
   *
   * @param itemKey The key of the item to remove.
   */
  public void removeItem(String itemKey) {
    items.remove(itemKey);
  }

  /**
   * Gets the list of items in the inventory.
   *
   * @return The list of keys of the items in the inventory.
   */
  public List<String> getKeys() {
    return items;
  }

  /**
   * Checks if the inventory contains the specified item key.
   *
   * @param itemKey The key of the item to check.
   * @return True if the inventory contains the item key, false otherwise.
   */
  public boolean contains(String itemKey) {
    return items.contains(itemKey);
  }

  /**
   * Get the items in the inventory.
   *
   * @return the items in the inventory.
   */
  public Map<String, BaseItemConfig> getInventoryItems() {
    Map<String, BaseItemConfig> itemMap = new HashMap<>();
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      return itemMap;
    }
    for (String itemKey : getKeys()) {
      itemMap.put(itemKey, configService.getItemConfig(itemKey));
    }
    return itemMap;
  }
}
