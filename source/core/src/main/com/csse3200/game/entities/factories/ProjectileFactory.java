package com.csse3200.game.entities.factories;

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
    pathToTypeMap.put("images/effects/shell.png", ProjectileType.SHELL);
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
    ProjectileType type = getProjectileTypeFromPath(path);
    ColliderComponent collider = new ColliderComponent();
    if (type == ProjectileType.HARPOON_PROJECTILE) {
      collider.setSensor(true);
    }
    Entity proj = new Entity();
    if (type
        != ProjectileType
            .SHELL) { // the mortar shell projectile doesn't get physics, its purely visual
      proj.addComponent(new PhysicsComponent())
          .addComponent(collider)
          .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
          .addComponent(new TouchAttackComponent(PhysicsLayer.ENEMY, 0))
          .addComponent(new CombatStatsComponent(1, damage)); // projectile should die on hit
    }

    // Add render component so it draws above the grid
    TextureRenderComponent render = new TextureRenderComponent(path);
    proj.addComponent(render);
    proj.addComponent(new ProjectileTagComponent(type));

    render.scaleEntity(); // mimic human entities to ensure it renders correctly
    if (type != ProjectileType.SHELL) { // no collider for the mortar shell
      PhysicsUtils.setScaledCollider(proj, 0.1f, 0.1f);
    }
    return proj;
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
