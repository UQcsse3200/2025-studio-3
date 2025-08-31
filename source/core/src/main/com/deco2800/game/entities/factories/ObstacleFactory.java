package com.deco2800.game.entities.factories;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.deco2800.game.components.Component;
import com.deco2800.game.entities.Entity;
import com.deco2800.game.physics.PhysicsLayer;
import com.deco2800.game.physics.PhysicsUtils;
import com.deco2800.game.physics.components.ColliderComponent;
import com.deco2800.game.physics.components.PhysicsComponent;
import com.deco2800.game.rendering.TextureRenderComponent;

/**
 * Factory to create obstacle entities.
 *
 * <p>Each obstacle entity type should have a creation method that returns a corresponding entity.
 */
public class ObstacleFactory {

  /**
   * Creates a tree entity.
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
     * creates a projectile entity
     * defines it vertical and linear speed, physics type, size and scale and collision mechanism
     * @return entity
     */
    public static Entity createProjectile() {
        Entity Projectile =
                new Entity()
                        .addComponent(new TextureRenderComponent("images/tree.png"))
                        .addComponent(new PhysicsComponent())
                        .addComponent(new ColliderComponent().setLayer(PhysicsLayer.PROJECTILE));

        Projectile.getComponent(PhysicsComponent.class).setBodyType(BodyType.KinematicBody);
        Projectile.getComponent(TextureRenderComponent.class).scaleEntity();
        Projectile.scaleHeight(3.0f);
        Projectile.scaleWidth(0.3f);
        PhysicsUtils.setScaledCollider(Projectile, 0.2f, 0.8f);
        Vector2 velocity = new Vector2(0, 15f);  // adjust speed as needed
        Projectile.getComponent(PhysicsComponent.class).setLinearVelocity(velocity);
        Projectile.addComponent(new Component() {
            @Override
            public void update(float deltaTime) {
                float y = Projectile.getComponent(PhysicsComponent.class).getBody().getPosition().y;
                if (y> 1000) {  // assuming 1000 is top of your screen/world
                    Projectile.dispose();
                }
            }
        });
        return Projectile;
    }


    /**
   * Creates an invisible physics wall.
   * @param width Wall width in world units
   * @param height Wall height in world units
   * @return Wall entity of given width and height
   */
  public static Entity createWall(float width, float height) {
    Entity wall = new Entity()
        .addComponent(new PhysicsComponent().setBodyType(BodyType.StaticBody))
        .addComponent(new ColliderComponent().setLayer(PhysicsLayer.OBSTACLE));
    wall.setScale(width, height);
    return wall;
  }

  private ObstacleFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
