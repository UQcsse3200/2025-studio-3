package com.csse3200.game.components;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BomberDeathExplodeComponentTest {
    private EntityService entityService;

    // Simple fake CombatStats component for testing damage and death
    static class FakeCombatStatsComponent extends CombatStatsComponent {
        boolean died = false;

        FakeCombatStatsComponent(int health) {
            super(health, 0);
        }

        @Override
        public void handleDeath() {
            died = true;
        }
    }

    @BeforeEach
    void setUp() {
        entityService = new EntityService();
        ServiceLocator.registerEntityService(entityService);
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }


    @Test
    void doesNotDamageDistantEntity() {
        Entity far = new Entity();
        far.setPosition(new Vector2(800, 400)); // far away
        FakeCombatStatsComponent stats = new FakeCombatStatsComponent(100);
        far.addComponent(stats);
        entityService.register(far);

        Entity bomber = new Entity();
        bomber.setPosition(new Vector2(200, 180));
        bomber.addComponent(new BomberDeathExplodeComponent(50, 1));
        entityService.register(bomber);

        bomber.getEvents().trigger("entityDeath");

        assertEquals(100, stats.getHealth(), "Distant target should not be affected");
        assertFalse(stats.died, "Distant target should not die");
    }

    @Test
    void bomberDoesNotDamageItself() {
        Entity bomber = new Entity();
        bomber.setPosition(new Vector2(200, 180));
        FakeCombatStatsComponent selfStats = new FakeCombatStatsComponent(999);
        bomber.addComponent(selfStats);
        bomber.addComponent(new BomberDeathExplodeComponent(999, 5));
        entityService.register(bomber);

        bomber.getEvents().trigger("entityDeath");

        assertEquals(999, selfStats.getHealth(), "Bomber should not harm itself");
        assertFalse(selfStats.died, "Bomber should not mark itself as dead");
    }
//  Wait for defences to be fixed.
//    @Test
//    void damagesNearbyEntity() {
//        // Target placed within explosion radius
//        Entity target = new Entity();
//        target.setPosition(new Vector2(220, 200));
//        FakeCombatStatsComponent stats = new FakeCombatStatsComponent(100);
//        target.addComponent(stats);
//        entityService.register(target);
//
//        // Bomber with explosion
//        Entity bomber = new Entity();
//        bomber.setPosition(new Vector2(200, 180));
//        bomber.addComponent(new BomberDeathExplodeComponent(50, 1));
//        entityService.register(bomber);
//
//        bomber.getEvents().trigger("entityDeath");
//
//        assertEquals(50, stats.getHealth(), "Target health should be reduced by damage");
//        assertTrue(stats.died, "Target should register death");
//    }

//    @Test
//    void hitsMultipleNearbyTargets() {
//        Entity t1 = new Entity();
//        t1.setPosition(new Vector2(210, 200));
//        FakeCombatStatsComponent s1 = new FakeCombatStatsComponent(100);
//        t1.addComponent(s1);
//        entityService.register(t1);
//
//        Entity t2 = new Entity();
//        t2.setPosition(new Vector2(180, 180));
//        FakeCombatStatsComponent s2 = new FakeCombatStatsComponent(120);
//        t2.addComponent(s2);
//        entityService.register(t2);
//
//        Entity bomber = new Entity();
//        bomber.setPosition(new Vector2(200, 180));
//        bomber.addComponent(new BomberDeathExplodeComponent(30, 1));
//        entityService.register(bomber);
//
//        bomber.getEvents().trigger("entityDeath");
//
//        assertEquals(70, s1.getHealth(), "First nearby target should be damaged");
//        assertEquals(90, s2.getHealth(), "Second nearby target should be damaged");
//        assertTrue(s1.died, "First target should have death handled");
//        assertTrue(s2.died, "Second target should have death handled");
//    }
}