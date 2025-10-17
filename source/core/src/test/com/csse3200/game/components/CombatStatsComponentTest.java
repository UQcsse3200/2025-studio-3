package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class CombatStatsComponentTest {
  @Test
  void shouldSetGetHealth() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    assertEquals(100, combat.getHealth());

    combat.setHealth(150);
    assertEquals(150, combat.getHealth());

    combat.setHealth(-50);
    assertEquals(0, combat.getHealth());
  }

  @Test
  void shouldSetGetMaxHealth() {
    CombatStatsComponent combat1 = new CombatStatsComponent(100, 20);
    assertEquals(100, combat1.getMaxHealth());

    CombatStatsComponent combat2 = new CombatStatsComponent(150, 20);
    assertEquals(150, combat2.getMaxHealth());

    CombatStatsComponent combat3 = new CombatStatsComponent(-50, 20);
    assertEquals(0, combat3.getMaxHealth());
  }

  @Test
  void shouldCheckIsDead() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    assertFalse(combat.isDead());

    combat.setHealth(0);
    assertTrue(combat.isDead());
  }

  @Test
  void shouldAddHealth() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addHealth(-500);
    assertEquals(0, combat.getHealth());

    combat.addHealth(100);
    combat.addHealth(-20);
    assertEquals(80, combat.getHealth());
  }

  @Test
  void shouldNotChangeMaxHealth() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addHealth(-500);
    assertEquals(100, combat.getMaxHealth());

    combat.setHealth(50);
    assertEquals(100, combat.getMaxHealth());
  }

  @Test
  void shouldSetGetBaseAttack() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    assertEquals(20, combat.getBaseAttack());

    combat.setBaseAttack(150);
    assertEquals(150, combat.getBaseAttack());

    combat.setBaseAttack(-50);
    assertEquals(150, combat.getBaseAttack());
  }

  @Test
  void shouldLowerHealthOnHit() {
    CombatStatsComponent defender = new CombatStatsComponent(5, 0);
    CombatStatsComponent attacker = new CombatStatsComponent(100, 3);

    assertEquals(5, defender.getHealth());

    defender.hit(attacker);
    assertEquals(2, defender.getHealth());
    assertFalse(defender.isDead());

    defender.hit(attacker);
    assertEquals(0, defender.getHealth());
    assertTrue(defender.isDead());
  }
}
