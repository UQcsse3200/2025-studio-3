package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.npc.DefenceAnimationController;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public class GeneratorFactory {
  private static final String HIT = "hit";
  private static final String IDLE = "idle";
  private static final String HEAL = "heal";

  public static Entity createGeneratorUnit(BaseGeneratorConfig config) {
    // start with a base defender (physics + collider)
    Entity defender = createBaseGenerator();
    // animation component
    AnimationRenderComponent animator = getAnimationComponent(config);
    // stats component
    GeneratorStatsComponent stats = getUnitStats(config);

    // attach components to the entity
    defender
        .addComponent(stats)
        .addComponent(animator)
        .addComponent(new DefenceAnimationController());

    // Scale to tilesize
    animator.scaleEntity();

    defender.getEvents().addListener(HEAL,
        () -> defender.getComponent(DefenderStatsComponent.class).addHealth(20));
    return defender;
  }

  public static GeneratorStatsComponent getUnitStats(BaseGeneratorConfig config) {
    return new GeneratorStatsComponent(
        config.getHealth(), config.getInterval(), config.getScrapValue());
  }

  public static AnimationRenderComponent getAnimationComponent(BaseGeneratorConfig config) {
    AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService()
                .getAsset(config.getAtlasPath(), TextureAtlas.class));

    // define animations for idle and attack states
    animator.addAnimation(IDLE, 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation(HIT, 0.04f, Animation.PlayMode.LOOP);

    return animator;
  }

  /**
   * Creates a base generator entity with default physics and collider setup.
   *
   * @return entity with physics and collision components
   */
  public static Entity createBaseGenerator() {

    ColliderComponent solid =
        new ColliderComponent()
            .setCollisionFilter(
                PhysicsLayer.NPC,
                (short) (PhysicsLayer.DEFAULT | PhysicsLayer.OBSTACLE | PhysicsLayer.ENEMY));

    Entity npc =
        new Entity()
            .addComponent(solid)
            .addComponent(new PhysicsComponent().setBodyType(BodyDef.BodyType.StaticBody))
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
            .addComponent(new ColliderComponent())
            .addComponent(new HitMarkerComponent());

    npc.getComponent(PhysicsComponent.class).setBodyType(BodyDef.BodyType.StaticBody);
    PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
    return npc;
  }

  /** private constructor prevents instantiation of this utility class. */
  private GeneratorFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
