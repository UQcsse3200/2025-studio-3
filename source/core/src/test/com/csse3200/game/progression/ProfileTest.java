package com.csse3200.game.progression;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.achievements.AchievementManager;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(GameExtension.class)
class ProfileTest {

  private final String name = "TestPlayer";
  private Wallet wallet;
  private Inventory inventory;
  private SkillSet skillset;
  private AchievementManager achievements;
  private Statistics statistics;

  @BeforeEach
  void setUp() {
    wallet = new Wallet(100, 5);
    inventory = new Inventory();
    skillset = new SkillSet();
    achievements = new AchievementManager();
    statistics = new Statistics();
  }

  @Test
  void testProfileDefaultConstructor() {
    Profile profile = new Profile();
    assertNotNull(profile.getName());
    assertNotNull(profile.wallet());
    assertEquals(100, profile.wallet().getCoins());
    assertEquals(10, profile.wallet().getSkillsPoints());
    
  }

  @Test
  void testProfileParameterizedConstructor() {
    Profile profile = new Profile(name, wallet, inventory, skillset, achievements, statistics);
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
    // Insert
  }

}