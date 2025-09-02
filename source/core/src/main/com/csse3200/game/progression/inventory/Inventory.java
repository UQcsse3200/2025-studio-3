package com.csse3200.game.progression.inventory;

import java.util.ArrayList;
import java.util.List;
import com.csse3200.game.components.items.DefaultItemComponent;

/**
 * Inventory class to manage the player's items.
 */
public class Inventory {
  private final List<DefaultItemComponent> items;

  /**
   * Constructor for the Inventory class.
   */
  public Inventory() {
    items = new ArrayList<>();
  }

  /**
   * Adds an item to the inventory.
   * 
   * @param item The item to add.
   */
  public void addItem(DefaultItemComponent item) {
    items.add(item);
  }

  /**
   * Removes an item from the inventory.
   * 
   * @param item The item to remove.
   */
  public void removeItem(DefaultItemComponent item) {
    items.remove(item);
  }

  /**
   * Gets the list of items in the inventory.
   * 
   * @return The list of items.
   */
  public List<DefaultItemComponent> get() {
    return items;
  }

  /**
   * Gets an item from the inventory by name.
   * 
   * @param itemName The name of the item to retrieve.
   * @return The item with the specified name, or null if not found.
   */
  public DefaultItemComponent getItem(String itemName) {
    // for (DefaultItemComponent item : items) {
    //   if (item.getName().equals(itemName)) {
    //     return item;
    //   }
    // }
    return null;
  }
}
