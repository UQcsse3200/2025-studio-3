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
    healthSkill = new Skill("Increase Health Basic", Skill.StatType.HEALTH, 10f, 1);
    attackSkill = new Skill("Increase AD Basic", Skill.StatType.ATTACK_DAMAGE, 15f, 2);
  }

  @Test
  void testConstructorAndGetters() {
    assertEquals("Increase Health Basic", healthSkill.getName());
    assertEquals(Skill.StatType.HEALTH, healthSkill.getStatType());
    assertEquals(10f, healthSkill.getPercentage(), 0.001);
    assertEquals(1, healthSkill.getCost());
    assertEquals("Increase AD Basic", attackSkill.getName());
    assertEquals(Skill.StatType.ATTACK_DAMAGE, attackSkill.getStatType());
    assertEquals(15f, attackSkill.getPercentage(), 0.001);
    assertEquals(2, attackSkill.getCost());
  }
}
