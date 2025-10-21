package com.csse3200.game.progression;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.arsenal.Arsenal;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import com.csse3200.game.services.ServiceLocator;
import net.dermetfan.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;

@ExtendWith(GameExtension.class)
class ProfileTest {
  private final String name = "TestPlayer";
  private Wallet wallet;
  private Inventory inventory;
  private SkillSet skillset;
  private Statistics statistics;
  private Arsenal arsenal;
  private String currentLevel;
  private Pair<String, String> nameAndLevel;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ServiceLocator.clear();
    wallet = new Wallet(30, 1);
    inventory = new Inventory();
    skillset = new SkillSet();
    statistics = new Statistics();
    arsenal = new Arsenal();
    currentLevel = "levelOne";
    nameAndLevel = new Pair<>(name, currentLevel);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void testProfileDefaultConstructor() {
    Profile profile = new Profile();
    assertNotNull(profile.getName());
    assertNotNull(profile.getWallet());
    assertEquals(30, profile.getWallet().getCoins());
    assertEquals(1, profile.getWallet().getSkillsPoints());
  }

  @Test
  void testProfileParameterizedConstructor() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(name, profile.getName());
    assertEquals(30, profile.getWallet().getCoins());
    assertEquals(1, profile.getWallet().getSkillsPoints());
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
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(currentLevel, profile.getCurrentLevel());
  }

  @Test
  void testSetCurrentLevel() {
    Profile profile = new Profile();
    String newLevel = "levelTwo";
    profile.setCurrentLevel(newLevel);
    assertEquals(newLevel, profile.getCurrentLevel());
  }

  @Test
  void testGetInventory() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(inventory, profile.getInventory());
  }

  @Test
  void testGetArsenal() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(arsenal, profile.getArsenal());
  }

  @Test
  void testGetSkillset() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(skillset, profile.getSkillset());
  }

  @Test
  void testGetStatistics() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(statistics, profile.getStatistics());
  }

  @Test
  void testGetWallet() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, statistics, arsenal);
    assertEquals(wallet, profile.getWallet());
  }

  @Test
  void testParameterizedConstructorWithNullStatistics() {
    Profile profile = new Profile(nameAndLevel, wallet, inventory, skillset, null, arsenal);
    assertNotNull(profile.getStatistics());
  }
}
