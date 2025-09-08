package com.csse3200.game.progression.inventory;

import java.util.ArrayList;
import java.util.List;
import com.csse3200.game.components.items.Item;

/**
 * Inventory class to manage the player's items.
 */
public class Inventory {
  private final List<Item> items;

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
  public void addItem(Item item) {
    items.add(item);
  }

  /**
   * Removes an item from the inventory.
   * 
   * @param item The item to remove.
   */
  public void removeItem(Item item) {
    items.remove(item);
  }

  /**
   * Gets the list of items in the inventory.
   * 
   * @return The list of items.
   */
  public List<Item> get() {
    return items;
  }

  /**
   * Gets an item from the inventory by name.
   * 
   * @param itemName The name of the item to retrieve.
   * @return The item with the specified name, or null if not found.
   */
  public Item getItem(String itemName) {
    for (Item item : items) {
      if (item.getName().equals(itemName)) {
        return item;
      }
    }
    return null;
  }
}
