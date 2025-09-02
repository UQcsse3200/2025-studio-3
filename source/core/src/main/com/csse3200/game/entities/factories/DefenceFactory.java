package com.csse3200.game.entities.factories;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.DefenceStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.npc.DefenceAnimationController;
import com.csse3200.game.components.tasks.WanderTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenceConfig;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsUtils;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.physics.components.PhysicsMovementComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;


public class DefenceFactory {
    private static final NPCConfigs configs =
        FileLoader.readClass(NPCConfigs.class, "configs/Defences.json");

    /**
     * Creates a base sling shooter entity
     * Returns the entity
     */
    public static Entity createSlingShooter() {
        Entity sigma = createBaseDefender();
        BaseDefenceConfig config = configs.slingshooter;

        AnimationRenderComponent animator =
        new AnimationRenderComponent(
            ServiceLocator.getResourceService()
                .getAsset("images/sling_shooter.atlas", TextureAtlas.class));
        
        animator.addAnimation("idle", 0.1f, Animation.PlayMode.LOOP);
        animator.addAnimation("attack", 0.04f, Animation.PlayMode.LOOP);

        sigma
            .addComponent(new DefenceStatsComponent(config.health, 
                        config.baseAttack, config.type, config.range, config.state,
                        config.attackSpeed, config.critChance))
            .addComponent(animator)
            .addComponent(new DefenceAnimationController());

        sigma.getEvents().trigger("attackStart");

        sigma.getComponent(AnimationRenderComponent.class).scaleEntity();
        return sigma;
    }
    
    public static Entity createBaseDefender() {
        Entity npc =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new ColliderComponent())
            .addComponent(new HitboxComponent().setLayer(PhysicsLayer.NPC));

        PhysicsUtils.setScaledCollider(npc, 0.9f, 0.4f);
        return npc;
    }

    private DefenceFactory() {
        throw new IllegalStateException("Instantiating static util class");
  }
}
