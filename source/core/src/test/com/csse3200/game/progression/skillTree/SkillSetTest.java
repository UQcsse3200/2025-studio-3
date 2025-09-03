package com.csse3200.game.progression.skillTree;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.skilltree.Skill;
import com.csse3200.game.progression.skilltree.SkillSet;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

@ExtendWith(GameExtension.class)
public class SkillSetTest {

    private SkillSet skillSet;
    @Before
    public void setUp() {
        skillSet = new SkillSet();
    }

    @Test
    public void testGetSkills() {
        List<Skill> skills = SkillSet.getSkills();
        // There should be 15 predefined skills (5 stats × 3 levels)
        assertEquals(15, skills.size());
        // Check that a specific skill exists
        boolean found = skills.stream().anyMatch(skill -> skill.getName().equals("Increase Health Basic"));
        assertTrue(found);
    }

    @Test
    public void testAddSkill() {
        Skill healthSkill = skillSet.getSkill("Increase Health Basic");
        assertNotNull(healthSkill);

        // Initially unlockedSkills should be empty
        assertTrue(skillSet.getUnlockedSkills().isEmpty());

        skillSet.addSkill(healthSkill);

        // After adding, unlockedSkills should contain the skill
        List<Skill> unlocked = skillSet.getUnlockedSkills();
        assertEquals(1, unlocked.size());
        assertEquals("Increase Health Basic", unlocked.get(0).getName());
    }

    @Test
    public void testGetSkill() {
        Skill skill = skillSet.getSkill("Increase AD Intermediate");
        assertNotNull(skill);
        assertEquals("Increase AD Intermediate", skill.getName());
        assertEquals(Skill.StatType.ATTACK_DAMAGE, skill.getStatType());
        assertEquals(20, (int) skill.getPercentage());
        assertEquals(2, skill.getCost());
    }

    @Test
    public void testGetSkill_returnsNull() {
        Skill skill = skillSet.getSkill("Nonexistent Skill");
        assertNull(skill);
    }

    @Test
    public void testUnlockedSkills() {
        SkillSet otherSet = new SkillSet();
        Skill skill = skillSet.getSkill("Increase Health Basic");

        skillSet.addSkill(skill);
        assertTrue(otherSet.getUnlockedSkills().isEmpty());
    }
}

