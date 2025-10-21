package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.BomberDeathExplodeComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.npc.RobotAnimationController;
import com.csse3200.game.components.tasks.*;
import com.csse3200.game.components.worldmap.CoinRewardedComponent;
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
import org.slf4j.Logger;

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
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RobotFactory.class);

  public enum RobotType {
    STANDARD("standardRobot"),
    FAST("fastRobot"),
    TANKY("tankyRobot"),
    BUNGEE("bungeeRobot"),
    TELEPORT("teleportRobot"),
    JUMPER("jumperRobot"),
    GUNNER("gunnerRobot"),
    GIANT("giantRobot"),
    MINI("miniRobot"),
    BOMBER("bomberRobot");

    private final String configKey;

    RobotType(String configKey) {
      this.configKey = configKey;
    }

    public String get() {
      return configKey;
    }

    /**
     * Converts a string into the corresponding RobotType. If type is null or invalid, the RobotType
     * will default to STANDARD. Matching logic is case-insensitive and can use either enum name or
     * config key REFERENCE: This was written with ChatGPT
     *
     * @param type The robot type, in string form
     * @return The corresponding RobotType. Will be standard if type is invalid
     */
    public static RobotType fromString(String type) {
      if (type == null) {
        logger.info("type is null. Defaulting to STANDARD RobotType.");
        return STANDARD;
      }
      String normalised = type.trim().toLowerCase();
      // This allows the Robot part to be removed. e.g. "fast" will still count as fastRobot
      // The levels json file does not include the "Robot" part, so this accounts for that.
      String normalised2 = normalised + "Robot";

      for (RobotType robotType : values()) {
        if (robotType.name().equalsIgnoreCase(normalised)
            || robotType.configKey.equalsIgnoreCase(normalised)
            || robotType.name().equalsIgnoreCase(normalised2)) {
          return robotType;
        }
      }

      logger.info("type is invalid. Defaulting to STANDARD RobotType.");
      return STANDARD; // Default fallback
    }
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
    BaseEnemyConfig config = configService.getEnemyConfig(robotType.get());
    if (config == null) {
      config = configService.getEnemyConfig(RobotType.STANDARD.get());
    }
    return createBaseRobot(config);
  }

  /**
   * Creates a Teleport Robot with teleport behaviour attached.
   *
   * @param cfg Teleport robot config (stats and teleport params)
   * @param laneYs Candidate lane Y positions to teleport between (must contain at least 2)
   * @return Entity with base robot components plus TeleportTask
   */

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
        new AITaskComponent().addTask(new MoveLeftTask(config.getMovementSpeed()));

    // Animation
    final String atlasPath = config.getAtlasPath();
    var rs = ServiceLocator.getResourceService();

    AnimationRenderComponent animator =
        new AnimationRenderComponent(rs.getAsset(atlasPath, TextureAtlas.class));

    // These are the animations that all robots should have
    animator.addAnimation("moveLeft", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attack", 0.05f, Animation.PlayMode.LOOP);
    animator.addAnimation("moveLeftDamaged", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attackDamaged", 0.05f, Animation.PlayMode.LOOP);
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
            .addComponent(new CoinRewardedComponent(config.getCoinsRewarded()))
            .addComponent(animator);

    // Default attack type is melee if not specified
    if (config.getAttackType() == null) {
      robot.getComponent(AITaskComponent.class).addTask(new RobotAttackTask(20f, PhysicsLayer.NPC));
    } else if (config.getAttackType().equals("melee")) {
      robot.getComponent(AITaskComponent.class).addTask(new RobotAttackTask(20f, PhysicsLayer.NPC));
    } else {
      // handle gunner attack type
      if (config.getName() != null && config.getName().contains("Gunner")) {
        AITaskComponent ai = robot.getComponent(AITaskComponent.class);
        if (ai != null) {
          ai.clearTask(); // clear any existing tasks ensure no clashing in tasks priority
          // apply gunner robot tasks
          ai.addTask(new MoveLeftTask(config.getMovementSpeed()));
          ai.addTask(new GunnerAttackTask(config.getAttackRange(), PhysicsLayer.NPC));
        }
      }
    }

    // Special abilities for specific robot types
    if (config.getName() != null && config.getName().contains("Jumper")) {
      robot.getComponent(AITaskComponent.class).addTask(new JumpTask(30f, PhysicsLayer.NPC));
    }

    if (config.getName() != null && config.getName().contains("Bungee")) {
      animator.addAnimation("teleportEnd", 0.1f, Animation.PlayMode.NORMAL);
      animator.addAnimation("teleportEndDamaged", 0.1f, Animation.PlayMode.NORMAL);
      robot.getComponent(AITaskComponent.class).addTask(new BungeeSpawnTask());
    }

    if (config.getName() != null && config.getName().contains("Teleport")) {
      animator.addAnimation("teleportStart", 0.1f, Animation.PlayMode.NORMAL);
      animator.addAnimation("teleportStartDamaged", 0.1f, Animation.PlayMode.NORMAL);
      animator.addAnimation("teleportEnd", 0.1f, Animation.PlayMode.NORMAL);
      animator.addAnimation("teleportEndDamaged", 0.1f, Animation.PlayMode.NORMAL);
      float[] laneYs = discoverLaneYsFromTiles();
      if (laneYs.length >= 2) {
        AITaskComponent ai = robot.getComponent(AITaskComponent.class);
        if (ai != null) {
          ai.addTask(
              new TeleportTask(
                  config.getTeleportCooldownSeconds(),
                  config.getTeleportChance(),
                  config.getMaxTeleports(),
                  laneYs));
        }
      }
    }

    if (config.getName() != null && config.getName().contains("Gunner")) {
      animator.addAnimation("shoot", 0.1f, Animation.PlayMode.NORMAL);
      animator.addAnimation("shootDamaged", 0.1f, Animation.PlayMode.NORMAL);
    }

    // ✅ Add explosion-on-death component for bomber
    if (config.isBomberRobot()) {
      BomberDeathExplodeComponent explodeComp =
          new BomberDeathExplodeComponent(config.getExplosionDamage(), 1.0f);
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

  /**
   * Creates a single preview entity for a given robot type. This is a lightweight entity: only
   * AnimationRenderComponent, no AI/physics/collision.
   *
   * @param robotType the robot type key from the spawn preview
   * @return a simple entity with an animation for display
   */
  public static Entity createPreviewRobot(RobotType robotType) {
    ConfigService configService = getConfigService();
    BaseEnemyConfig config = configService.getEnemyConfig(robotType.get());
    if (config == null) {
      config = configService.getEnemyConfig("standardRobot");
    }

    final String atlasPath = config.getAtlasPath();
    var rs = ServiceLocator.getResourceService();
    AnimationRenderComponent animator =
        new AnimationRenderComponent(rs.getAsset(atlasPath, TextureAtlas.class));

    animator.addAnimation("moveLeft", 0.1f, Animation.PlayMode.LOOP);

    Entity preview = new Entity().addComponent(animator);
    animator.scaleEntity();
    animator.startAnimation("moveLeft");

    return preview;
  }
}
