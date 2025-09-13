package com.csse3200.game.entities.factories;

import com.csse3200.game.components.ProjectileComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;

public class ProjectileFactory {
  /** private constructor prevents instantiation of this utility class. */
  private ProjectileFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }

  public static Entity createSlingShot(int damage) {
    Entity slingShot =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
            .addComponent(new ProjectileComponent(damage));

    PhysicsUtils.setScaledCollider(slingShot, 0.1f, 0.1f);
    return slingShot;
  }
}
