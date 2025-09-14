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
}
