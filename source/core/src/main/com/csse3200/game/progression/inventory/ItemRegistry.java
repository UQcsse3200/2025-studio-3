package com.csse3200.game.progression.inventory;

import com.csse3200.game.components.items.BuffComponent;
import com.csse3200.game.components.items.CoffeeComponent;
import com.csse3200.game.components.items.EmpComponent;
import com.csse3200.game.components.items.GrenadeComponent;
import com.csse3200.game.components.items.Item;
import com.csse3200.game.components.items.NukeComponent;

/**
 * Singleton item registry with all possible items.
 */
public class ItemRegistry {
  public record ItemEntry(String key, Item item, String assetPath) {
  }

  public static final ItemEntry[] ITEMS = {
      new ItemEntry("nuke", new NukeComponent(), "images/item_nuke.png"),
      new ItemEntry("grenade", new GrenadeComponent(), "images/item_grenade.png"),
      new ItemEntry("coffee", new CoffeeComponent(), "images/item_coffee.png"),
      new ItemEntry("buff", new BuffComponent(), "images/item_buff.png"),
      new ItemEntry("emp", new EmpComponent(), "images/item_emp.png"),
  };
}
