package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.npc.RobotAnimationController;
import com.csse3200.game.components.tasks.MoveLeftTask;
import com.csse3200.game.components.tasks.RobotAttackTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.*;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * Factory to create non-playable character (NPC) entities with predefined components.
 *
 * <p>Each NPC entity type should have a creation method that returns a corresponding entity.
 * Predefined entity properties can be loaded from configs stored as json files which are defined in
 * "NPCConfigs".
 *
 * <p>If needed, this factory can be separated into more specific factories for entities with
 * similar characteristics.
 */
public class RobotFactory {
  /**
   * Loads enemy config data from JSON. The configs object is populated at class-load time. If the
   * file is missing or deserialization fails, this will be null.
   */
  private static final NPCConfigs configs =
      FileLoader.readClass(NPCConfigs.class, "configs/enemies.json");

  /**
   * A basic function to create a specific type of robot depending on the input TODO make this use
   * constants of some kind. Or EntityConfig classes If an invalid type is given, a standard robot
   * is created
   *
   * @param robotType The type of robot to create
   * @return The created robot
   */
  public static Entity createRobotType(String robotType) {
    BaseEnemyConfig config;
    if (robotType.equalsIgnoreCase("fast")) {
      config = configs.fastRobot;
    } else if (robotType.equalsIgnoreCase("tanky")) {
      config = configs.tankyRobot;
    } else if (robotType.equalsIgnoreCase("bungee")) {
      config = configs.bungeeRobot;
    } else {
      config = configs.standardRobot;
    }
    return createBaseRobot(config);
  }

  /**
   * Initialises a Base Robot containing the features shared by all robots (e.g. combat stats,
   * movement left, Physics, Hitbox) This robot can be used as a base entity by more specific
   * robots.
   *
   * @param config A config file that contains the robot's stats.
   * @return A robot entity.
   */
  private static Entity createBaseRobot(BaseEnemyConfig config) {

    AITaskComponent aiComponent =
        new AITaskComponent()
            .addTask(new MoveLeftTask(config.movementSpeed))
            .addTask(new RobotAttackTask(90f, PhysicsLayer.NPC));

    // Animation
    final String atlasPath = config.atlasFilePath;
    var rs = ServiceLocator.getResourceService();

    AnimationRenderComponent animator =
        new AnimationRenderComponent(rs.getAsset(atlasPath, TextureAtlas.class));

    animator.addAnimation("chill", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("angry", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attack", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("default", 1f, Animation.PlayMode.NORMAL);

    // We could also do
    // .addComponent(new RobotAnimationController())
    // but that isn't really implemented

    Entity robot =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new PhysicsMovementComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.ENEMY))
            .addComponent(new CombatStatsComponent(config.health, config.attack))
            .addComponent(aiComponent)
            .addComponent(new RobotAnimationController())
            .addComponent(new HitMarkerComponent())
            .addComponent(new TouchAttackComponent(PhysicsLayer.NPC, 0f))
            .addComponent(animator);

    // Scales
    animator.scaleEntity();
    animator.startAnimation("chill"); // start an animation

    // This is irrelevant since the robot is rescaled to fit the tile height in LevelGameArea.
    robot.setScale(robot.getScale().x * config.scale, robot.getScale().y * config.scale);

    return robot;

    // The original NPCFactory had:
    // PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
    // and also .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
    // I don't think we need that but I'm putting it here for reference
  }
}
