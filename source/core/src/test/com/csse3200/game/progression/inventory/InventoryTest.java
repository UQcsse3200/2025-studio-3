package com.csse3200.game.progression.inventory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(GameExtension.class)
class InventoryTest {
  private Inventory inventory;

  private Integer NUM_INITIAL_INVENTORY = 0;

  @BeforeEach
  void setUp() {
    inventory = new Inventory();
  }

  @Test
  void shouldCreateEmptyInventory() {
    List<String> items = inventory.getKeys();
    assertTrue(items.isEmpty());
  }

  @Test
  void shouldAddItem() {
    inventory.addItem("sword");

    assertTrue(inventory.contains("sword"));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());
    assertEquals("sword", inventory.getKeys().get(0));
  }

  @Test
  void shouldAddMultipleItems() {
    inventory.addItem("sword");
    inventory.addItem("shield");
    inventory.addItem("potion");

    assertEquals(NUM_INITIAL_INVENTORY + 3, inventory.getKeys().size());
    assertTrue(inventory.contains("sword"));
    assertTrue(inventory.contains("shield"));
    assertTrue(inventory.contains("potion"));
  }

  @Test
  void shouldAddSameItemMultipleTimes() {
    inventory.addItem("potion");
    inventory.addItem("potion");
    inventory.addItem("potion");

    // Should have 3 entries (List allows duplicates)
    assertEquals(NUM_INITIAL_INVENTORY + 3, inventory.getKeys().size());
    assertTrue(inventory.contains("potion"));

    // All entries should be the same item
    List<String> items = inventory.getKeys();
    for (String item : items) {
      assertEquals("potion", item);
    }
  }

  @Test
  void shouldRemoveItem() {
    inventory.addItem("sword");
    inventory.addItem("shield");

    assertTrue(inventory.contains("sword"));
    assertTrue(inventory.contains("shield"));
    assertEquals(NUM_INITIAL_INVENTORY + 2, inventory.getKeys().size());

    inventory.removeItem("sword");

    assertFalse(inventory.contains("sword"));
    assertTrue(inventory.contains("shield"));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());
  }

  @Test
  void shouldRemoveOnlyFirstOccurrenceOfItem() {
    inventory.addItem("potion");
    inventory.addItem("potion");
    inventory.addItem("potion");

    assertEquals(NUM_INITIAL_INVENTORY + 3, inventory.getKeys().size());

    inventory.removeItem("potion");

    // Should still contain potion (but only 2 instances)
    assertTrue(inventory.contains("potion"));
    assertEquals(NUM_INITIAL_INVENTORY + 2, inventory.getKeys().size());
  }

  @Test
  void shouldHandleRemovingNonExistentItem() {
    inventory.addItem("sword");

    // Try to remove an item that doesn't exist
    inventory.removeItem("bow");

    // Original item should still be there
    assertTrue(inventory.contains("sword"));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());
  }

  @Test
  void shouldHandleRemovingFromEmptyInventory() {
    // Try to remove from empty inventory
    inventory.removeItem("sword");

    // Should remain empty
    assertTrue(inventory.getKeys().isEmpty());
    assertFalse(inventory.contains("sword"));
  }

  @Test
  void shouldReturnCorrectContainsResult() {
    assertFalse(inventory.contains("sword"));
    assertFalse(inventory.contains("shield"));

    inventory.addItem("sword");

    assertTrue(inventory.contains("sword"));
    assertFalse(inventory.contains("shield"));

    inventory.addItem("shield");

    assertTrue(inventory.contains("sword"));
    assertTrue(inventory.contains("shield"));
  }

  @Test
  void shouldHandleNullItemKey() {
    // Test adding null
    inventory.addItem(null);
    assertTrue(inventory.contains(null));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());

    // Test removing null
    inventory.removeItem(null);
    assertFalse(inventory.contains(null));
    assertTrue(inventory.getKeys().isEmpty());
  }

  @Test
  void shouldHandleEmptyStringItemKey() {
    inventory.addItem("");

    assertTrue(inventory.contains(""));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());
    assertEquals("", inventory.getKeys().get(0));

    inventory.removeItem("");

    assertFalse(inventory.contains(""));
    assertTrue(inventory.getKeys().isEmpty());
  }

  @Test
  void shouldPreserveOrderOfAddedItems() {
    inventory.addItem("first");
    inventory.addItem("second");
    inventory.addItem("third");

    List<String> items = inventory.getKeys();
    assertEquals("first", items.get(0));
    assertEquals("second", items.get(1));
    assertEquals("third", items.get(2));
  }

  @Test
  void shouldMaintainOrderAfterRemoval() {
    inventory.addItem("first");
    inventory.addItem("second");
    inventory.addItem("third");
    inventory.addItem("fourth");

    // Remove the second element
    inventory.removeItem("second");

    List<String> items = inventory.getKeys();
    assertEquals(NUM_INITIAL_INVENTORY + 3, items.size());
    assertEquals("first", items.get(0));
    assertEquals("third", items.get(1));
    assertEquals("fourth", items.get(2));
  }

  @Test
  void shouldHandleCaseSensitiveItemKeys() {
    inventory.addItem("Sword");
    inventory.addItem("sword");
    inventory.addItem("SWORD");

    assertEquals(3, inventory.getKeys().size());
    assertTrue(inventory.contains("Sword"));
    assertTrue(inventory.contains("sword"));
    assertTrue(inventory.contains("SWORD"));

    // Should be case sensitive
    assertFalse(inventory.contains("sWoRd"));
  }

  @Test
  void shouldAllowSpecialCharactersInItemKeys() {
    String specialKey = "sword-of-fire_v2.0@legendary";
    inventory.addItem(specialKey);

    assertTrue(inventory.contains(specialKey));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());
    assertEquals(specialKey, inventory.getKeys().get(0));
  }

  @Test
  void shouldHandleVeryLongItemKeys() {
    String longKey = "a".repeat(1000); // 1000 character key
    inventory.addItem(longKey);

    assertTrue(inventory.contains(longKey));
    assertEquals(NUM_INITIAL_INVENTORY + 1, inventory.getKeys().size());
    assertEquals(longKey, inventory.getKeys().get(0));
  }

  @Test
  void shouldAllowManyItems() {
    // Add many items
    for (int i = 0; i < 1000; i++) {
      inventory.addItem("item" + i);
    }

    assertEquals(NUM_INITIAL_INVENTORY + 1000, inventory.getKeys().size());
    assertTrue(inventory.contains("item0"));
    assertTrue(inventory.contains("item500"));
    assertTrue(inventory.contains("item999"));
    assertFalse(inventory.contains("item1000"));
  }

  @Test
  void shouldCompletelyEmptyInventoryAfterRemovingAllItems() {
    inventory.addItem("sword");
    inventory.addItem("shield");
    inventory.addItem("potion");

    assertEquals(NUM_INITIAL_INVENTORY + 3, inventory.getKeys().size());

    inventory.removeItem("sword");
    inventory.removeItem("shield");
    inventory.removeItem("potion");

    assertTrue(inventory.getKeys().isEmpty());
    assertFalse(inventory.contains("sword"));
    assertFalse(inventory.contains("shield"));
    assertFalse(inventory.contains("potion"));
  }

  @Test
  void shouldRemoveCorrectItemFromDuplicates() {
    inventory.addItem("potion");
    inventory.addItem("sword");
    inventory.addItem("potion");
    inventory.addItem("shield");
    inventory.addItem("potion");

    assertEquals(NUM_INITIAL_INVENTORY + 5, inventory.getKeys().size());

    // Remove one potion
    inventory.removeItem("potion");

    assertEquals(NUM_INITIAL_INVENTORY + 4, inventory.getKeys().size());
    assertTrue(inventory.contains("potion")); // Still has potions
    assertTrue(inventory.contains("sword"));
    assertTrue(inventory.contains("shield"));

    // Verify the correct order is maintained
    List<String> items = inventory.getKeys();
    assertEquals("sword", items.get(0));
    assertEquals("potion", items.get(1));
    assertEquals("shield", items.get(2));
    assertEquals("potion", items.get(3));
  }

  @Test
  void shouldGetInventoryItems() {
    inventory.addItem("sword");
    inventory.addItem("shield");
    inventory.addItem("potion");
    ConfigService configService = mock(ConfigService.class);
    ServiceLocator.registerConfigService(configService);
    when(configService.getItemConfig("sword")).thenReturn(new BaseItemConfig());
    when(configService.getItemConfig("shield")).thenReturn(new BaseItemConfig());
    when(configService.getItemConfig("potion")).thenReturn(new BaseItemConfig());
    Map<String, BaseItemConfig> items = inventory.getInventoryItems();
    assertEquals(3, items.size());
    assertTrue(items.containsKey("sword"));
    assertTrue(items.containsKey("shield"));
    assertTrue(items.containsKey("potion"));
  }

  @Test
  void shouldGetEmptyInventoryItems() {
    Map<String, BaseItemConfig> items = inventory.getInventoryItems();
    assertTrue(items.isEmpty());
  }

  @Test
  void shouldGetInventoryItemsWithNullConfigService() {
    ConfigService configService = mock(ConfigService.class);
    ServiceLocator.registerConfigService(configService);
    Map<String, BaseItemConfig> items = inventory.getInventoryItems();
    assertTrue(items.isEmpty());
  }
}
