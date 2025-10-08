package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.progression.skilltree.Skill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class DefenderStatsComponentTest {
  DefenderStatsComponent defender;
  
  @BeforeEach
  void beforeEach() {
    ProfileService profileService = mock(ProfileService.class, RETURNS_DEEP_STUBS);
    ServiceLocator.registerProfileService(profileService);
    when(profileService.getProfile().getSkillset().getUpgradeValue(Skill.StatType.ATTACK_DAMAGE)).thenReturn(1.0f);
    when(profileService.getProfile().getSkillset().getUpgradeValue(Skill.StatType.HEALTH)).thenReturn(1.0f);
    when(profileService.getProfile().getSkillset().getUpgradeValue(Skill.StatType.FIRING_SPEED)).thenReturn(1.0f);
    when(profileService.getProfile().getSkillset().getUpgradeValue(Skill.StatType.CRIT_CHANCE)).thenReturn(1.0f);
    defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
  }

  @Test
  void shouldSetGetHealth() {


    assertEquals(100, defender.getHealth());
    defender.setHealth(150);
    assertEquals(150, defender.getHealth());
    defender.setHealth(-50);
    assertEquals(0, defender.getHealth());
  }

  @Test
  void shouldCheckIsDead() {

    assertFalse(defender.isDead());

    defender.setHealth(0);
    assertTrue(defender.isDead());
  }

  @Test
  void shouldSetGetBaseAttack() {

    assertEquals(50, defender.getBaseAttack());

    defender.setBaseAttack(150);
    assertEquals(150, defender.getBaseAttack());

    defender.setBaseAttack(-50);
    assertEquals(150, defender.getBaseAttack());
  }

  @Test
  void testTypeSetterGetter() {

    defender.setType(5);
    assertEquals(5, defender.getType());
  }

  @Test
  void testRangeSetterGetter_Positive() {

    defender.setRange(10);
    assertEquals(10, defender.getRange());
  }

  @Test
  void testRangeSetterGetter_Negative() {

    defender.setRange(-5); // should clamp to 0
    assertEquals(0, defender.getRange());
  }

  @Test
  void testStateSetterGetter() {

    defender.setState(2);
    assertEquals(2, defender.getState());
  }

  @Test
  void testAttackSpeedSetterGetter() {

    defender.setAttackSpeed(15);
    assertEquals(15, defender.getAttackSpeed());
  }

  @Test
  void testAttackSpeedSetterGetter_Negative() {

    defender.setAttackSpeed(-5); // should clamp to 0
    assertEquals(0, defender.getAttackSpeed());
  }

  @Test
  void testCritChanceSetterGetter() {

    defender.setCritChance(25);
    assertEquals(26, defender.getCritChance());
  }
}
