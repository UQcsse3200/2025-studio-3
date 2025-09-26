package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.npc.DefenceAnimationController;
import com.csse3200.game.components.tasks.AttackTask;
import com.csse3200.game.components.tasks.IdleTask;
import com.csse3200.game.components.tasks.TargetDetectionTasks;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;

// TODO - provide documentation for refactored functions

/**
 * Factory class for creating defence entities (e.g., sling shooters). This class should not be
 * instantiated — all methods and configuration are static utilities.
 */
public class DefenceFactory {
  private static final String ATTACK = "attack";
  private static final String IDLE = "idle";

  /** Gets the config service for accessing defence configurations. */
  private static ConfigService getConfigService() {
    return ServiceLocator.getConfigService();
  }

  public static Entity createDefenceUnit(BaseDefenderConfig config) {
    // start with a base defender (physics + collider)
    Entity defender = createBaseDefender();

    AITaskComponent tasks = getTaskComponent(config);
    defender.addComponent(tasks);
    // animation component
    AnimationRenderComponent animator = getAnimationComponent(config);
    // stats component
    DefenderStatsComponent stats = getUnitStats(config);

    // attach components to the entity
    defender
        .addComponent(stats)
        .addComponent(animator)
        .addComponent(new DefenceAnimationController());

    // Scale to tilesize
    animator.scaleEntity();
    return defender;
  }

  public static DefenderStatsComponent getUnitStats(BaseDefenderConfig config) {
    DefenderStatsComponent stats =
        new DefenderStatsComponent(
            config.getHealth(),
            config.getAttack(),
            config.getRangeType(),
            config.getRange(),
            config.getAttackState(),
            config.getAttackSpeed(),
            config.getCritChance());

    return stats;
  }

  public static AITaskComponent getTaskComponent(BaseDefenderConfig config) {
    // TODO - differentiate between different configs to decide attack direction and projectile type
    AITaskComponent tasks =
        new AITaskComponent()
            .addTask(
                new AttackTask(
                    config.getRange(),
                    ProjectileFactory.ProjectileType.BULLET,
                    TargetDetectionTasks.AttackDirection.RIGHT))
            .addTask(new IdleTask(config.getRange(), TargetDetectionTasks.AttackDirection.LEFT));

    return tasks;
  }

  public static AnimationRenderComponent getAnimationComponent(BaseDefenderConfig config) {
    AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService()
                .getAsset(config.getAtlasPath(), TextureAtlas.class));

    // define animations for idle and attack states
    animator.addAnimation(IDLE, 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation(ATTACK, 0.04f, Animation.PlayMode.LOOP);

    return animator;
  }

  /**
   * Creates a wall entity with entity collision
   *
   * @return the wall entity to be placed
   */
  public static Entity createWall() {
    BaseDefenderConfig config = getConfigService().getDefenderConfig("wall");
    // start with a base defender (physics + collider)
    Entity wall = createBaseDefender();

    // animation component
    AnimationRenderComponent animator = getAnimationComponent(config);
    // stats component
    DefenderStatsComponent stats = getUnitStats(config);
    // attach components to the entity
    wall.addComponent(stats).addComponent(animator).addComponent(new DefenceAnimationController());

    // Scale to tilesize
    animator.scaleEntity();
    return wall;
  }

  /**
   * Creates a base defender entity with default physics and collider setup.
   *
   * @return entity with physics and collision components
   */
  public static Entity createBaseDefender() {

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
  private DefenceFactory() {
    throw new IllegalStateException("Instantiating static util class");
  }
}
