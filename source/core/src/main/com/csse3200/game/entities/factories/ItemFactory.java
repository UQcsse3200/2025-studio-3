package com.csse3200.game.entities.factories;

import com.csse3200.game.components.items.*;
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
   * Creates a grenade entity. Damages enemies in a short radius.
   *
   * @return entity
   */
  public static Entity createGrenade() {
    return createBaseItem()
            .addComponent(new TextureRenderComponent("images/item_grenade.png"))
            .addComponent(new GrenadeComponent());
  }

  /**
   * Creates a coffee entity. Place on a "plant" to give it a temporary attack speed bonus.
   *
   * @return entity
   */
  public static Entity createCoffee() {
    return createBaseItem()
            .addComponent(new TextureRenderComponent("images/item_coffee.png"))
            .addComponent(new CoffeeComponent());
  }

  /**
   * Creates a buff entity. Place on a "plant" to heal it and double it's max health.
   *
   * @return entity
   */
  public static Entity createBuff() {

    return createBaseItem()
            .addComponent(new TextureRenderComponent("images/item_buff.png"))
            .addComponent(new BuffComponent());
  }

  /**
   * Creates an EMP entity. Stuns enemies in a short radius.
   *
   * @return entity
   */
  public static Entity createEmp() {

    return createBaseItem()
            .addComponent(new TextureRenderComponent("images/item_emp.png"))
            .addComponent(new EmpComponent());
  }

  /**
   * Creates a nuke entity. Place anywhere on screen to destroy all entities, friend & foe.
   *
   * @return entity
   */
  public static Entity createNuke() {

      return createBaseItem()
            .addComponent(new TextureRenderComponent("images/item_nuke.png"))
            .addComponent(new NukeComponent());
  }

  /**
   * Creates a generic item to be used as a base entity by more specific item creation methods.
   *
   * @return entity
   */
  private static Entity createBaseItem() {
    // components may need to be changed (may not want collision)
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
