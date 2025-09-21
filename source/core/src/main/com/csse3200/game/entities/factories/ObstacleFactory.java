package com.csse3200.game.entities.factories;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.attacking_system.DamageMappingSystem4;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

/**
 * Factory to create obstacle entities.
 *
 * <p>Each obstacle entity type should have a creation method that returns a corresponding entity.
 */
public class ObstacleFactory {

  /**
   * Creates a tree entity.
   *
   * @return entity
   */
  public static Entity createTree() {
    Entity tree =
        new Entity()
            .addComponent(new TextureRenderComponent("images/tree.png"))
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));

    tree.getComponent(PhysicsComponent.class).setBodyType(BodyType.StaticBody);
    tree.getComponent(TextureRenderComponent.class).scaleEntity();
    tree.scaleHeight(2.5f);
    PhysicsUtils.setScaledCollider(tree, 0.5f, 0.2f);
    return tree;
  }

  /**
   * creates a laser projectile entity
   *
   * @return entity
   */
  public static Entity createLaser() {
    Entity laser =
        new Entity()
            .addComponent(new TextureRenderComponent("images/laser.png"))
            .addComponent(new PhysicsComponent())
            .addComponent(
                new ColliderComponent().setLayer(PhysicsLayer.PROJECTILE).setSensor(false))
            .addComponent(new CombatStatsComponent(1, 0)) // damage
            .addComponent(new TouchAttackComponent(PhysicsLayer.ENEMY))
            .addComponent(new HitboxComponent());
    laser.getComponent(PhysicsComponent.class).setBodyType(BodyType.KinematicBody);
    laser.getComponent(TextureRenderComponent.class).scaleEntity();
    laser.scaleHeight(2.0f);
    laser.scaleWidth(0.2f);
    laser.setProperty("isProjectile", true);
    laser.getEvents().addListener("destroy", laser::dispose);
    PhysicsUtils.setScaledCollider(laser, 0.2f, 0.8f);
    new DamageMappingSystem4(laser);
    laser.getComponent(PhysicsComponent.class).setLinearVelocity(5f, 0f);
    laser.getComponent(TextureRenderComponent.class).scaleEntity();
    laser.scaleHeight(1.0f); // adjust size as needed
    PhysicsUtils.setScaledCollider(laser, 0.2f, 0.8f);

    return laser;
  }

  /**
   * Creates an invisible physics wall.
   *
   * @param width Wall width in world units
   * @param height Wall height in world units
   * @return Wall entity of given width and height
   */
  public static Entity createWall(float width, float height) {
    Entity wall =
        new Entity()
            .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
            .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
    wall.setScale(width, height);
    return wall;
  }

  private ObstacleFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
