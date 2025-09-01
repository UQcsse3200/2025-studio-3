package com.csse3200.game.progression.skillTree;

import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.progression.skilltree.Skill;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

@ExtendWith(GameExtension.class)
public class SkillTest {
    private Skill healthSkill;
    private Skill attackSkill;

    @Before
    public void setUp() {
        healthSkill = new Skill("Increase Health Basic", Skill.StatType.HEALTH, 10f, 1);
        attackSkill = new Skill("Increase AD Basic", Skill.StatType.ATTACK_DAMAGE, 15f, 2);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("Increase Health Basic", healthSkill.getName());
        assertEquals(Skill.StatType.HEALTH, healthSkill.getStatType());
        assertEquals(10f, healthSkill.getPercentage(), 0.001);
        assertEquals(1, healthSkill.getCost());
        assertTrue(healthSkill.getLockStatus()); // should be initially locked
    }

    @Test
    public void testUnlockSkill() {
        assertTrue(attackSkill.getLockStatus()); // initially locked
        attackSkill.unlock();
        assertFalse(attackSkill.getLockStatus()); // should be unlocked
    }

    @Test
    public void testMultipleSkillsIndependentLockStatus() {
        // Unlock one skill
        healthSkill.unlock();
        // Other skill should remain locked
        assertFalse(healthSkill.getLockStatus());
        assertTrue(attackSkill.getLockStatus());
    }
}

