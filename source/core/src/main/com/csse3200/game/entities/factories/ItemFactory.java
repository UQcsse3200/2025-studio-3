package com.csse3200.game.entities.factories;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.ItemConfigs;
import com.csse3200.game.entities.configs.ItemEntityConfig;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.TextureRenderComponent;

/**
 * Factory to create item entities with predefined components.
 */
public class ItemFactory {
  private static final ItemConfigs configs =
      FileLoader.readClass(ItemConfigs.class, "configs/items.json");

  /**
   * Creates a bomb entity.
   *
   * @return entity
   */
  public static Entity createBomb() {
    Entity bomb = createBaseItem();
    ItemEntityConfig config = configs.bomb;

    bomb.addComponent(new TextureRenderComponent("images/bomb.png"));

    return bomb;
  }

  /**
   * Creates a generic item to be used as a base entity by more specific item creation methods.
   *
   * @return entity
   */
  private static Entity createBaseItem() {
    Entity item =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new PhysicsMovementComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.ALL));

    PhysicsUtils.setScaledCollider(item, 0.9f, 0.4f);
    return item;
  }

  private ItemFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
