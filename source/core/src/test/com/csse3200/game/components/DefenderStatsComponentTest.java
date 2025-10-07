package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class DefenderStatsComponentTest {
  private static ProfileService mockProfileService;
  private static Profile mockProfile;
  private static SkillSet mockSkillset;

  @BeforeAll
  static void setupMocks() {
    // create mocks
    mockProfileService = mock(ProfileService.class);
    mockProfile = mock(Profile.class);
    mockSkillset = mock(SkillSet.class);

    // stub methods
    when(mockProfileService.getProfile()).thenReturn(mockProfile);
    when(mockProfile.getSkillset()).thenReturn(mockSkillset);

    // stub upgrade values
    when(mockSkillset.getUpgradeValue(any())).thenReturn(1f); // default upgrade multiplier

    // inject into ServiceLocator
    ServiceLocator.registerProfileService(mockProfileService);
  }

  @Test
  void shouldSetGetHealth() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);

    assertEquals(100, defender.getHealth());
    defender.setHealth(150);
    assertEquals(150, defender.getHealth());
    defender.setHealth(-50);
    assertEquals(0, defender.getHealth());
  }

  @Test
  void shouldCheckIsDead() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    assertFalse(defender.isDead());

    defender.setHealth(0);
    assertTrue(defender.isDead());
  }

  @Test
  void shouldSetGetBaseAttack() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    assertEquals(50, defender.getBaseAttack());

    defender.setBaseAttack(150);
    assertEquals(150, defender.getBaseAttack());

    defender.setBaseAttack(-50);
    assertEquals(150, defender.getBaseAttack());
  }

  @Test
  void testTypeSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setType(5);
    assertEquals(5, defender.getType());
  }

  @Test
  void testRangeSetterGetter_Positive() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setRange(10);
    assertEquals(10, defender.getRange());
  }

  @Test
  void testRangeSetterGetter_Negative() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setRange(-5); // should clamp to 0
    assertEquals(0, defender.getRange());
  }

  @Test
  void testStateSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setState(2);
    assertEquals(2, defender.getState());
  }

  @Test
  void testAttackSpeedSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setAttackSpeed(15);
    assertEquals(15, defender.getAttackSpeed());
  }

  @Test
  void testAttackSpeedSetterGetter_Negative() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setAttackSpeed(-5); // should clamp to 0
    assertEquals(0, defender.getAttackSpeed());
  }

  @Test
  void testCritChanceSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setCritChance(25);
    assertEquals(26, defender.getCritChance()); // Because of the upgrade of 1, it gets added to become 26
  }

  @Test
  void testCostGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1, 100);
    defender.setCost(150);
    assertEquals(150, defender.getCost());
  }
}
