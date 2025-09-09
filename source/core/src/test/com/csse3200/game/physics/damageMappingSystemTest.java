package com.csse3200.game.physics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.attacking_system.damageMappingSystem;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DamageMappingSystemTest {
  private Entity attacker;
  private Entity defender;
  private CombatStatsComponent attackerStats;
  private CombatStatsComponent defenderStats;
  private Fixture attackerFixture;
  private Fixture defenderFixture;
  private damageMappingSystem damageSystem;

  @BeforeEach
  void setUp() {
    attacker = new Entity();
    attackerStats = new CombatStatsComponent(100, 10);
    attacker.addComponent(attackerStats);
    attacker.addComponent(mock(HitboxComponent.class));
    attacker.create();
    attacker.setProperty("isProjectile", true);

    defender = new Entity();
    defenderStats = new CombatStatsComponent(50, 5);
    defender.addComponent(defenderStats);
    defender.addComponent(mock(ColliderComponent.class));
    defender.create();

    damageSystem = new damageMappingSystem(attacker);

    attackerFixture = mock(Fixture.class);
    var attackerBody = mock(com.badlogic.gdx.physics.box2d.Body.class);
    BodyUserData attackerData = new BodyUserData();
    attackerData.entity = attacker;
    when(attackerFixture.getBody()).thenReturn(attackerBody);
    when(attackerBody.getUserData()).thenReturn(attackerData);

    defenderFixture = mock(Fixture.class);
    var defenderBody = mock(com.badlogic.gdx.physics.box2d.Body.class);
    BodyUserData defenderData = new BodyUserData();
    defenderData.entity = defender;
    when(defenderFixture.getBody()).thenReturn(defenderBody);
    when(defenderBody.getUserData()).thenReturn(defenderData);
  }

  @Test
  void DamgaeIsApplied() {
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertEquals(40, defenderStats.getHealth(), "Defender should lose 10HP.");
  }

  @Test
  void DeathTriggered() {
    defenderStats.setHealth(5);
    final boolean[] deathTriggered = {false};
    defender.getEvents().addListener("death", () -> deathTriggered[0] = true);
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertTrue(defenderStats.getHealth() <= 0, "Defender HP should be <=0");
  }

  @Test
  void NoDamageIfNoCombatStats() {
    Entity temp = new Entity();
    temp.create();

    Fixture tempFixture = mock(Fixture.class);
    var tempBody = mock(com.badlogic.gdx.physics.box2d.Body.class);
    BodyUserData tempData = new BodyUserData();
    tempData.entity = temp;
    when(tempFixture.getBody()).thenReturn(tempBody);
    when(tempBody.getUserData()).thenReturn(tempData);
    damageSystem.onCollisionStart(attackerFixture, tempFixture);
    assertEquals(
        50, defenderStats.getHealth(), "Entity without CombatStats should not take Damage");
  }

  @Test
  void DestroyTriggered() {
    final boolean[] destroyTriggered = {false};
    attacker.getEvents().addListener("destroy", () -> destroyTriggered[0] = true);
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertTrue(destroyTriggered[0], "Projectile should trigger destroy event.");
  }

  @Test
  void NoDamageIfNotProjectile() {
    attacker.setProperty("isProjectile", false);
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertEquals(50, defenderStats.getHealth(), "Non-Projectile should not deal damage");
  }
}
