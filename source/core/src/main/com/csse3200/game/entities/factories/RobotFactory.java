package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.npc.RobotAnimationController;
import com.csse3200.game.components.tasks.MoveLeftTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.FastRobotConfig;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.entities.configs.StandardRobotConfig;
import com.csse3200.game.files.FileLoader;
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
     * Creates a standard robot
     * @return the created robot entity
     */
    public static Entity createStandardRobot() {
        // Ideally this would use NPCConfigs.java but I can't figure out how.
        BaseEntityConfig config = new StandardRobotConfig();

        // This creates pretty much everything except the animation
        Entity robot = createBaseRobot(config);

        //Animation
        final String atlasPath = "images/robot_placeholder.atlas";
        var rs = ServiceLocator.getResourceService();

        AnimationRenderComponent animator = new AnimationRenderComponent(
                rs.getAsset(atlasPath, TextureAtlas.class));

        animator.addAnimation("chill", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("angry", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("default", 1f, Animation.PlayMode.NORMAL);

        robot.addComponent(animator);

        // We could also do
        // .addComponent(new RobotAnimationController())
        // but that isn't really implemented

        animator.scaleEntity();
        animator.startAnimation("chill"); // start an animation
        // make a bit larger
        robot.setScale(robot.getScale().x * 4f, robot.getScale().y * 4f);

        return robot;
    }


    public static Entity createFastRobot() {
        // Ideally this would use NPCConfigs.java but I can't figure out how.
        BaseEntityConfig config = new FastRobotConfig();

        // This creates pretty much everything except the animation
        Entity robot = createBaseRobot(config);

        //Animation
        final String atlasPath = "images/robot_placeholder.atlas";
        var rs = ServiceLocator.getResourceService();

        AnimationRenderComponent animator = new AnimationRenderComponent(
                rs.getAsset(atlasPath, TextureAtlas.class));

        animator.addAnimation("chill", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("angry", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("default", 1f, Animation.PlayMode.NORMAL);

        robot.addComponent(animator);

        animator.scaleEntity();
        animator.startAnimation("angry"); // angry to differentiate it from the standard robot
        // make a bit larger
        robot.setScale(robot.getScale().x * 4f, robot.getScale().y * 4f);

        return robot;
    }

    /**
     * Initialises a Base Robot containing the features shared by all robots
     * (e.g. combat stats, movement left, Physics, Hitbox)
     * This robot can be used as a base entity by more specific robots.
     *
     * @param config A config file that contains the robot's stats.
     * @return A robot entity. Note that it does not have an animator component.
     */
    private static Entity createBaseRobot(BaseEntityConfig config) {

        AITaskComponent aiComponent =
                new AITaskComponent()
                        .addTask(new MoveLeftTask(config.movementSpeed));

        return new Entity()
                .addComponent(new PhysicsComponent())
                .addComponent(new PhysicsMovementComponent())
                .addComponent(new ColliderComponent())
                .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC))
                .addComponent(new CombatStatsComponent(config.health, config.baseAttack))
                .addComponent(aiComponent);


        // The original NPCFactory had:
        // PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
        // and also .addComponent(new TouchAttackComponent(PhysicsLayer.PLAYER, 1.5f))
        // I don't think we need that but I'm putting it here for reference
    }
}
