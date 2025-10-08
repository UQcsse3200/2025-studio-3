package com.csse3200.game.entities.factories;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

/**
 * Factory class for creating projectile entities for defense entities (e.g., sling shoots for sling
 * shooters). This class should not be instantiated — all methods and configuration are static
 * utilities.
 */
public class ProjectileFactory {
  /** private constructor prevents instantiation of this utility class. */
  private ProjectileFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }

  /**
   * Creates a sling shot projectile entity.
   *
   * <p>The sling shot is designed to be used by defense entities such as sling shooters. It
   * includes components for physics, collision, attack damage, and rendering. The projectile is set
   * to deal damage to enemies and is destroyed upon impact.
   *
   * @param damage amount of damage dealt to an enemy entity
   * @param speed the speed the sling shot moves at
   * @return entity representing a sling shot projectile
   */
  public static Entity createSlingShot(int damage, float speed) {

    short targetLayers = (short) (PhysicsLayer.ENEMY | PhysicsLayer.BOSS);

    // --- BUG FIX STARTS HERE ---
    // Create and configure the collider separately to add a collision filter.
    ColliderComponent collider = new ColliderComponent();
    // This filter makes the projectile collide ONLY with enemies and bosses.
    // It will pass through friendly NPCs, preventing the "flinging" bug.
    collider.setCollisionFilter(PhysicsLayer.PROJECTILE, targetLayers);
    // --- BUG FIX ENDS HERE ---

    Entity slingShot =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(collider) // Add the configured collider
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
            .addComponent(new TouchAttackComponent(targetLayers, 0))
            .addComponent(new CombatStatsComponent(1, damage)); // projectile should die on hit

    // Add render component so it draws above the grid
    TextureRenderComponent render =
        new TextureRenderComponent("images/effects/sling_projectile.png");
    slingShot.addComponent(render);

    render.scaleEntity(); // mimic human entities to ensure it renders correctly
    PhysicsUtils.setScaledCollider(slingShot, 0.1f, 0.1f);
    return slingShot;
  }

  public static Entity createBossProjectile(int damage) {
    short targetLayers = PhysicsLayer.NPC;
    ColliderComponent collider = new ColliderComponent();
    collider.setCollisionFilter(PhysicsLayer.BOSS_PROJECTILE, targetLayers);
    Entity bossProjectile =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(collider)
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.BOSS_PROJECTILE))
            .addComponent(new TouchAttackComponent(targetLayers, 0f))
            .addComponent(new CombatStatsComponent(1, damage));
    bossProjectile.addComponent(new TextureRenderComponent("images/effects/gun_bot_fireball.png"));
    bossProjectile.getComponent(TextureRenderComponent.class).scaleEntity();
    PhysicsUtils.setScaledCollider(bossProjectile, 0.2f, 0.2f);
    return bossProjectile;
  }
}
