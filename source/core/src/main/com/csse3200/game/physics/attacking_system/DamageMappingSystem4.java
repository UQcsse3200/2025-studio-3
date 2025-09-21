package com.csse3200.game.physics.attacking_system;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.BodyUserData;

/**
 * the damage mapping system handles damage interactions between entities. It listens to
 * collisionStart event and applies the damage logic.
 */
public class DamageMappingSystem4 {

  /**
   * Creates a new damage mapping system for the specified entity.
   *
   * @param entity the entity to attach the damage system to
   */
  public DamageMappingSystem4(Entity entity) {
    entity.getEvents().addListener("collisionStart", this::onCollisionStart);
  }

  /**
   * Handles collision start events and applies damage logic.
   *
   * @param fixtureA the first fixture in the collision
   * @param fixtureB the second fixture in the collision
   */
  public void onCollisionStart(Fixture fixtureA, Fixture fixtureB) {

    Entity entityA = ((BodyUserData) fixtureA.getBody().getUserData()).getEntity();
    Entity entityB = ((BodyUserData) fixtureB.getBody().getUserData()).getEntity();
    if (entityA == null || entityB == null) return;

    Boolean isProjectile = (Boolean) entityA.getProperty("isProjectile");
    if (isProjectile == null || !isProjectile) return;
    CombatStatsComponent attackerStats = entityA.getComponent(CombatStatsComponent.class);
    CombatStatsComponent victimStats = entityB.getComponent(CombatStatsComponent.class);

    if (attackerStats != null && victimStats != null) {
      victimStats.hit(attackerStats);
      if (isProjectile) {
        entityA.getEvents().trigger("destroy");
      }
    }
  }
}
