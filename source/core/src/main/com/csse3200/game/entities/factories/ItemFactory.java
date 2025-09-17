package com.csse3200.game.entities.factories;

import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;

/** Factory to create item entities with predefined components. */
public class ItemFactory {
  private static final ConfigService configService = ServiceLocator.getConfigService();

  /**
   * Creates a grenade entity. Damages enemies in a short radius.
   *
   * @return a grenade item entity
   */
  public static Entity createGrenade() {
    BaseItemConfig config = configService.getItemConfig("grenade");
    return createBaseItem()
        .addComponent(new TextureRenderComponent(config.getAssetPath()))
        .addComponent(new ItemComponent(ItemComponent.Type.GRENADE));
  }

  /**
   * Creates a coffee entity. Place on a "plant" to give it a temporary attack speed bonus.
   *
   * @return a coffee item entity
   */
  public static Entity createCoffee() {
    BaseItemConfig config = configService.getItemConfig("coffee");
    return createBaseItem()
        .addComponent(new TextureRenderComponent(config.getAssetPath()))
        .addComponent(new ItemComponent(ItemComponent.Type.COFFEE));
  }

  /**
   * Creates a buff entity. Place on a "plant" to heal it and double it's max health.
   *
   * @return a buff item entity
   */
  public static Entity createBuff() {
    BaseItemConfig config = configService.getItemConfig("buff");
    return createBaseItem()
        .addComponent(new TextureRenderComponent(config.getAssetPath()))
        .addComponent(new ItemComponent(ItemComponent.Type.BUFF));
  }

  /**
   * Creates an EMP entity. Stuns enemies in a short radius.
   *
   * @return an emp item entity
   */
  public static Entity createEmp() {
    BaseItemConfig config = configService.getItemConfig("emp");
    return createBaseItem()
        .addComponent(new TextureRenderComponent(config.getAssetPath()))
        .addComponent(new ItemComponent(ItemComponent.Type.EMP));
  }

  /**
   * Creates a nuke entity. Place anywhere on screen to destroy all entities, friend and foe.
   *
   * @return a nuke item entity
   */
  public static Entity createNuke() {
    BaseItemConfig config = configService.getItemConfig("nuke");
    return createBaseItem()
        .addComponent(new TextureRenderComponent(config.getAssetPath()))
        .addComponent(new ItemComponent(ItemComponent.Type.NUKE));
  }

  /**
   * Creates a generic item to be used as a base entity by more specific item creation methods.
   *
   * @return an item entity
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

  /** Private constructor to prevent instantiation of this utility class. */
  private ItemFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
