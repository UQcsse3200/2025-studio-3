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
    assertTrue(
        skillSet.isUnlockable("Health Basic"), "Basic skills should be unlockable at level 0");
    assertFalse(
        skillSet.isUnlockable("Health Intermediate"),
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

  @Test
  void testRemoveSkill() {
    Skill healthBasic = skillSet.getSkill("Health Basic");
    Skill attackBasic = skillSet.getSkill("Attack Basic");

    skillSet.addSkill(healthBasic);
    skillSet.addSkill(attackBasic);
    assertEquals(2, skillSet.getUnlockedSkills().size());

    skillSet.removeSkill(Skill.StatType.HEALTH);
    assertEquals(1, skillSet.getUnlockedSkills().size());
    assertFalse(skillSet.getUnlockedSkills().contains(healthBasic));
    assertTrue(skillSet.getUnlockedSkills().contains(attackBasic));
  }

  @Test
  void testCheckIfUnlocked() {
    Skill healthBasic = skillSet.getSkill("Health Basic");
    assertFalse(skillSet.checkIfUnlocked("Health Basic"));

    skillSet.addSkill(healthBasic);
    assertTrue(skillSet.checkIfUnlocked("Health Basic"));
    assertFalse(skillSet.checkIfUnlocked("Health Intermediate"));
  }

  @Test
  void testGetUpgradeValue_critChanceDefault() {
    // CRIT_CHANCE should return 0 by default when no skills are unlocked
    assertEquals(0f, skillSet.getUpgradeValue(Skill.StatType.CRIT_CHANCE));
  }

  @Test
  void testGetUpgradeValue_nonCritChanceDefault() {
    // Other stat types should return 1 by default when no skills are unlocked
    assertEquals(1f, skillSet.getUpgradeValue(Skill.StatType.HEALTH));
    assertEquals(1f, skillSet.getUpgradeValue(Skill.StatType.ATTACK_DAMAGE));
    assertEquals(1f, skillSet.getUpgradeValue(Skill.StatType.FIRING_SPEED));
    assertEquals(1f, skillSet.getUpgradeValue(Skill.StatType.CURRENCY_GEN));
  }

  @Test
  void testGetUpgradeValue_multipleSkillsUnlocked() {
    Skill healthBasic = skillSet.getSkill("Health Basic"); // 1.1f
    Skill healthIntermediate = skillSet.getSkill("Health Intermediate"); // 1.2f
    Skill healthAdvanced = skillSet.getSkill("Health Advanced"); // 1.3f

    skillSet.addSkill(healthBasic);
    skillSet.addSkill(healthAdvanced); // Skip intermediate
    skillSet.addSkill(healthIntermediate);

    // Should return the highest level unlocked (Advanced = level 3)
    assertEquals(1.3f, skillSet.getUpgradeValue(Skill.StatType.HEALTH));
  }

  @Test
  void testGetLevel_edgeCases() {
    assertEquals(0, skillSet.getLevel("Random Skill Name"));
    assertEquals(0, skillSet.getLevel(""));
    assertEquals(0, skillSet.getLevel("No Level Keyword"));
  }

  @Test
  void testIsUnlockable_edgeCases() {
    // Test with expert level skills when no previous levels are unlocked
    assertFalse(skillSet.isUnlockable("Health Expert"));
    assertFalse(skillSet.isUnlockable("Attack Expert"));

    // Test skipping levels
    skillSet.addSkill(skillSet.getSkill("Health Basic"));
    assertFalse(skillSet.isUnlockable("Health Advanced")); // Can't skip intermediate
  }

  @Test
  void testGetCurrentLevel_multipleStatTypes() {
    skillSet.addSkill(skillSet.getSkill("Health Intermediate")); // level 2
    skillSet.addSkill(skillSet.getSkill("Attack Expert")); // level 4
    skillSet.addSkill(skillSet.getSkill("Crit Basic")); // level 1

    assertEquals(2, skillSet.getCurrentLevel(Skill.StatType.HEALTH));
    assertEquals(4, skillSet.getCurrentLevel(Skill.StatType.ATTACK_DAMAGE));
    assertEquals(1, skillSet.getCurrentLevel(Skill.StatType.CRIT_CHANCE));
    assertEquals(0, skillSet.getCurrentLevel(Skill.StatType.FIRING_SPEED)); // No skills unlocked
  }

  @Test
  void testGetUpgradeValue_withCritChanceSkills() {
    Skill critBasic = skillSet.getSkill("Crit Basic"); // 0.1f
    Skill critIntermediate = skillSet.getSkill("Crit Intermediate"); // 0.2f

    skillSet.addSkill(critBasic);
    assertEquals(0.1f, skillSet.getUpgradeValue(Skill.StatType.CRIT_CHANCE));

    skillSet.addSkill(critIntermediate);
    assertEquals(0.2f, skillSet.getUpgradeValue(Skill.StatType.CRIT_CHANCE));
  }
}
