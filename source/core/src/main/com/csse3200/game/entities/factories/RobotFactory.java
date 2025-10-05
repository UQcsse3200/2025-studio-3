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
import com.csse3200.game.components.tasks.TeleportTask;
import com.csse3200.game.components.BomberDeathExplodeComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.*;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.services.ConfigService;
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
  public enum RobotType {
    STANDARD,
    FAST,
    TANKY,
    BUNGEE,
    TELEPORT,
      BOMBER
  }

  /** Gets the config service for accessing enemy configurations. */
  private static ConfigService getConfigService() {
    return ServiceLocator.getConfigService();
  }

  /**
   * A basic function to create a specific type of robot depending on the input. make this use
   * constants of some kind. Or EntityConfig classes If an invalid type is given, a standard robot
   * is created
   *
   * @param robotType The type of robot to create
   * @return The created robot
   */
  public static Entity createRobotType(RobotType robotType) {
    ConfigService configService = getConfigService();
    BaseEnemyConfig config = null;
    switch (robotType) {
      case FAST -> config = configService.getEnemyConfig("fastRobot");
      case TANKY -> config = configService.getEnemyConfig("tankyRobot");
      case BUNGEE -> config = configService.getEnemyConfig("bungeeRobot");
      case STANDARD -> config = configService.getEnemyConfig("standardRobot");
      case TELEPORT -> config = configService.getEnemyConfig("teleportRobot");
        case BOMBER -> config = configService.getEnemyConfig("bomberRobot");
    }
    return createBaseRobot(config);
  }

  /**
   * /** Initialises a Base Robot containing the features shared by all robots (e.g. combat stats,
   * movement left, Physics, Hitbox) This robot can be used as a base entity by more specific
   * robots.
   *
   * @param config A config file that contains the robot's stats.
   * @return A robot entity.
   */
  private static Entity createBaseRobot(BaseEnemyConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("BaseEnemyConfig cannot be null when creating robot");
    }

    AITaskComponent aiComponent =
        new AITaskComponent()
            .addTask(new MoveLeftTask(config.getMovementSpeed()))
            .addTask(new RobotAttackTask(90f, PhysicsLayer.NPC));

    // Animation
    final String atlasPath = config.getAtlasPath();
    var rs = ServiceLocator.getResourceService();

    AnimationRenderComponent animator =
        new AnimationRenderComponent(rs.getAsset(atlasPath, TextureAtlas.class));

    // These are the animations that all robots should have
    animator.addAnimation("moveLeft", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attack", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("moveLeftDamaged", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attackDamaged", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("default", 1f, Animation.PlayMode.NORMAL);

    ColliderComponent solid =
        new ColliderComponent()
            .setCollisionFilter(
                PhysicsLayer.ENEMY,
                (short)
                    (PhysicsLayer.DEFAULT | PhysicsLayer.NPC | PhysicsLayer.OBSTACLE) // no ENEMY
                )
            .setFriction(0f);

    Entity robot =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new PhysicsMovementComponent())
            .addComponent(solid)
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.ENEMY))
            .addComponent(new CombatStatsComponent(config.getHealth(), config.getAttack()))
            .addComponent(aiComponent)
            .addComponent(new RobotAnimationController())
            .addComponent(new HitMarkerComponent())
            .addComponent(new TouchAttackComponent(PhysicsLayer.NPC, 0f))
            .addComponent(animator);

    if (config.isTeleportRobot()) {
      float[] laneYs = discoverLaneYsFromTiles();
      // Only attach if we found at least two distinct lanes
      if (laneYs.length >= 2) {
        robot.addComponent(
            new TeleportTask(
                config.getTeleportCooldownSeconds(),
                config.getTeleportChance(),
                config.getMaxTeleports(),
                laneYs));
      }
    }

      // ✅ Add explosion-on-death component for bomber
      if (config.isBomberRobot()) {
          BomberDeathExplodeComponent explodeComp = new BomberDeathExplodeComponent(
                  config.getExplosionDamage(),
                  1.0f
          );
          robot.addComponent(explodeComp);
          System.out.println("[RobotFactory] Added BomberDeathExplodeComponent to " + robot.getId());
      }





    // Scales
    animator.scaleEntity();
    animator.startAnimation("default"); // start an animation

    // This is irrelevant since the robot is rescaled to fit the tile height in LevelGameArea.
    robot.setScale(robot.getScale().x * config.getScale(), robot.getScale().y * config.getScale());

    return robot;
  }

  private static float[] discoverLaneYsFromTiles() {
    var es = com.csse3200.game.services.ServiceLocator.getEntityService();
    if (es == null) return new float[0];

    java.util.Set<Integer> yInts = new java.util.TreeSet<>();

    for (com.csse3200.game.entities.Entity e : es.getEntities()) {
      boolean isTileWithPos =
          e != null
              && e.getComponent(com.csse3200.game.components.tile.TileStorageComponent.class)
                  != null
              && e.getPosition() != null;

      if (!isTileWithPos) {
        continue; // <- only one continue in the whole loop
      }

      var p = e.getPosition();
      yInts.add(Math.round(p.y * 1000f));
    }

    if (yInts.size() < 2) return new float[0];

    float[] ys = new float[yInts.size()];
    int i = 0;
    for (Integer yi : yInts) ys[i++] = yi / 1000f;
    return ys;
  }
}
