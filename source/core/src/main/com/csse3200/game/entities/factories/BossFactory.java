package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Timer;
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
import java.awt.*;

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
public class BossFactory {
  /**
   * Loads boss config data from JSON. The configs object is populated at class-load time. If the
   * file is missing or deserialization fails, this will be null.
   */
  public enum BossTypes {
    SCRAP_TITAN
  }

  private static final BossConfigs configs =
      FileLoader.readClass(BossConfigs.class, "configs/boss.json");

  /**
   * A basic function to create a specific type of boss depending on the input. make this use
   * constants of some kind. Or EntityConfig classes If an invalid type is given, a standard robot
   * is created
   *
   * @param bossType The type of boss to create
   * @return The created boss
   */
  public static Entity createBossType(BossTypes bossType) {
    BaseBossConfig config = null;
    switch (bossType) {
      case SCRAP_TITAN -> config = configs.scrapTitan;
    }
    return createBaseBoss(config);
  }

  /**
   * Creates a base robot entity with the common components.
   *
   * @param config The configuration to create the robot from
   * @return The created robot entity
   */
  private static Entity createBaseBoss(BaseBossConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("Robot config cannot be null");
    }
    AITaskComponent aiComponent =
        new AITaskComponent()
            .addTask(new MoveLeftTask(config.speed))
            .addTask(new RobotAttackTask(90f, PhysicsLayer.NPC));
    AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService().getAsset(config.atlasFilePath, TextureAtlas.class));

    animator.addAnimation("moveLeft", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attack", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("moveLeftDamaged", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("attackDamaged", 0.1f, Animation.PlayMode.LOOP);
    animator.addAnimation("default", 1f, Animation.PlayMode.LOOP);

    ColliderComponent colliderComponent =
        new ColliderComponent()
            .setCollisionFilter(
                PhysicsLayer.BOSS,
                (short) (PhysicsLayer.DEFAULT | PhysicsLayer.NPC | PhysicsLayer.OBSTACLE))
            .setFriction(0f);

    Entity boss =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new PhysicsMovementComponent())
            .addComponent(colliderComponent)
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.BOSS))
            .addComponent(new CombatStatsComponent(config.health, config.attack))
            .addComponent(aiComponent)
            .addComponent(new RobotAnimationController())
            .addComponent(new HitMarkerComponent())
            .addComponent(new TouchAttackComponent(PhysicsLayer.NPC, 0f))
            .addComponent(animator);

    animator.scaleEntity();
    animator.startAnimation("default");

    TouchAttackComponent touch = boss.getComponent(TouchAttackComponent.class);
    RobotAnimationController controller = boss.getComponent(RobotAnimationController.class);
    if (touch != null && controller != null) {
      boss.getEvents()
          .addListener(
              "attack",
              target -> {
                animator.startAnimation("attack");

                Timer.schedule(
                    new Timer.Task() {
                      @Override
                      public void run() {
                        animator.startAnimation("default");
                      }
                    },
                    0.6f);
              });
    }

    boss.setScale(boss.getScale().x * config.scale, boss.getScale().y * config.scale);
    return boss;
  }
}
