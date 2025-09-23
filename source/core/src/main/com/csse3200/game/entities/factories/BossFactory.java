package com.csse3200.game.entities.factories;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.npc.RobotAnimationController;
import com.csse3200.game.components.tasks.MoveLeftTask;
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
import net.dermetfan.gdx.physics.box2d.PositionController;
import com.csse3200.game.components.npc.RobotAnimationController;
import com.csse3200.game.components.tasks.MoveLeftTask;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.badlogic.gdx.math.Vector2;


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
    public enum BossTypes{
        SCRAP_TITAN
    }
    private static final BossConfigs configs = FileLoader.readClass(BossConfigs.class,"configs/boss.json");
    /**
     * A basic function to create a specific type of boss depending on the input. make this use
     * constants of some kind. Or EntityConfig classes If an invalid type is given, a standard robot
     * is created
     *
     * @param bossType The type of boss to create
     * @return The created boss
     */
    public static Entity createBossType(BossTypes bossType,Entity target) {
        BaseBossConfig config = null;
        switch (bossType) {
            case SCRAP_TITAN -> config = configs.scrapTitan;
        }
        return createBaseBoss(config,target);
    }
    /**
     * Creates a base robot entity with the common components.
     * @param config The configuration to create the robot from
     * @return The created robot entity
     */
    private static Entity createBaseBoss(BaseBossConfig config,Entity target) {
        AITaskComponent aiComponent = new AITaskComponent();

//        AnimationRenderComponent animator =
//                new AnimationRenderComponent(ServiceLocator.getResourceService().getAsset(config.atlasFilePath, TextureAtlas.class));
//        animator.addAnimation("walk", 0.2f, Animation.PlayMode.LOOP);
//        animator.addAnimation("attack", 0.1f, Animation.PlayMode.LOOP);
//        animator.addAnimation("moveLeftDamaged", 0.1f, Animation.PlayMode.LOOP);
//        animator.addAnimation("attackDamaged", 0.1f, Animation.PlayMode.LOOP);
//        animator.addAnimation("default", 1f, Animation.PlayMode.LOOP);

        ColliderComponent colliderComponent = new ColliderComponent()
                .setLayer(PhysicsLayer.BOSS)
                 .setCollisionFilter(
                        PhysicsLayer.BOSS, (short) (PhysicsLayer.DEFAULT | PhysicsLayer.OBSTACLE | PhysicsLayer.BOSS_PROJECTILE)
                 );


        Entity boss =
                new Entity()
                        .addComponent(new PhysicsComponent())
                        .addComponent(colliderComponent)
                        .addComponent(new HitboxComponent().setLayer(PhysicsLayer.BOSS))
                        .addComponent(new CombatStatsComponent(config.health, config.attack))
                        .addComponent(aiComponent)
                        //.addComponent(animator)
                        .addComponent(new TouchAttackComponent(PhysicsLayer.NPC,1.5f));
//                        boss.setScale(config.scale,config.scale);
                        //animator.startAnimation("default");


        return boss;
    }



}


