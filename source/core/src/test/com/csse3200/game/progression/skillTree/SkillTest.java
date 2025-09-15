package com.csse3200.game.progression.skillTree;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.skilltree.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class SkillTest {
  private Skill healthSkill;
  private Skill attackSkill;

  @BeforeEach
  void setUp() {
    healthSkill = new Skill("Increase Health Basic", Skill.StatType.HEALTH, 1.1f, 1);
    attackSkill = new Skill("Increase AD Basic", Skill.StatType.ATTACK_DAMAGE, 1.15f, 2);
  }

  @Test
  void testConstructorAndGetters() {
    assertEquals("Increase Health Basic", healthSkill.getName());
    assertEquals(Skill.StatType.HEALTH, healthSkill.getStatType());
    assertEquals(1.1f, healthSkill.getPercentage(), 0.001);
    assertEquals(1, healthSkill.getCost());
    assertEquals("Increase AD Basic", attackSkill.getName());
    assertEquals(Skill.StatType.ATTACK_DAMAGE, attackSkill.getStatType());
    assertEquals(1.15f, attackSkill.getPercentage(), 0.001);
    assertEquals(2, attackSkill.getCost());
  }

  @Test
  void testGetDescription_forRegularStat() {
    Skill skill = new Skill("Health Basic", Skill.StatType.HEALTH, 1.1f, 1);

    String description = skill.getDescription();

    assertTrue(
        description.contains("increase health by 10%"),
        "Description should mention 10% increase for health");
    assertEquals("Unlocking this skill will permanently increase health by 10%", description);
  }

  @Test
  void testGetDescription_forCritChance() {
    Skill skill = new Skill("Crit Chance Basic", Skill.StatType.CRIT_CHANCE, 0.25f, 1);

    String description = skill.getDescription();

    assertTrue(
        description.contains("increase crit chance by 25%"),
        "Description should mention 25% increase for crit chance");
    assertEquals("Unlocking this skill will permanently increase crit chance by 25%", description);
  }

  @Test
  void testDefaultConstructor() {
    Skill skill = new Skill();
    assertNotNull(skill);
  }

  @Test
  void testGetDescription_forAttackDamage() {
    Skill skill = new Skill("Attack Basic", Skill.StatType.ATTACK_DAMAGE, 1.2f, 1);

    String description = skill.getDescription();

    assertTrue(
        description.contains("increase attack damage by 20%"),
        "Description should mention 20% increase for attack damage");
    assertEquals(
        "Unlocking this skill will permanently increase attack damage by 20%", description);
  }

  @Test
  void testGetDescription_forFiringSpeed() {
    Skill skill = new Skill("Firing Speed Basic", Skill.StatType.FIRING_SPEED, 1.15f, 1);

    String description = skill.getDescription();

    assertTrue(
        description.contains("increase firing speed by 14%"),
        "Description should mention 14% increase for firing speed");
    assertEquals("Unlocking this skill will permanently increase firing speed by 14%", description);
  }

  @Test
  void testGetDescription_forCurrencyGen() {
    Skill skill = new Skill("Currency Basic", Skill.StatType.CURRENCY_GEN, 1.3f, 1);

    String description = skill.getDescription();

    assertTrue(
        description.contains("increase currency gen by 29%"),
        "Description should mention 29% increase for currency gen");
    assertEquals("Unlocking this skill will permanently increase currency gen by 29%", description);
  }

  @Test
  void testGetDescription_withZeroPercentageIncrease() {
    Skill skill = new Skill("No Bonus", Skill.StatType.HEALTH, 1.0f, 1);

    String description = skill.getDescription();

    assertTrue(
        description.contains("increase health by 0%"), "Description should handle 0% increase");
    assertEquals("Unlocking this skill will permanently increase health by 0%", description);
  }
}
