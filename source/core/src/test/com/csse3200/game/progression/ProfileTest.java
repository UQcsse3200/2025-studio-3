package com.csse3200.game.progression;

import com.csse3200.game.extensions.GameExtension;
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
  // private Inventory inventory; // The player's inventory of items (not
  // defences)
  // private Skillset skillset; // The player's skills / skill tree
  // private Achievements achievements; // The player's achievements
  // private Statistics statistics; // The player's statistics
  // private Progress progress; // The player's overall progress
  // private Arsenal arsenal; // The player's unlocked defences

  @BeforeEach
  void setUp() {
    wallet = new Wallet(100, 5);
    // inventory = new Inventory();
    // skillset = new Skillset();
    // achievements = new Achievements();
    // statistics = new Statistics();
    // progress = new Progress();
    // arsenal = new Arsenal();
  }

  @Test
  void testProfileDefaultConstructor() {
    Profile profile = new Profile();
    assertNotNull(profile.getName());
    assertNotNull(profile.wallet());
    assertEquals(100, profile.wallet().getCoins());
    assertEquals(0, profile.wallet().getSkillsPoints());
    // Insert
  }

  @Test
  void testProfileParameterizedConstructor() {
    Profile profile = new Profile(name, wallet);
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