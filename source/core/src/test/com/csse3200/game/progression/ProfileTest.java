package com.csse3200.game.progression;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
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
    assertNotNull(profile.getWallet());
    assertEquals(30, profile.getWallet().getCoins()); // Corrected default value
    assertEquals(1, profile.getWallet().getSkillsPoints()); // Corrected default value
  }

  @Test
  void testProfileParameterizedConstructor() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(name, profile.getName());
    assertEquals(100, profile.getWallet().getCoins());
    assertEquals(5, profile.getWallet().getSkillsPoints());
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
    assertEquals(inventory, profile.getInventory());
  }

  @Test
  void testGetArsenal() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(arsenal, profile.getArsenal());
  }

  @Test
  void testGetSkillset() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(skillset, profile.getSkillset());
  }

  @Test
  void testGetStatistics() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(statistics, profile.getStatistics());
  }

  @Test
  void testGetWallet() {
    Profile profile =
        new Profile(name, wallet, inventory, skillset, statistics, arsenal, currentLevel);
    assertEquals(wallet, profile.getWallet());
  }

  @Test
  void testParameterizedConstructorWithNullStatistics() {
    Profile profile = new Profile(name, wallet, inventory, skillset, null, arsenal, currentLevel);
    assertNotNull(profile.getStatistics());
  }
}
