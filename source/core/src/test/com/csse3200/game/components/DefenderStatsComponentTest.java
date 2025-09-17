package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class DefenderStatsComponentTest {

  @Test
  void shouldSetGetHealth() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);

    assertEquals(100, defender.getHealth());
    defender.setHealth(150);
    assertEquals(150, defender.getHealth());
    defender.setHealth(-50);
    assertEquals(0, defender.getHealth());
  }

  @Test
  void shouldCheckIsDead() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    assertFalse(defender.isDead());

    defender.setHealth(0);
    assertTrue(defender.isDead());
  }

  @Test
  void shouldSetGetBaseAttack() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    assertEquals(50, defender.getBaseAttack());

    defender.setBaseAttack(150);
    assertEquals(150, defender.getBaseAttack());

    defender.setBaseAttack(-50);
    assertEquals(150, defender.getBaseAttack());
  }

  @Test
  void testTypeSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setType(5);
    assertEquals(5, defender.getType());
  }

  @Test
  void testRangeSetterGetter_Positive() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setRange(10);
    assertEquals(10, defender.getRange());
  }

  @Test
  void testRangeSetterGetter_Negative() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setRange(-5); // should clamp to 0
    assertEquals(0, defender.getRange());
  }

  @Test
  void testStateSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setState(2);
    assertEquals(2, defender.getState());
  }

  @Test
  void testAttackSpeedSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setAttackSpeed(15);
    assertEquals(15, defender.getAttackSpeed());
  }

  @Test
  void testAttackSpeedSetterGetter_Negative() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setAttackSpeed(-5); // should clamp to 0
    assertEquals(0, defender.getAttackSpeed());
  }

  @Test
  void testCritChanceSetterGetter() {
    DefenderStatsComponent defender = new DefenderStatsComponent(100, 50, 0, 500, 0, 3, 1);
    defender.setCritChance(25);
    assertEquals(25, defender.getCritChance());
  }
}
