package com.csse3200.game.progression.skillTree;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.progression.skilltree.SkillSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class SkillSetTest {

  private SkillSet skillSet;

  @BeforeEach
  void setUp() {
    skillSet = new SkillSet();
  }

  @Test
  void testGetSkills() {
    List<Skill> skills = SkillSet.getSkills();
    // There should be 20 predefined skills (5 stats × 4 levels)
    assertEquals(20, skills.size());
    // Check that a specific skill exists
    boolean found = skills.stream().anyMatch(skill -> skill.getName().equals("Health Basic"));
    assertTrue(found);
  }

  @Test
  void testAddSkill() {
    Skill healthSkill = skillSet.getSkill("Health Basic");
    assertNotNull(healthSkill);

    // Initially unlockedSkills should be empty
    assertTrue(skillSet.getUnlockedSkills().isEmpty());

    skillSet.addSkill(healthSkill);

    // After adding, unlockedSkills should contain the skill
    List<Skill> unlocked = skillSet.getUnlockedSkills();
    assertEquals(1, unlocked.size());
    assertEquals("Health Basic", unlocked.get(0).getName());
  }

  @Test
  void testGetSkill() {
    Skill skill = skillSet.getSkill("Attack Intermediate");
    assertNotNull(skill);
    assertEquals("Attack Intermediate", skill.getName());
    assertEquals(Skill.StatType.ATTACK_DAMAGE, skill.getStatType());
    assertEquals(1.2f, skill.getPercentage());
    assertEquals(2, skill.getCost());
  }

  @Test
  void testGetSkill_returnsNull() {
    Skill skill = skillSet.getSkill("Nonexistent Skill");
    assertNull(skill);
  }

  @Test
  void testUnlockedSkills() {
    SkillSet otherSet = new SkillSet();
    Skill skill = skillSet.getSkill("Health Basic");

    skillSet.addSkill(skill);
    assertTrue(otherSet.getUnlockedSkills().isEmpty());
  }




    @Test
    void testGetLevel() {
        assertEquals(1, skillSet.getLevel("Health Basic"));
        assertEquals(2, skillSet.getLevel("Health Intermediate"));
        assertEquals(3, skillSet.getLevel("Health Advanced"));
        assertEquals(4, skillSet.getLevel("Health Expert"));
        assertEquals(0, skillSet.getLevel("Unknown Skill"));
    }

    @Test
    void testGetCurrentLevel_initiallyZero() {
        assertEquals(0, skillSet.getCurrentLevel(Skill.StatType.HEALTH));
    }

    @Test
    void testGetCurrentLevel_afterUnlocking() {
        Skill basic = skillSet.getSkill("Health Basic");
        Skill advanced = skillSet.getSkill("Health Advanced");
        skillSet.addSkill(basic);
        skillSet.addSkill(advanced);

        // Highest unlocked should be "Advanced" -> level 3
        assertEquals(3, skillSet.getCurrentLevel(Skill.StatType.HEALTH));
    }

    @Test
    void testGetUpgradeValue_defaultValues() {
        // No skills unlocked yet
        assertEquals(1f, skillSet.getUpgradeValue(Skill.StatType.HEALTH));
        assertEquals(0f, skillSet.getUpgradeValue(Skill.StatType.CRIT_CHANCE));
    }

    @Test
    void testGetUpgradeValue_afterUnlocking() {
        Skill basic = skillSet.getSkill("Health Basic"); // 1.1f
        Skill intermediate = skillSet.getSkill("Health Intermediate"); // 1.2f
        skillSet.addSkill(basic);
        skillSet.addSkill(intermediate);

        // Current level = 2 -> percentage = 1.2f
        assertEquals(1.2f, skillSet.getUpgradeValue(Skill.StatType.HEALTH));
    }

    @Test
    void testIsUnlockable_basicLevel() {
        assertTrue(skillSet.isUnlockable("Health Basic"),
                "Basic skills should be unlockable at level 0");
        assertFalse(skillSet.isUnlockable("Health Intermediate"),
                "Intermediate shouldn't be unlockable without Basic");
    }

    @Test
    void testIsUnlockable_higherLevels() {
        skillSet.addSkill(skillSet.getSkill("Health Basic"));
        assertTrue(skillSet.isUnlockable("Health Intermediate"));
        assertFalse(skillSet.isUnlockable("Health Advanced"));

        skillSet.addSkill(skillSet.getSkill("Health Intermediate"));
        assertTrue(skillSet.isUnlockable("Health Advanced"));
    }

}
