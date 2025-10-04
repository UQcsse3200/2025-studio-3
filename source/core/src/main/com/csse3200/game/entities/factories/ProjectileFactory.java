package com.csse3200.game.entities.factories;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.ProjectileTagComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.ProjectileType;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import java.util.HashMap;
import java.util.Map;

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

  // Static map for path to projectile type
  private static final Map<String, ProjectileType> pathToTypeMap = new HashMap<>();

  static {
    // Initialize the mapping once
    pathToTypeMap.put("images/effects/sling_projectile.png", ProjectileType.SLINGSHOT);
    pathToTypeMap.put("images/effects/bullet.png", ProjectileType.BULLET);
    pathToTypeMap.put("images/effects/harpoon_projectile.png", ProjectileType.HARPOON_PROJECTILE);
    pathToTypeMap.put("images/effects/shock.png", ProjectileType.SHOCK);
    // add more mappings as needed
  }

  public static ProjectileType getProjectileTypeFromPath(String path) {
    return pathToTypeMap.getOrDefault(
        path, ProjectileType.SLINGSHOT); // default type or handle null
  }

  /**
   * Creates and returns a projectile entity given it's sprite path and the damage it deals
   *
   * @param path the file path of the projectile's sprite
   * @param damage amount of damage that the projectile does
   * @return projectile entity
   */
  public static Entity createProjectile(String path, int damage) {
    HitboxComponent hitbox = new HitboxComponent();
    hitbox.setLayer(PhysicsLayer.PROJECTILE);
    hitbox.setSensor(true);
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyDef.BodyType.KinematicBody); // kinematic so it moves but doesn't react
    ColliderComponent collider = new ColliderComponent();
    collider.setSensor(true);
    ProjectileType type = getProjectileTypeFromPath(path);
    Entity proj =
        new Entity()
            .addComponent(physics)
            .addComponent(collider)
            .addComponent(hitbox)
            .addComponent(new ProjectileTagComponent(type))
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
