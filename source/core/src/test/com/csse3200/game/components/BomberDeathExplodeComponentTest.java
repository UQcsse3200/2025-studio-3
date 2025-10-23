package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
