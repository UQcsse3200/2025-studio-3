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
    BULLET,
    SLINGSHOT,
    SHOCK
  }

    /**
     * Creates and returns a projectile entity given it's sprite path and the damage it deals
     *
     * @param path the file path of the projectile's sprite
     * @param damage amount of damage that the projectile does
     * @return projectile entity
     */
  public static Entity createProjectile(String path, int damage) {
    Entity proj =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
            .addComponent(new TouchAttackComponent(PhysicsLayer.ENEMY, 0))
            .addComponent(new CombatStatsComponent(1, damage)); // projectile should die on hit

    // Add render component so it draws above the grid
    TextureRenderComponent render = new TextureRenderComponent(path);
    proj.addComponent(render);

    render.scaleEntity(); // mimic human entities to ensure it renders correctly
    PhysicsUtils.setScaledCollider(proj, 0.1f, 0.1f);
    return proj;
  }
}
