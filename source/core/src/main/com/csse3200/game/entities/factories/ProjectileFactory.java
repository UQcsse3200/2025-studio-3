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

  public enum ProjectileType {
    BULLET, SLINGSHOT
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
    Entity slingShot =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
            .addComponent(new TouchAttackComponent(PhysicsLayer.ENEMY, 0))
            .addComponent(new CombatStatsComponent(1, damage)); // projectile should die on hit

    // Add render component so it draws above the grid
    TextureRenderComponent render =
        new TextureRenderComponent("images/effects/sling_projectile.png");
    slingShot.addComponent(render);

    render.scaleEntity(); // mimic human entities to ensure it renders correctly
    PhysicsUtils.setScaledCollider(slingShot, 0.1f, 0.1f);
    return slingShot;
  }

  /**
   * Creates a bullet projectile entity.
   *
   * <p>The bullet is designed to be used by defense entities such as the army guy. It includes
   * components for physics, collision, attack damage, and rendering. The projectile is set to deal
   * damage to enemies and is destroyed upon impact. It fires 4 times faster than a slingshot
   *
   * @param damage amount of damage dealt to an enemy entity
   * @return entity representing a bullet projectile
   */
  public static Entity createBullet(int damage) {
    Entity bullet =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
            .addComponent(new TouchAttackComponent(PhysicsLayer.ENEMY, 0))
            .addComponent(new CombatStatsComponent(1, damage)); // projectile should die on hit

    // Add render component so it draws above the grid
    TextureRenderComponent render = new TextureRenderComponent("images/effects/bullet.png");
    bullet.addComponent(render);

    render.scaleEntity(); // mimic human entities to ensure it renders correctly
    PhysicsUtils.setScaledCollider(bullet, 0.05f, 0.05f);
    return bullet;
  }
}
