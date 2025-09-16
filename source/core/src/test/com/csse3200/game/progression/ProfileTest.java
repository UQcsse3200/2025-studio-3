package com.csse3200.game.progression;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@ExtendWith(GameExtension.class)
class ProfileTest {

  @Mock private ConfigService mockConfigService;

  private final String name = "TestPlayer";
  private Wallet wallet;
  private Inventory inventory;
  private SkillSet skillset;
  private Statistics statistics;
  private Arsenal arsenal;
  private String currentLevel;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ServiceLocator.clear(); // Ensure clean state before each test
    wallet = new Wallet(100, 5);
    inventory = new Inventory();
    skillset = new SkillSet();
    statistics = new Statistics();
    arsenal = new Arsenal();
    currentLevel = "level1";
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear(); // Clean up after each test
  }

  @Test
  void testProfileDefaultConstructor() {
    Profile profile = new Profile();
    assertNotNull(profile.getName());
    assertNotNull(profile.wallet());
    assertEquals(30, profile.wallet().getCoins()); // Corrected default value
    assertEquals(1, profile.wallet().getSkillsPoints()); // Corrected default value
  }

  @Test
  void testProfileParameterizedConstructor() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(name, profile.getName());
    assertEquals(100, profile.wallet().getCoins());
    assertEquals(5, profile.wallet().getSkillsPoints());
    // Insert
  }

  @Test
  void testProfileSetName() {
    Profile profile = new Profile();
    String newName = "NewPlayerName";
    profile.setName(newName);
    assertEquals(newName, profile.getName());
  }

  @Test
  void testGetCurrentLevel() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(currentLevel, profile.getCurrentLevel());
  }

  @Test
  void testSetCurrentLevel() {
    Profile profile = new Profile();
    String newLevel = "level2";
    profile.setCurrentLevel(newLevel);
    assertEquals(newLevel, profile.getCurrentLevel());
  }

  @Test
  void testGetInventory() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(inventory, profile.inventory());
  }

  @Test
  void testGetArsenal() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(arsenal, profile.arsenal());
  }

  @Test
  void testGetSkillset() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(skillset, profile.skillset());
  }

  @Test
  void testGetStatistics() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(statistics, profile.statistics());
  }

  @Test
  void testAddItemToInventory() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    profile.addItemToInventory("sword");
    assertTrue(inventory.contains("sword"));
  }

  @Test
  void testRemoveItemFromInventory() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    inventory.addItem("sword");
    profile.removeItemFromInventory("sword");
    assertFalse(inventory.contains("sword"));
  }

  @Test
  void testGetInventoryItemsWithConfigService() {
    // Mock ConfigService
    BaseItemConfig mockItemConfig = mock(BaseItemConfig.class);
    when(mockConfigService.getItemConfig("sword")).thenReturn(mockItemConfig);
    ServiceLocator.registerConfigService(mockConfigService);

    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    inventory.addItem("sword");

    Map<String, BaseItemConfig> items = profile.getInventoryItems();
    assertEquals(1, items.size());
    assertTrue(items.containsKey("sword"));
    assertEquals(mockItemConfig, items.get("sword"));
  }

  @Test
  void testGetInventoryItemsWithNullConfigService() {
    // Create a completely fresh inventory to avoid any state leakage
    Inventory freshInventory = new Inventory();

    ServiceLocator.deregisterConfigService();

    // Verify that ConfigService is indeed null
    assertNull(ServiceLocator.getConfigService());

    Profile profile =
        new Profile(name, wallet, freshInventory, skillset, statistics, arsenal, currentLevel);
    freshInventory.addItem("sword");

    Map<String, BaseItemConfig> items = profile.getInventoryItems();
    assertTrue(items.isEmpty());
  }

  @Test
  void testParameterizedConstructorWithNullStatistics() {
    Profile profile = new Profile(name, wallet, inventory, skillset, null, arsenal, currentLevel);
    assertNotNull(profile.statistics());
  }
}
