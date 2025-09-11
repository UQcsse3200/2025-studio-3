package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.DefenceStatsComponent;
import com.csse3200.game.components.npc.DefenceAnimationController;
import com.csse3200.game.components.tasks.AttackTask;
import com.csse3200.game.components.tasks.IdleTask;
// import com.csse3200.game.components.WanderTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenceConfig;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import java.util.List;

/**
 * Factory class for creating defence entities (e.g., sling shooters). This class should not be
 * instantiated — all methods and configuration are static utilities.
 */
public class DefenceFactory {
  /**
   * Loads defence configuration data from JSON. The configs object is populated at class-load time.
   * If the file is missing or deserialization fails, this will be null.
   */
  private static final NPCConfigs configs =
      FileLoader.readClass(NPCConfigs.class, "configs/Defences.json");

  /**
   * Creates a fully configured Sling Shooter defence entity.
   *
   * <p>The entity is composed of: - Base physics and collider setup - Stats loaded from the config
   * file - Animation rendering and animation controller
   *
   * @param targets the list of the entities that the slingshooter will attack
   * @return entity representing the slingshooter
   */
  public static Entity createSlingShooter(List<Entity> targets) {
    // load the sling shooter’s specific configuration;
    BaseDefenceConfig config = configs.slingshooter;

    // start with a base defender (physics + collider)
    Entity defender = createBaseDefender(targets, config);

    // animation component
    AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService()
                .getAsset("images/sling_shooter.atlas", TextureAtlas.class));

    // define animations for idle and attack states
    animator.addAnimation("idle", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attack", 0.04f, Animation.PlayMode.LOOP);

    // attach components to the entity
    defender
        .addComponent(
            new DefenceStatsComponent(
                config.getHealth(),
                config.getAttack(),
                config.rangeType,
                config.range,
                config.state,
                config.attackSpeed,
                config.critChance))
        .addComponent(animator)
        .addComponent(new DefenceAnimationController());

    // Scale to tilesize
    animator.scaleEntity();

    // scale the entity to match animation sprite dimensions
    defender.getComponent(AnimationRenderComponent.class).scaleEntity();

    return defender;
  }

  public static Entity createForge(List<Entity> targets) {
    // load the sling shooter’s specific configuration;
    BaseDefenceConfig config = configs.forge;

    // start with a base defender (physics + collider)
    Entity defender = createBaseDefender(targets, config);

    // animation component
    AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService().getAsset("images/forge.atlas", TextureAtlas.class));

    // define animations for idle and attack states
    animator.addAnimation("idle", 0.1f, Animation.PlayMode.LOOP);
    // attach components to the entity
    defender
        .addComponent(
            new DefenceStatsComponent(
                config.getHealth(),
                config.getAttack(),
                config.rangeType,
                config.range,
                config.state,
                config.attackSpeed,
                config.critChance))
        .addComponent(animator)
        .addComponent(new DefenceAnimationController());

    // trigger the initial attack event to kick off behaviour
    // this will be changed to idle once idle is made
    defender.getEvents().trigger("idleStart");

    // scale the entity to match animation sprite dimensions
    defender.getComponent(AnimationRenderComponent.class).scaleEntity();

    return defender;
  }

  /**
   * Creates a base defender entity with default physics and collider setup. Base defender is also
   * able to detect enemies and act accordingly - i.e. remain idle or attack
   *
   * @return entity with physics and collision components
   */
  public static Entity createBaseDefender(List<Entity> targets, BaseDefenceConfig config) {
    AITaskComponent enemyDetectionTasks =
        new AITaskComponent()
            .addTask(new AttackTask(targets, config.range))
            .addTask(new IdleTask(targets, config.range));

    Entity npc =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
            .addComponent(enemyDetectionTasks);

    PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
    return npc;
  }

  /** private constructor prevents instantiation of this utility class. */
  private DefenceFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
