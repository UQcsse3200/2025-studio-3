package com.csse3200.game.entities.factories;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.ProjectileComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

public class ProjectileFactory {
  /** private constructor prevents instantiation of this utility class. */
  private ProjectileFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }

  public static Entity createSlingShot(int damage, float speed) {
    Entity slingShot =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.PROJECTILE))
            .addComponent(new ProjectileComponent(damage))
            .addComponent(new TouchAttackComponent(PhysicsLayer.ENEMY, 1.5f))
                .addComponent(new CombatStatsComponent(1, damage)); // projectile should die on hit

    // Add render component so it draws above the grid
    TextureRenderComponent render = new TextureRenderComponent("images/sling_projectile.png");
    slingShot.addComponent(render);

    render.scaleEntity(); // mimic human entities to ensure it renders correctly

    PhysicsUtils.setScaledCollider(slingShot, 0.1f, 0.1f);
    return slingShot;
  }
}
