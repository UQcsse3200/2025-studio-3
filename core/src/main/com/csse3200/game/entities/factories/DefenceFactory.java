package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.ProjectileComponent;
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

/**
 * Factory class for creating defence entities (e.g., sling shooters). This class should not be
 * instantiated — all methods and configuration are static utilities.
 */
public class DefenceFactory {
  private static final String ATTACK = "attack";
  private static final String IDLE = "idle";
  private static final String BUFF = "doubleDamage";
  private static final String UNBUFF = "doubleDamageStop";
  private static final String HEAL = "heal";

  /** Gets the config service for accessing defence configurations. */
  private static ConfigService getConfigService() {
    return ServiceLocator.getConfigService();
  }

  /**
   * Create a defence unit based on the config class that is passed into the function, which
   * contains health, damage, range, attack speed, crit chance and asset paths
   *
   * @param config configuration class describing the entity's stats
   * @return new defence entity
   */
  public static Entity createDefenceUnit(BaseDefenderConfig config) {
    // start with a base defender (physics + collider)
    Entity defender = createBaseDefender();
    if (config.getName() != null && config.getName().equals("Mortar")) {
      defender.setProperty("unitType", "mortar");
    }

    // --- Create and attach task component ---
    TargetDetectionTasks.AttackDirection dir = TargetDetectionTasks.AttackDirection.RIGHT;
    if (config.getDirection().equals("left")) {
      dir = TargetDetectionTasks.AttackDirection.LEFT;
    }

    AttackTask attackTask = new AttackTask(config.getRange(), config.getAttackSpeed(), dir);
    IdleTask idleTask = new IdleTask(config.getRange(), dir);
    AITaskComponent tasks = new AITaskComponent().addTask(attackTask).addTask(idleTask);

    // Attach AI tasks
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
    if (config.getProjectilePath() != null) {
      defender.addComponent(
          new ProjectileComponent(config.getProjectilePath(), config.getDamage()));
    }

    // Scale to tilesize
    animator.scaleEntity();

    // add event listener for buffing the defence when a buff item is used on it
    defender
        .getEvents()
        .addListener(BUFF, () -> defender.getComponent(DefenderStatsComponent.class).buff());
    defender
        .getEvents()
        .addListener(UNBUFF, () -> defender.getComponent(DefenderStatsComponent.class).unbuff());
    defender
        .getEvents()
        .addListener(HEAL, () -> defender.getComponent(DefenderStatsComponent.class).addHealth(20));

    // Wire up fire-rate event listeners directly — no iteration
    defender.getEvents().addListener("doubleFireRate", attackTask::enableDoubleFireRate);
    defender.getEvents().addListener("doubleFireRateStop", attackTask::resetFireRate);

    // add sound path
    defender.setProperty("soundPath", config.getSoundPath());
    return defender;
  }

  /**
   * Construct a stats component for the defence unit given its config class
   *
   * @param config class containing defence stats
   * @return stats component to be added to an entity
   */
  public static DefenderStatsComponent getUnitStats(BaseDefenderConfig config) {
    return new DefenderStatsComponent(
        config.getHealth(),
        config.getDamage(),
        config.getRange(),
        config.getAttackSpeed(),
        config.getCritChance(),
        config.getCost());
  }

  /**
   * Construct a task component for allowing the defence to idle and attack, dependent on attack
   * speed, range and direction
   *
   * @param config class containing all stats of the unit
   * @return task component for the entity
   */
  public static AITaskComponent getTaskComponent(BaseDefenderConfig config) {
    TargetDetectionTasks.AttackDirection dir = TargetDetectionTasks.AttackDirection.RIGHT;
    if (config.getDirection().equals("left")) {
      dir = TargetDetectionTasks.AttackDirection.LEFT;
    }

    return new AITaskComponent()
        .addTask(new AttackTask(config.getRange(), config.getAttackSpeed(), dir))
        .addTask(new IdleTask(config.getRange(), dir));
  }

  /**
   * Get the animation component to be attached to the entity
   *
   * @param config class containing the atlas path of the entity
   * @return animation component for the entity
   */
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
                (short)
                    (PhysicsLayer.DEFAULT
                        | PhysicsLayer.OBSTACLE
                        | PhysicsLayer.ENEMY
                        | PhysicsLayer.BOSS));

    Entity npc =
        new Entity()
            .addComponent(solid)
            .addComponent(new PhysicsComponent().setBodyType(BodyDef.BodyType.StaticBody))
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
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
