package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class GeneratorStatsComponentTest {

  @Test
  void shouldSetGetHealth() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);

    assertEquals(100, generator.getHealth());
    generator.setHealth(150);
    assertEquals(150, generator.getHealth());
    generator.setHealth(-50);
    assertEquals(0, generator.getHealth());
  }

  @Test
  void shouldCheckIsDead() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    assertFalse(generator.isDead());

    generator.setHealth(0);
    assertTrue(generator.isDead());
  }

  @Test
  void shouldSetGetBaseAttack() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    assertEquals(0, generator.getBaseAttack()); // should have 0 base attack
  }

  @Test
  void testIntervalSetterGetter_Positive() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    generator.setInterval(10);
    assertEquals(10, generator.getInterval());
  }

  @Test
  void testIntervalSetterGetter_Negative() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    generator.setInterval(-5); // should clamp to 0
    assertEquals(0, generator.getInterval());
  }

  @Test
  void testScrapValueSetterGetter_Positive() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    generator.setScrapValue(15);
    assertEquals(15, generator.getScrapValue());
  }

  @Test
  void testScrapValueSetterGetter_Negative() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    generator.setScrapValue(-10); // should clamp to 0
    assertEquals(0, generator.getScrapValue());
  }

  @Test
  void testCostGetter_Positive() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    generator.setCost(100);
    assertEquals(100, generator.getCost());
  }

  @Test
  void testCostGetter_Negative() {
    GeneratorStatsComponent generator = new GeneratorStatsComponent(100, 15, 25, 50);
    generator.setCost(-100); // should clamp to 0
    assertEquals(0, generator.getCost());
  }
}
